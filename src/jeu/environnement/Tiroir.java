package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Tiroir extends Conteneur {
	public Tiroir(String nom) {
		super(nom, false, null, false);
	}

	@Override
	public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
		return false; 
	}
}
