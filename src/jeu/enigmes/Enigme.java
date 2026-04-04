package jeu.enigmes;

public class Enigme {
	private String question;
	private String reponse;

	public Enigme(String question, String reponse) {
		this.question = question;
		this.reponse = reponse;
	}

	public boolean verifierReponse(String reponseJoueur) {
		if (reponseJoueur == null) {
			return false;
		}
		// On compare en ignorant les majuscules et les espaces superflus
		return this.reponse.trim().equalsIgnoreCase(reponseJoueur.trim());
	}

	public String getQuestion() {
		return this.question;
	}
}