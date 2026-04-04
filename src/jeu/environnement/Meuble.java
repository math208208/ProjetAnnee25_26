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

    public void utilise(Joueur joueur, Jeu jeu) {
        // La logique sera gérée par la classe Jeu selon le TypeMeuble
    }
}
