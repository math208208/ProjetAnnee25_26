package jeu.sauvegarde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeu.Jeu;
import jeu.environnement.Conteneur;
import jeu.environnement.Zone;
import jeu.objets.Allumettes;
import jeu.objets.Cle;
import jeu.objets.Echelle;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetJeu;
import jeu.objets.ObjetMaudit;
import jeu.objets.TypeFragment;

public class EtatPartie {
    private String pseudo;
    private int vies;
    private List<String> inventaire;
    private String zoneActuelle;
    private int fragmentsDetruits;
    private boolean eclairageActif;
    private boolean chemineActif;
    
    private Map<String, String> positionsObjets = new HashMap<>();
    private List<String> etatsConteneurs = new ArrayList<>();
    
    public EtatPartie() {}

    public EtatPartie capturer(Jeu jeu) {
        this.pseudo = jeu.getJoueur().getPseudo();
        this.vies = jeu.getJoueur().getVies();
        this.zoneActuelle = jeu.getZoneCourante().getNom();
        this.fragmentsDetruits = jeu.getFragmentsDetruits();
        this.eclairageActif = jeu.isEclairageActif();
        this.chemineActif = jeu.isChemineActif();
        
        if (this.inventaire == null) this.inventaire = new ArrayList<>();
        this.inventaire.clear();
        for (ObjetJeu obj : jeu.getJoueur().getInventaire().getObjets()) {
            this.inventaire.add(obj.getNom());
        }
        
        this.positionsObjets.clear();
        this.etatsConteneurs.clear(); 
        
        for (Zone z : jeu.getManoir().getToutesLesZones()) {
            // Objets au sol
            for (ObjetJeu obj : z.getObjetsPresents()) {
                positionsObjets.put(obj.getNom(), "ZONE:" + z.getNom());
            }
            
            for (Conteneur c : z.getConteneurs()) {
                // Objets dans les meubles
                for (ObjetJeu obj : c.getContenu()) {
                    positionsObjets.put(obj.getNom(), "CONTENEUR:" + c.getNom() + ":" + z.getNom());
                }
                // Sauvegarde de l'état du meuble (NomZone|NomMeuble|Ouvert|Verrouille)
                etatsConteneurs.add(z.getNom() + "|" + c.getNom() + "|" + c.estOuvert() +"|" + c.estVerrouille());
            }
        }
        return this;
    }

    public void restaurer(Jeu jeu) {
        // 1. Restaurer les statistiques et l'environnement global
        jeu.getJoueur().setVies(this.vies);
        jeu.setFragmentsDetruits(this.fragmentsDetruits);
        jeu.setEclairageActif(this.eclairageActif);
        jeu.setChemineActif(this.chemineActif);

        // 2. Positionner le joueur dans la bonne zone
        Zone zoneSauvee = jeu.getManoir().obtientZone(this.zoneActuelle);
        if (zoneSauvee != null) jeu.setZoneCourante(zoneSauvee);

        // 3. NETTOYAGE : Récupérer toutes les instances d'objets créées par initialiserObjets()
        // On les stocke dans une Map pour pouvoir les replacer précisément par leur nom.
        Map<String, ObjetJeu> poolObjets = new HashMap<>();
        for (Zone z : jeu.getManoir().getToutesLesZones()) {
            // On vide le sol
            List<ObjetJeu> sol = new ArrayList<>(z.getObjetsPresents());
            for (ObjetJeu o : sol) {
                poolObjets.put(o.getNom(), z.retireObjet(o.getNom()));
            }

            // On vide les meubles
            for (Conteneur c : z.getConteneurs()) {
                List<ObjetJeu> contenu = new ArrayList<>(c.getContenu());
                for (ObjetJeu o : contenu) {
                    poolObjets.put(o.getNom(), c.retireObjet(o.getNom()));
                }
            }
        }
        forcerSerrures(jeu, poolObjets);
        // 4. RESTAURER L'INVENTAIRE
        jeu.getJoueur().getInventaire().getObjets().clear();
        if (this.inventaire != null) {
            for (String nom : inventaire) {
                ObjetJeu obj = poolObjets.remove(nom);
                // Sécurité : si le randomiseur n'a pas généré cet objet spécifique cette fois-ci
                if (obj == null) obj = creerObjetManquant(nom, jeu); 
                jeu.getJoueur().getInventaire().ajoute(obj);
            }
        }

        // 5. REPOSITIONNER LES OBJETS (SOL ET MEUBLES)
        if (this.positionsObjets != null) {
            for (Map.Entry<String, String> entree : positionsObjets.entrySet()) {
                String nomObj = entree.getKey();
                String lieu = entree.getValue();
                
                ObjetJeu obj = poolObjets.remove(nomObj);
                if (obj == null) obj = creerObjetManquant(nomObj, jeu);

                if (lieu.startsWith("ZONE:")) {
                    String nomZone = lieu.substring(5);
                    jeu.environnement.Zone z = jeu.getManoir().obtientZone(nomZone);
                    if (z != null) z.ajouteObjet(obj);
                } else if (lieu.startsWith("CONTENEUR:")) {
                    String[] parts = lieu.split(":"); // CONTENEUR:NomMeuble:NomZone
                    jeu.environnement.Zone z = jeu.getManoir().obtientZone(parts[2]);
                    if (z != null) {
                        Conteneur c = z.getConteneur(parts[1]);
                        if (c != null) c.ajouteObjet(obj);
                    }
                }
            }
        }

        // 6. RESTAURER L'ÉTAT DES MEUBLES (Ouvert / Verrouillé)
        if (this.etatsConteneurs != null) {
            for (String info : this.etatsConteneurs) {
                String[] parts = info.split("\\|");
                if (parts.length == 4) {
                    jeu.environnement.Zone z = jeu.getManoir().obtientZone(parts[0]);
                    if (z != null) {
                        jeu.environnement.Conteneur c = z.getConteneur(parts[1]);
                        if (c != null) {
                            c.setEstOuvert(Boolean.parseBoolean(parts[2]));
                            c.setVerrouille(Boolean.parseBoolean(parts[3]));
                        }
                    }
                }
            }
        }
    }

    private void forcerSerrures(Jeu jeu, Map<String, ObjetJeu> poolObjets) {
        Zone bureau = jeu.getManoir().obtientZone("bureau");
        if (bureau != null) {
            if (bureau.getConteneur("Bureau") != null) bureau.getConteneur("Bureau").setCleRequise((jeu.objets.Cle) recupererOuCreerCle("cle_bureau", "Bureau", poolObjets));
            if (bureau.getConteneur("Armoire") != null) bureau.getConteneur("Armoire").setCleRequise((jeu.objets.Cle) recupererOuCreerCle("cle_armoire", "Armoire", poolObjets));
        }
        Zone ch1 = jeu.getManoir().obtientZone("chambre1");
        if (ch1 != null && ch1.getConteneur("Coffre") != null) ch1.getConteneur("Coffre").setCleRequise((jeu.objets.Cle) recupererOuCreerCle("cle_coffre_1", "Coffre", poolObjets));
        
        Zone ch2 = jeu.getManoir().obtientZone("chambre2");
        if (ch2 != null && ch2.getConteneur("Coffre") != null) ch2.getConteneur("Coffre").setCleRequise((jeu.objets.Cle) recupererOuCreerCle("cle_coffre_2", "Coffre", poolObjets));
    }
    
    private ObjetJeu recupererOuCreerCle(String nomCle, String cible, Map<String, ObjetJeu> poolObjets) {
        // 1. Si la clé a été générée par le Randomiseur (et aspirée dans le pool), on l'utilise
        if (poolObjets.containsKey(nomCle)) {
            return poolObjets.get(nomCle);
        }
        // 2. Sinon, on crée la clé PARFAITE et on la glisse dans le pool pour que l'inventaire l'attrape !
        jeu.objets.Cle nouvelleCle = new jeu.objets.Cle(nomCle, "Clé de " + cible, cible);
        poolObjets.put(nomCle, nouvelleCle);
        return nouvelleCle;
    }
    /**
     * Recrée un objet si le randomiseur ne l'a pas généré au lancement.
     */
    private ObjetJeu creerObjetManquant(String nom, Jeu jeu) {
        if (nom.equals("echelle")) return new Echelle("echelle", "Une échelle");
        if (nom.equals("bois")) return new MorceauBois("bois", "Un morceau de bois");
        if (nom.equals("allumettes")) return new Allumettes("allumettes", "Des allumettes");
        if (nom.startsWith("cle_")) return new Cle(nom, "Une clé", "Porte/Coffre");
        
        // Pour les fragments maudits
        try {
            TypeFragment type = TypeFragment.valueOf(nom.toUpperCase());
            return new ObjetMaudit(nom, "Un fragment d'âme", type);
        } catch (Exception e) {
            return new Cle(nom, "Objet inconnu", "Inconnu");
        }
    }

    // --- GETTERS & SETTERS ---
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public int getVies() { return vies; }
    public void setVies(int vies) { this.vies = vies; }
    public List<String> getInventaire() { return inventaire; }
    public void setInventaire(List<String> inventaire) { this.inventaire = inventaire; }
    public String getZoneActuelle() { return zoneActuelle; }
    public void setZoneActuelle(String zoneActuelle) { this.zoneActuelle = zoneActuelle; }
    public int getFragmentsDetruits() { return fragmentsDetruits; }
    public void setFragmentsDetruits(int fragmentsDetruits) { this.fragmentsDetruits = fragmentsDetruits; }
    public boolean isEclairageActif() { return eclairageActif; }
    public void setEclairageActif(boolean eclairageActif) { this.eclairageActif = eclairageActif; }
    public Map<String, String> getPositionsObjets() { return positionsObjets; }
    public void setPositionsObjets(Map<String, String> positionsObjets) { this.positionsObjets = positionsObjets; }
    public List<String> getEtatsConteneurs() { return etatsConteneurs; }
    public void setEtatsConteneurs(List<String> etatsConteneurs) { this.etatsConteneurs = etatsConteneurs; }
    public boolean isChemineActif() { return chemineActif; }
    public void setChemineActif(boolean chemineActif) { this.chemineActif = chemineActif; }
}