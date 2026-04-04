package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class MorceauBois extends ObjetJeu {
	public MorceauBois(String nom, String description) {
		super(nom, description, false);
	}

	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("Ce bois est parfait pour la cheminée. Rendez-vous dans le salon et utilisez la commande ALLUMER_FEU.");
	}
}