package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Armoire extends Conteneur {

    public Armoire(String nom, boolean estVerrouille, Cle cleRequise) {
        super(nom, estVerrouille, cleRequise, false); 
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        if (this.estVerrouille && this.cleRequise != null && cle.getNom().equalsIgnoreCase(this.cleRequise.getNom())) {
            this.estVerrouille = false;
            this.estOuvert = true;
            return true;
        }
        return false;
    }
}
