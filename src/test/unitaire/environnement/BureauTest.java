package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Bureau;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class BureauTest {

	@Test
	void deverrouillerAvecLaBonneCleOuvreLeBureau() {
		Bureau bureau = new Bureau("Bureau", true, new Cle("cle_bureau", "", "Bureau"));

		assertTrue(bureau.deverrouillerAvecCle(new Cle("cle_bureau", "", "Bureau"), new Joueur("test")));
		assertFalse(bureau.estVerrouille());
		assertTrue(bureau.estOuvert());
	}

	@Test
	void bureauRefuseUneMauvaiseCleOuUneCleAbsente() {
		Bureau bureau = new Bureau("Bureau", true, new Cle("cle_bureau", "", "Bureau"));

		assertFalse(bureau.deverrouillerAvecCle(new Cle("cle_armoire", "", "Armoire"), new Joueur("test")));
		assertTrue(bureau.estVerrouille());
		bureau.setCleRequise(null);
		assertFalse(bureau.deverrouillerAvecCle(new Cle("cle_bureau", "", "Bureau"), new Joueur("test")));
	}
}
