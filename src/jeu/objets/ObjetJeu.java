package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public abstract class ObjetJeu {
	private String nom;
	private String description;
	private boolean estFragment;

	public ObjetJeu(String nom, String description, boolean estFragment) {
		this.nom = nom;
		this.description = description;
		this.estFragment = estFragment;
	}

	public String getNom() {
		return nom;
	}

	public String getDescription() {
		return description;
	}


	public boolean estFragment() {
		return estFragment;
	} 

	@Override
	public String toString() {
		return nom;
	}

	public abstract void utiliser(Joueur joueur, Jeu jeu);
}
