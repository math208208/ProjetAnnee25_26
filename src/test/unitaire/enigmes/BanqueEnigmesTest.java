package test.unitaire.enigmes;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.enigmes.BanqueEnigmes;
import jeu.enigmes.Enigme;

class BanqueEnigmesTest {

	@Test
	void constructeurChargeDesEnigmesEtRetourneUneEnigmeAleatoire() {
		BanqueEnigmes banque = new BanqueEnigmes();

		for (int i = 0; i < 20; i++) {
			Enigme enigme = banque.obtientEnigmeAleatoire();
			assertNotNull(enigme);
			assertNotNull(enigme.getQuestion());
			assertFalse(enigme.getQuestion().isBlank());
		}
	}

	@Test
	void chargerEnigmesAjouteEncoreDesQuestionsSansCasserLaSelection() {
		BanqueEnigmes banque = new BanqueEnigmes();

		banque.chargerEnigmes();

		assertNotNull(banque.obtientEnigmeAleatoire());
	}
}
