package jeu;

/**
 * Enumération des commandes non-directionnelles du jeu.
 *
 * <p>
 * Chaque constante associe une abréviation (utilisée pour la saisie) et une
 * description lisible destinée à l'affichage pour l'aide ou les menus.
 */
public enum CommandeNonDirectionnelle implements Commande {

	AIDE("?", "? (aide)"), QUITTER("Q", "Q (quitter)"), RETOUR("R", "R (retour)"), PRENDRE("P", "P (prendre un objet)"),
	INVENTAIRE("I", "I (voir l'inventaire)"), OUVRIR("OU", "OU (ouvrir un conteneur)"),
	ALLUMER_FEU("AF", "AF (allumer la cheminée)"), ALLUMER_FEU_2("CMD2", "COMMANDE 2"),
	ALLUMER_LUM("CMD1", "COMMANDE 1"), BRULER("B", "B (brûler un fragment)"),
	REPONDRE("REP", "REP (répondre à la question)"), SAUVER("SAUV", "SAUV (sauvegarder la partie)"),
	ABANDON("AB", "AB (abandonner la partie)"), TEST("T", "T (exécuter une partie de test)"),
	MIROIR("M", "M (se déplacer via le miroir)"), TELEPORTER("TP", "TP (téléportation via miroir)");

	private final String abreviation;
	private final String description;

	/**
	 * Construit une commande non-directionnelle.
	 *
	 * @param abreviation courte utilisée pour la saisie
	 * @param description description lisible destinée à l'affichage
	 */
	private CommandeNonDirectionnelle(String abreviation, String description) {
		this.abreviation = abreviation;
		this.description = description;
	}

	@Override
	public String getAbreviation() {
		return abreviation;
	}

	@Override
	public String getDescription() {
		return description;
	}
}
