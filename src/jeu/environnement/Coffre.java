package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Coffre extends Conteneur {
	public Coffre(String nom, Cle bonneCle) {
		// Les coffres des chambres sont verrouillés et piégés [cite: 105, 423]
		super(nom, true, bonneCle, true);
	}

	@Override
	public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
		// On vérifie que le nom de la clé (ex: cle_coffre_1) correspond EXACTEMENT à la
		// clé requise par CE coffre
		if (this.cleRequise != null && cle.getNom().equalsIgnoreCase(this.cleRequise.getNom())) {
			this.estVerrouille = false;
			this.estOuvert = true;
			return true;
		}
		return false;
	}
}