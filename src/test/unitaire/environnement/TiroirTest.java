package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Tiroir;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class TiroirTest {

	@Test
	void tiroirEstOuvrableSansCleEtNonDeverrouillable() {
		Tiroir tiroir = new Tiroir("Tiroir");
		Joueur joueur = new Joueur("test");

		assertFalse(tiroir.estVerrouille());
		assertTrue(tiroir.ouvre(joueur));
		assertTrue(tiroir.estOuvert());
		assertFalse(tiroir.deverrouillerAvecCle(new Cle("cle", "", "Tiroir"), joueur));
	}
}
