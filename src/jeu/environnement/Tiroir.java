package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Tiroir extends Conteneur {
	public Tiroir(String nom) {
		// Le tiroir de la cuisine est ouvert et non piégé [cite: 427]
		super(nom, false, null, false);
	}

	@Override
	public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
		return false; // N'a pas de serrure
	}
}
