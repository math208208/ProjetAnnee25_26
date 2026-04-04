package jeu.joueur;

import java.time.LocalDateTime;

public class ProfilJoueur {
    private String pseudo;
    private LocalDateTime dateCreation;
    private int partiesGagnees;

    public ProfilJoueur(String pseudo) {
        this.pseudo = pseudo;
        this.dateCreation = LocalDateTime.now();
        this.partiesGagnees = 0;
    }

    public String getPseudo() { return pseudo; }
    public LocalDateTime getDateCreation() { return dateCreation; }
    public int getPartiesGagnees() { return partiesGagnees; }

    public void incrementeVictoires() {
        this.partiesGagnees++;
    }
}