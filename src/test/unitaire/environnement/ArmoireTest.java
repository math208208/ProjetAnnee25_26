package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Armoire;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class ArmoireTest {

	@Test
	void deverrouillerAvecLaBonneCleOuvreLArmoire() {
		Cle bonneCle = new Cle("cle_armoire", "Cle armoire", "Armoire");
		Armoire armoire = new Armoire("Armoire", true, bonneCle);

		assertTrue(armoire.estVerrouille());
		assertFalse(armoire.estOuvert());
		assertTrue(armoire.deverrouillerAvecCle(new Cle("cle_armoire", "", "Armoire"), new Joueur("test")));
		assertFalse(armoire.estVerrouille());
		assertTrue(armoire.estOuvert());
	}

	@Test
	void mauvaiseCleOuAbsenceDeCleNeDeverrouillePas() {
		Armoire armoire = new Armoire("Armoire", true, new Cle("cle_armoire", "", "Armoire"));

		assertFalse(armoire.deverrouillerAvecCle(new Cle("cle_bureau", "", "Bureau"), new Joueur("test")));
		armoire.setCleRequise(null);
		assertFalse(armoire.deverrouillerAvecCle(new Cle("cle_armoire", "", "Armoire"), new Joueur("test")));
		assertTrue(armoire.estVerrouille());
	}
}
