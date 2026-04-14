package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Coffre;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class CoffreTest {

	@Test
	void coffreEstVerrouillePiegeEtSouvreAvecLaBonneCle() {
		Coffre coffre = new Coffre("Coffre", new Cle("cle_coffre_1", "", "Coffre"));
		Joueur joueur = new Joueur("test");

		assertTrue(coffre.estVerrouille());
		assertFalse(coffre.estOuvert());
		assertFalse(coffre.ouvre(joueur));
		assertTrue(coffre.deverrouillerAvecCle(new Cle("cle_coffre_1", "", "Coffre"), joueur));
		assertFalse(coffre.estVerrouille());
		assertTrue(coffre.estOuvert());
	}

	@Test
	void mauvaiseCleRefuseeEtPiegeRetireUneVie() {
		Coffre coffre = new Coffre("Coffre", new Cle("cle_coffre_1", "", "Coffre"));
		Joueur joueur = new Joueur("test");

		assertFalse(coffre.deverrouillerAvecCle(new Cle("cle_coffre_2", "", "Coffre"), joueur));
		coffre.declenchePiege(joueur);

		assertEquals(2, joueur.getVies());
	}
}
