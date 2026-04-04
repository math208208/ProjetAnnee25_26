package jeu.environnement;

import jeu.Jeu;
import jeu.joueur.Inventaire;
import jeu.joueur.Joueur;
import jeu.objets.Cle;
import jeu.objets.ObjetMaudit;

public class Cheminee extends Conteneur {
	private boolean feuAllume;

    public Cheminee(String nom) {
        super(nom, false, null, false);
        this.feuAllume = false;
    }

    public boolean verifierPresenceBoisAllumettes(Inventaire inv) {
        return inv.possedeBois() && inv.possedeAllumettes(); 
    }

    public boolean allumerFeu(Joueur joueur) {
        if (verifierPresenceBoisAllumettes(joueur.getInventaire())) {
            joueur.getInventaire().retire("bois");
            joueur.getInventaire().retire("allumettes");
            this.feuAllume = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        return false; 
    }


}
