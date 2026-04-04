package jeu.objets;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class ObjetMaudit extends ObjetJeu {
	
	private TypeFragment typeFragment;

	// Le constructeur demande maintenant le TypeFragment
	public ObjetMaudit(String nom, String description, TypeFragment typeFragment) {
		super(nom, description, true); // C'est toujours un fragment (true)
		this.typeFragment = typeFragment;
	}

	public TypeFragment getTypeFragment() { 
		return this.typeFragment; 
	}
	
	@Override
	public void utiliser(Joueur joueur, Jeu jeu) {
		jeu.getGui().afficher("Vous ressentez une aura glaciale émaner de cet objet. Il doit être purifié dans la cheminée du salon avec la commande BRULER.");
	}
}
