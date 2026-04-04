package jeu.joueur;

import java.util.HashSet;
import java.util.Set;

public class Joueur {
	private String pseudo;
	private int vies;
	private Inventaire inventaire;
	private Set<String> zonesVisitees;

	// Constructeur pour initialiser un nouveau joueur
	public Joueur(String pseudo) {
		this.pseudo = pseudo;
		this.vies = 3; // Le joueur commence avec 3 vies 
		this.inventaire = new Inventaire();
		this.zonesVisitees = new HashSet<>();
	}

	public void perdreVie() {
		if (this.vies > 0) {
			this.vies--;
		}
	}

	public void diminuerPV(int montant) {
		this.vies -= montant;
		if (this.vies < 0) {
			this.vies = 0;
		}
	}

	public boolean possede(String nomObjet) {
		// On délègue la vérification à l'inventaire
		return this.inventaire.possede(nomObjet); 
	}

	// --- Getters et Setters ---
	
	public String getPseudo() {
		return pseudo;
	}
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	public int getVies() {
		return vies;
	}
	public void setVies(int vies) {
		this.vies = vies;
	}
	public Inventaire getInventaire() {
		return inventaire;
	}
	public void setInventaire(Inventaire inventaire) {
		this.inventaire = inventaire;
	}
	public Set<String> getZonesVisitees() {
		return zonesVisitees;
	}
	public void setZonesVisitees(Set<String> zonesVisitees) {
		this.zonesVisitees = zonesVisitees;
	}
    
    
}
