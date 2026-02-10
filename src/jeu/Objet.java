package jeu;

public class Objet {
	private String nom;
	private String description;
	private String image;
	private boolean estFragment;

	public Objet(String nom, String description, String image, boolean estFragment) {
		this.nom = nom;
		this.description = description;
		this.image = image;
		this.estFragment = estFragment;
	}

	public String getNom() {
		return nom;
	}

	public boolean estFragment() {
		return estFragment;
	}

	public String getDescription() {
		return description;
	}

	public String getImage() {
		return image;
	}

	public boolean isEstFragment() {
		return estFragment;
	}

	@Override
	public String toString() {
		return nom;
	}
}