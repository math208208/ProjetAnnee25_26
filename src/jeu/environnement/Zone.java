package jeu.environnement;

import java.util.Map;

import jeu.Direction;
import jeu.objets.ObjetJeu;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Représente une zone du jeu.
 * <p>
 * Chaque zone possède une description, une image et des sorties vers d'autres
 * zones identifiées par des directions.
 */
public class Zone {
	private String nom;
	private String description;
	private String nomImage;
	private final Map<Direction, Zone> sorties;
	private final Map<String, Zone> sortiesCachees;
	private List<ObjetJeu> objetsPresents;
	private List<Conteneur> conteneurs;
	private boolean eclairee;

	public Zone(String nom, String description, String image, boolean eclairee) {
		this.nom = nom;
		this.description = description;
		this.nomImage = image;
		this.eclairee = eclairee;
		this.sorties = new EnumMap<>(Direction.class);
		this.sortiesCachees = new HashMap<>();
		this.objetsPresents = new ArrayList<>();
		this.conteneurs = new ArrayList<>();
	}

	public String getNom() {
		return nom;
	}

	public String nomImage() {
		return nomImage;
	}

	public boolean estEclairee() {
		return eclairee;
	}

	public void setEclairee(boolean eclairee) {
		this.eclairee = eclairee;
	}

	public void ajouteSortie(Direction direction, Zone zone) {
		sorties.put(direction, zone);
	}

	public void ajouteSortieCachee(String action, Zone zone) {
		sortiesCachees.put(action, zone);
	}

	public Zone obtientSortie(Direction direction) {
		return sorties.get(direction);
	}

	public void revelerSortieCachee(String action, Direction direction) {
		Zone zoneCachee = sortiesCachees.get(action);
		if (zoneCachee != null) {
			ajouteSortie(direction, zoneCachee);
		}
	}

	public void ajouteConteneur(Conteneur c) {
		this.conteneurs.add(c);
	}

	public Conteneur getConteneur(String nomConteneur) {
		for (Conteneur c : conteneurs) {
			if (c.getNom().equalsIgnoreCase(nomConteneur))
				return c;
		}
		return null;
	}

	public void ajouteObjet(ObjetJeu objet) {
		if (objet != null)
			this.objetsPresents.add(objet);
	}

	public ObjetJeu retireObjet(String nomObjet) {
		Iterator<ObjetJeu> it = objetsPresents.iterator();
		while (it.hasNext()) {
			ObjetJeu obj = it.next();
			if (obj.getNom().equalsIgnoreCase(nomObjet)) {
				it.remove();
				return obj;
			}
		}
		return null;
	}

	public String listerObjets() {
		if (objetsPresents == null || objetsPresents.isEmpty()) {
			return "Il n'y a aucun objet visible ici.";
		}

		List<String> nomsObjets = new ArrayList<>();
		for (ObjetJeu obj : objetsPresents) {
			nomsObjets.add(obj.getNom());
		}
		return String.join(", ", nomsObjets);
	}

	public List<Conteneur> getConteneurs() {
		return this.conteneurs;
	}

	public int getNombreObjetsSurSol() {
		return objetsPresents.size();
	}

	public String toString() {
		return description;
	}

	public String descriptionLongue() {
		StringBuilder sb = new StringBuilder();
		sb.append("Lieu : ").append(description);
		sb.append("\nSorties : ");

		if (sorties.isEmpty()) {
			sb.append("aucune");
		} else {
			List<String> nomsSorties = new ArrayList<>();
			for (Direction direction : sorties.keySet()) {
				nomsSorties.add(direction.name());
			}
			sb.append(String.join(", ", nomsSorties));
		}

		return sb.toString();
	}

	public List<ObjetJeu> getObjetsPresents() {
		return this.objetsPresents;
	}

}
