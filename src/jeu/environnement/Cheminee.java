package jeu.environnement;

import jeu.joueur.Inventaire;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Cheminee extends Conteneur {
	private boolean feuAllume;

    public Cheminee(String nom) {
        super(nom, false, null, false);
        feuAllume = false;
    }

    public boolean verifierPresenceBoisAllumettes(Inventaire inv) {
        return inv.possedeBois() && inv.possedeAllumettes(); 
    }

    public boolean allumerFeu(Joueur joueur) {
        if (verifierPresenceBoisAllumettes(joueur.getInventaire())) {
            joueur.getInventaire().retire("bois");
            joueur.getInventaire().retire("allumettes");
            feuAllume = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        return false; 
    }

	public boolean isFeuAllume() {
		return feuAllume;
	}

	public void setFeuAllume(boolean feuAllume) {
		this.feuAllume = feuAllume;
	}

}
