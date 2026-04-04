package jeu.joueur;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeu.objets.ObjetJeu;

public class Inventaire {

	private List<ObjetJeu> objets;
	private int capaciteMax = 6; 

	public Inventaire() {
		this.objets = new ArrayList<>();
	}

	public boolean ajoute(ObjetJeu objet) {
		if (verifierCapacite()) {
			return this.objets.add(objet);
		}
		return false;
	}

	public boolean verifierCapacite() {
		return this.objets.size() < this.capaciteMax;
	}

	public boolean estPlein() {
		return this.objets.size() >= this.capaciteMax;
	}

	public boolean estVide() {
		return this.objets.isEmpty();
	}

	public ObjetJeu retire(String nomObjet) {
		Iterator<ObjetJeu> it = this.objets.iterator();
		while (it.hasNext()) {
			ObjetJeu obj = it.next();
			if (obj.getNom().equalsIgnoreCase(nomObjet)) {
				it.remove(); 
				return obj;  
			}
		}
		return null; 
	}

	public boolean possedeBois() {
		return possede("bois") || possede("morceaux de bois");
	}

	public boolean possedeAllumettes() {
		return possede("allumettes");
	}

	public boolean possede(String nomObjet) {
		for (ObjetJeu obj : this.objets) {
			if (obj.getNom().equalsIgnoreCase(nomObjet)) {
				return true;
			}
		}
		return false;
	}

	public String listerObjets() {
		if (estVide()) {
			return "Votre sac à dos est vide.";
		}
		StringBuilder sb = new StringBuilder();
		for (ObjetJeu obj : this.objets) {
			sb.append("- ").append(obj.getNom()).append("\n");
		}
		return sb.toString();
	}

	
	public List<ObjetJeu> getObjets() {
		return objets;
	}
	public void setObjets(List<ObjetJeu> objets) {
		this.objets = objets;
	}
	public int getCapaciteMax() {
		return capaciteMax;
	}
	public void setCapaciteMax(int capaciteMax) {
		this.capaciteMax = capaciteMax;
	}
    
}
