package jeu.sauvegarde;

import java.util.HashMap;
import java.util.Map;
import jeu.joueur.ProfilJoueur;

public class GestionnaireProfils {
    private Map<String, ProfilJoueur> profils;

    public GestionnaireProfils() {
        this.profils = new HashMap<>();
    }

    public ProfilJoueur creerProfil(String pseudo) {
        // Crée un nouveau profil et le stocke en mémoire
        ProfilJoueur nouveauProfil = new ProfilJoueur(pseudo);
        profils.put(pseudo, nouveauProfil);
        return nouveauProfil;
    }

    public ProfilJoueur authentifier(String pseudo) {
        // Cherche le profil dans la liste. 
        // Si c'est un nouveau joueur, la logique du Jeu devrait appeler creerProfil()
        return profils.get(pseudo);
    }

    public void sauvegarderProfilsJSON() {
        // Facultatif : si tu as besoin de sauvegarder une liste globale de TOUS les profils
        // (Par exemple un fichier "tous_les_joueurs.json" pour gérer des statistiques globales)
    }
}