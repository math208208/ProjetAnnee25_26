package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class MedaillonMagique extends ObjetJeu {
	public MedaillonMagique(String nom, String description) {
		super(nom, description, false);
	}

	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("Le médaillon palpite doucement... Allez dans la Salle de Bain et utilisez la commande MIROIR pour l'activer.");
	}
}