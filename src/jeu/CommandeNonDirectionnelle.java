package jeu;


public enum CommandeNonDirectionnelle implements Commande {
	AIDE("?", "? (aide)"),

	QUITTER("Q", "Q (quitter)"),

	RETOUR("R", "R (retour)"),

	PRENDRE("P", "P (prendre un objet)"),

	INVENTAIRE("I", "I (Voir l'inventaire)"),

	OUVRIR("OU", "OU (ouvrir un conteneur)"),

	ALLUMER_FEU("AF", "AF (Allumer la cheminer)"),

	ALLUMER_FEU_2("CMD2", "COMMANDE 2"),

	ALLUMER_LUM("CMD1", "COMMANDE 1"),

	BRULER("B", "B (brûler un fragment)"),

	REPONDRE("REP","REP (Repondre a la question) "),

	SAUVER("SAUV", "SAUV (Sauvgarder la partie)"),

	ABANDON("AB", "AB (Abandonner la partie)"),

	TEST("T", "T (Executter une partie victorieuse)"),

	MIROIR("M", "M (se deplacer via le miroir"),

	TELEPORTER("TP", "TP teleportation via miroir");

	private final String abreviation;

	private final String description;

	private CommandeNonDirectionnelle(String a, String d) {
		abreviation = a;
		description = d;
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
