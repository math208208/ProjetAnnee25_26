package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Coffre extends Conteneur {
	public Coffre(String nom, Cle bonneCle) {
		super(nom, true, bonneCle, true);
	}

	@Override
	public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {

		if (this.cleRequise != null && cle.getNom().equalsIgnoreCase(this.cleRequise.getNom())) {
			this.estVerrouille = false;
			this.estOuvert = true;
			return true;
		}
		return false;
	}
}