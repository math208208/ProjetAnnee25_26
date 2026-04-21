package jeu;

/**
 * Enumération des commandes non-directionnelles du jeu.
 *
 * <p>
 * Chaque constante associe une abréviation (utilisée pour la saisie) et une
 * description lisible destinée à l'affichage pour l'aide ou les menus.
 */
public enum CommandeNonDirectionnelle implements Commande {

	AIDE("?", "? (afficher l'aide)"), QUITTER("Q", "Q (quitter)"), RETOUR("R", "R (retour)"),
	PRENDRE("P", "P <objet> (prendre un objet visible)"), INVENTAIRE("I", "I (voir l'inventaire)"),
	OUVRIR("OU", "OU <nom> (ouvrir un conteneur ou passage)"), ALLUMER_FEU("AF", "AF (allumer la cheminee)"),
	ALLUMER_FEU_2("CMD2", "CMD2 (Commande 2)"), ALLUMER_LUM("CMD1", "CMD1 (Commande 1)"),
	BRULER("B", "B (bruler les fragments)"), REPONDRE("REP", "REPONDRE <texte> (repondre a une enigme)"),
	SAUVER("SAUV", "SAUV (sauvegarder la partie)"), ABANDON("AB", "AB (abandonner la partie)"),
	TEST("T", "T (executer une partie de test)"), MIROIR("M", "M (activer le miroir)"),
	TELEPORTER("TP", "TP <piece> (teleportation via miroir)");

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
