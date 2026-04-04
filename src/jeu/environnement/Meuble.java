package jeu.environnement;

import jeu.Jeu;
import jeu.joueur.Joueur;

public class Meuble {
	private String nom;
    private TypeMeuble type;

    public Meuble(String nom, TypeMeuble type) {
        this.nom = nom;
        this.type = type;
    }

    public String getNom() {
        return nom;
    }

    public TypeMeuble getType() {
        return type;
    }

}
