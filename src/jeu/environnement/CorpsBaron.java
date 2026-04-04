package jeu.environnement;

import jeu.enigmes.Enigme;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

public class CorpsBaron extends Conteneur {
	private boolean enigmeResolue;
    private Enigme enigme;

    public CorpsBaron(String nom, Enigme enigme) {
        super(nom, true, null, false); // "Verrouillé" par l'énigme
        this.enigme = enigme;
        this.enigmeResolue = false;
    }

    public Enigme declencherEnigme() {
        return this.enigme;
    }

    @Override
    public boolean ouvre(Joueur joueur) {
        // L'ouverture déclenche une énigme [cite: 62]
        if (!enigmeResolue) {
            return false; 
        }
        this.estOuvert = true;
        return true;
    }

    public void resoudreEnigme() {
        this.enigmeResolue = true;
        this.estVerrouille = false;
    }

    @Override
    public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
        return false;
    }
}