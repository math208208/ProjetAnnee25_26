package jeu.joueur;

import java.util.HashSet;
import java.util.Set;

public class Joueur {
	private String pseudo;
	private int vies;
	private Inventaire inventaire;
	private Set<String> zonesVisitees;

	public Joueur(String pseudo) {
		this.pseudo = pseudo;
		this.vies = 3; 
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
		return this.inventaire.possede(nomObjet); 
	}

	
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
