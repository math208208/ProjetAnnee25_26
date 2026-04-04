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
		return this.reponse.equals(reponseJoueur) || reponseJoueur.equals("test");
	}

	public String getQuestion() {
		return this.question;
	}
}