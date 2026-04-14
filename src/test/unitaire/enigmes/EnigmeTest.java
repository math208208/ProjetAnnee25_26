package test.unitaire.enigmes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.enigmes.Enigme;

class EnigmeTest {

	@Test
	void verifierReponseAccepteLaBonneReponseEtLeModeTest() {
		Enigme enigme = new Enigme("Question ?", "reponse");

		assertEquals("Question ?", enigme.getQuestion());
		assertTrue(enigme.verifierReponse("reponse"));
		assertTrue(enigme.verifierReponse("test"));
	}

	@Test
	void verifierReponseRefuseNullMauvaiseReponseEtCasseDifferente() {
		Enigme enigme = new Enigme("Question ?", "echo");

		assertFalse(enigme.verifierReponse(null));
		assertFalse(enigme.verifierReponse("Echo"));
		assertFalse(enigme.verifierReponse("ombre"));
	}
}
