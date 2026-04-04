package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class Allumettes extends ObjetJeu {
	public Allumettes(String nom, String description) {
		super(nom, description, false); // N'est jamais un fragment
	}
	
	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("Les allumettes ne s'utilisent pas seules. Rendez-vous dans le salon et utilisez la commande ALLUMER_FEU.");
	}
}
