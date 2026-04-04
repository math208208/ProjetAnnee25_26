package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class Echelle extends ObjetJeu {
	public Echelle(String nom, String description) {
		super(nom, description, false);
	}

	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("L'échelle est encombrante. Vous n'avez pas besoin de l'utiliser manuellement : possédez-la simplement dans votre sac lorsque vous tentez de quitter la cave.");
	}
}