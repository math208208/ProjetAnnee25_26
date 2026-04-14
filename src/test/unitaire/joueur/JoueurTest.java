package test.unitaire.joueur;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jeu.joueur.Inventaire;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class JoueurTest {

	@Test
	void joueurDemarreAvecPseudoTroisViesInventaireEtZonesVisitees() {
		Joueur joueur = new Joueur("Alice");

		assertEquals("Alice", joueur.getPseudo());
		assertEquals(3, joueur.getVies());
		assertNotNull(joueur.getInventaire());
		assertTrue(joueur.getZonesVisitees().isEmpty());
	}

	@Test
	void perdreVieEtDiminuerPVNePassentPasSousZero() {
		Joueur joueur = new Joueur("Alice");

		joueur.perdreVie();
		assertEquals(2, joueur.getVies());
		joueur.diminuerPV(10);
		assertEquals(0, joueur.getVies());
		joueur.perdreVie();
		assertEquals(0, joueur.getVies());
	}

	@Test
	void possedeDelegueAInventaireEtSettersRemplacentLEtat() {
		Joueur joueur = new Joueur("Alice");
		Inventaire inventaire = new Inventaire();
		inventaire.ajoute(new Cle("cle", "", ""));
		Set<String> zones = new HashSet<>();
		zones.add("salon");

		joueur.setPseudo("Bob");
		joueur.setVies(7);
		joueur.setInventaire(inventaire);
		joueur.setZonesVisitees(zones);

		assertEquals("Bob", joueur.getPseudo());
		assertEquals(7, joueur.getVies());
		assertSame(inventaire, joueur.getInventaire());
		assertSame(zones, joueur.getZonesVisitees());
		assertTrue(joueur.possede("CLE"));
		assertFalse(joueur.possede("bois"));
	}
}
