package test.unitaire.joueur;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.joueur.ProfilJoueur;

class ProfilJoueurTest {

	@Test
	void profilStockePseudoDateCreationEtVictoires() {
		ProfilJoueur profil = new ProfilJoueur("Alice");

		assertEquals("Alice", profil.getPseudo());
		assertNotNull(profil.getDateCreation());
		assertEquals(0, profil.getPartiesGagnees());

		profil.incrementeVictoires();
		profil.incrementeVictoires();

		assertEquals(2, profil.getPartiesGagnees());
	}
}
