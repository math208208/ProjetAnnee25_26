package jeu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Représente une commande disponible dans le jeu.
 *
 * <p>Une commande fournit une abréviation courte (utilisée pour la saisie) et une
 * description lisible. Les méthodes statiques permettent d'obtenir des listes
 * rassemblant toutes les commandes et certaines de leurs propriétés (descriptions,
 * abréviations, noms).
 */
public interface Commande {

	/**
	 * Retourne l'abréviation de la commande (ex : "n" pour nord).
	 *
	 * @return l'abréviation utilisable en saisie
	 */
	String getAbreviation();

	/**
	 * Retourne une description lisible de la commande (ex : "aller au nord").
	 *
	 * @return la description de la commande
	 */
	String getDescription();

	/**
	 * Construit et retourne une liste contenant toutes les commandes disponibles.
	 *
	 * <p>La liste contient d'abord les commandes directionnelles (enum {@code Direction})
	 * puis les commandes non directionnelles (enum {@code CommandeNonDirectionnelle}).
	 *
	 * @return nouvelle {@code List<Commande>} avec toutes les commandes
	 */
	static List<Commande> toutes() {
		List<Commande> allCommands = new ArrayList<>();
		allCommands.addAll(Arrays.asList(Direction.values()));
		allCommands.addAll(Arrays.asList(CommandeNonDirectionnelle.values()));
		return allCommands;
	}

	/**
	 * Retourne une liste des descriptions de toutes les commandes.
	 *
	 * @return nouvelle {@code List<String>} contenant les descriptions
	 */
	static List<String> toutesLesDescriptions() {
		List<String> descriptions = new ArrayList<>();
		for (Commande cmd : toutes()) {
			descriptions.add(cmd.getDescription());
		}
		return descriptions;
	}

	/**
	 * Retourne une liste des abréviations de toutes les commandes.
	 *
	 * @return nouvelle {@code List<String>} contenant les abréviations
	 */
	static List<String> toutesLesAbreviations() {
		List<String> abreviations = new ArrayList<>();
		for (Commande cmd : toutes()) {
			abreviations.add(cmd.getAbreviation());
		}
		return abreviations;
	}

	/**
	 * Retourne une liste des noms (valeurs de {@code toString()}) de toutes les commandes.
	 *
	 * @return nouvelle {@code List<String>} contenant les noms
	 */
	static List<String> tousLesNoms() {
		List<String> noms = new ArrayList<>();
		for (Commande cmd : toutes()) {
			noms.add(cmd.toString());
		}
		return noms;
	}
}