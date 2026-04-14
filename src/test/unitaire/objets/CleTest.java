package test.unitaire.objets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.objets.Cle;

class CleTest {

	@Test
	void cleExposeSesInformationsEtCorrespondALaCibleSansCasse() {
		Cle cle = new Cle("cle_bureau", "Cle du bureau", "Bureau");

		assertEquals("cle_bureau", cle.getNom());
		assertEquals("Cle du bureau", cle.getDescription());
		assertEquals("Bureau", cle.getCible());
		assertFalse(cle.estFragment());
		assertEquals("cle_bureau", cle.toString());
		assertTrue(cle.correspondA("bureau"));
		assertFalse(cle.correspondA("Armoire"));
	}
}
