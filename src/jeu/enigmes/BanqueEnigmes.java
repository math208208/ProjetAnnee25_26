package jeu.enigmes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BanqueEnigmes {

	private List<Enigme> enigmes;
	private Random random;

	public BanqueEnigmes() {
		this.enigmes = new ArrayList<>();
		this.random = new Random();
		chargerEnigmes();
	}

	public Enigme obtientEnigmeAleatoire() {
		if (enigmes.isEmpty()) {
			return null;
		}
		int index = random.nextInt(enigmes.size());
		return enigmes.get(index);
	}

	public void chargerEnigmes() {
		// Ajout de quelques énigmes thématiques
		enigmes.add(new Enigme("Je parle sans bouche et j'entends sans oreilles. Je n'ai pas de corps, mais je vis avec le vent. Qui suis-je ?", "echo"));
		enigmes.add(new Enigme("Plus il y en a, moins on voit. Qui suis-je ?", "obscurite"));
		enigmes.add(new Enigme("Je peux être brisée sans jamais être touchée. Que suis-je ?", "promesse"));
		enigmes.add(new Enigme("Je n'ai pas de poumons, mais j'ai besoin d'air pour vivre. L'eau me tue. Qui suis-je ?", "feu"));
		enigmes.add(new Enigme("Je te suis le jour mais je t'abandonne dans l'obscurité. Qui suis-je ?", "ombre"));
	}
}