package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Bureau extends Conteneur {
	public Bureau(String nom, boolean estVerrouille, Cle cleRequise) {
        super(nom, estVerrouille, cleRequise, false); // Le bureau nécessite une clé mais n'est pas piégé [cite: 63]
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        if (this.estVerrouille && this.cleRequise != null && cle.getNom().equalsIgnoreCase(this.cleRequise.getNom())) {
            this.estVerrouille = false;
            this.estOuvert = true;
            return true;
        }
        return false;
    }


}
