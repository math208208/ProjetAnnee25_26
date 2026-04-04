package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class Cle extends ObjetJeu {
	
	private String cible; // Ex: "Bureau", "Coffre Chambre 1"

	public Cle(String nom, String description, String cible) {
		super(nom, description, false);
		this.cible = cible;
	}

	public String getCible() {
		return cible;
	}

	public boolean correspondA(String nomConteneur) {
		return this.cible.equalsIgnoreCase(nomConteneur);
	}

	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("C'est une clé permettant d'ouvrir : " + cible + ". Utilisez la commande OUVRIR lorsque vous êtes face au bon meuble.");
	}
}
