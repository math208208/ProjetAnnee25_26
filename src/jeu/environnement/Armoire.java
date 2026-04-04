package jeu.environnement;

import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class Armoire extends Conteneur {
	// On passe l'état de verrouillage au constructeur car il change selon les parties
    public Armoire(String nom, boolean estVerrouille, Cle cleRequise) {
        super(nom, estVerrouille, cleRequise, false); // L'armoire n'est pas piégée (false)
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        // Vérifie si l'armoire est bien verrouillée, s'il faut une clé, et si le nom correspond
        if (this.estVerrouille && this.cleRequise != null && cle.getNom().equalsIgnoreCase(this.cleRequise.getNom())) {
            this.estVerrouille = false;
            this.estOuvert = true;
            return true;
        }
        return false;
    }
}
