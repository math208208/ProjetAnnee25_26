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
        return inv.possedeBois() && inv.possedeAllumettes(); // Condition de purification [cite: 78]
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

    public void brulerFragment(ObjetMaudit objet, Jeu jeu) {
        if (feuAllume && objet.estFragment()) {
            // La destruction sera gérée par la méthode bruler() dans Jeu [cite: 71, 80]
        }
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        return false; 
    }


}
