package jeu.sauvegarde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeu.Jeu;
import jeu.objets.ObjetJeu;

public class EtatPartie {
    private String pseudo;
    private int vies;
    private List<String> inventaire;
    private String zoneActuelle;
    private List<String> fragmentsADetruire;
    private int fragmentsDetruits;
    private boolean eclairageActif;
    private Map<String, String> positionsObjets;

    // Un constructeur vide est OBLIGATOIRE pour que Jackson puisse recréer l'objet depuis le JSON
    public EtatPartie() {
        this.inventaire = new ArrayList<>();
        this.fragmentsADetruire = new ArrayList<>();
        this.positionsObjets = new HashMap<>();
    }

    public EtatPartie capturer(Jeu jeu) {
        // Attention: Tu devras créer ces getters (getJoueur(), getZoneCourante(), etc.) dans ta classe Jeu !
        /*
        this.pseudo = jeu.getJoueur().getPseudo();
        this.vies = jeu.getJoueur().getVies();
        
        for (ObjetJeu obj : jeu.getJoueur().getInventaire().getObjets()) {
            this.inventaire.add(obj.getNom());
        }
        
        this.zoneActuelle = jeu.getZoneCourante().getNom();
        this.fragmentsDetruits = jeu.getFragmentsDetruits();
        this.eclairageActif = jeu.isEclairageActif();
        */
        return this;
    }

    public void restaurer(Jeu jeu) {
        // Logique inverse : on prend les valeurs de cette classe pour écraser celles du Jeu actuel
        /*
        jeu.getJoueur().setVies(this.vies);
        jeu.setFragmentsDetruits(this.fragmentsDetruits);
        // etc...
        */
    }

    // --- GETTERS & SETTERS (Obligatoires pour la librairie Jackson) ---
    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public int getVies() { return vies; }
    public void setVies(int vies) { this.vies = vies; }
    public List<String> getInventaire() { return inventaire; }
    public void setInventaire(List<String> inventaire) { this.inventaire = inventaire; }
    public String getZoneActuelle() { return zoneActuelle; }
    public void setZoneActuelle(String zoneActuelle) { this.zoneActuelle = zoneActuelle; }
    public List<String> getFragmentsADetruire() { return fragmentsADetruire; }
    public void setFragmentsADetruire(List<String> fragmentsADetruire) { this.fragmentsADetruire = fragmentsADetruire; }
    public int getFragmentsDetruits() { return fragmentsDetruits; }
    public void setFragmentsDetruits(int fragmentsDetruits) { this.fragmentsDetruits = fragmentsDetruits; }
    public boolean isEclairageActif() { return eclairageActif; }
    public void setEclairageActif(boolean eclairageActif) { this.eclairageActif = eclairageActif; }
    public Map<String, String> getPositionsObjets() { return positionsObjets; }
    public void setPositionsObjets(Map<String, String> positionsObjets) { this.positionsObjets = positionsObjets; }
}