package jeu.environnement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeu.joueur.Joueur;
import jeu.objets.Cle;
import jeu.objets.ObjetJeu;

public abstract class Conteneur {
	protected String nom;
    protected boolean estOuvert;
    protected boolean estVerrouille;
    protected Cle cleRequise;
    protected List<ObjetJeu> contenu;
    protected boolean estPiege;

    public Conteneur(String nom, boolean estVerrouille, Cle cleRequise, boolean estPiege) {
        this.nom = nom;
        this.estOuvert = false;
        this.estVerrouille = estVerrouille;
        this.cleRequise = cleRequise;
        this.estPiege = estPiege;
        this.contenu = new ArrayList<>();
    }

    public String getNom() { return this.nom; }
    public boolean estVerrouille() { return this.estVerrouille; }
    public boolean estOuvert() { return this.estOuvert; }
    public List<ObjetJeu> getContenu() { return this.contenu; }

    public boolean ouvre(Joueur joueur) {
        if (estVerrouille) {
            return false;
        }
        estOuvert = true;
        return true;
    }

    public void ajouteObjet(ObjetJeu objet) {
        if (objet != null) {
            this.contenu.add(objet);
        }
    }

    public ObjetJeu retireObjet(String nomObjet) {
        Iterator<ObjetJeu> it = contenu.iterator();
        while(it.hasNext()) {
            ObjetJeu obj = it.next();
            if(obj.getNom().equalsIgnoreCase(nomObjet)) {
                it.remove();
                return obj;
            }
        }
        return null;
    }

    public void declenchePiege(Joueur joueur) {
        if (estPiege) {
            joueur.perdreVie();
        }
    }
    public void setVerrouille(boolean estVerrouille) {
        this.estVerrouille = estVerrouille;
    }

    public void setCleRequise(Cle cleRequise) {
        this.cleRequise = cleRequise;
    }
    
    public abstract boolean deverrouillerAvecCle(Cle cle, Joueur joueur);

	public void setEstOuvert(boolean estOuvert) {
		this.estOuvert = estOuvert;
	}


	
	
}
