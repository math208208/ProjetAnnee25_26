package jeu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Commande {

	String getAbreviation();

	String getDescription();

	static List<Commande> toutes() {
		List<Commande> toutes = new ArrayList<>();
		toutes.addAll(Arrays.asList(Direction.values()));
		toutes.addAll(Arrays.asList(CommandeNonDirectionnelle.values()));
		return toutes;
	}

	static List<String> toutesLesDescriptions() {
		List<String> resultat = new ArrayList<>();
		for (Commande c : toutes()) {
			resultat.add(c.getDescription());
		}
		return resultat;
	}

	static List<String> toutesLesAbreviations() {
		List<String> resultat = new ArrayList<>();
		for (Commande c : toutes()) {
			resultat.add(c.getAbreviation());
		}
		return resultat;
	}

	static List<String> tousLesNoms() {
		List<String> resultat = new ArrayList<>();
		for (Commande c : toutes()) {
			resultat.add(c.toString());
		}
		return resultat;
	}
}