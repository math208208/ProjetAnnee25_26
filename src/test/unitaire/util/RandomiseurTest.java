package test.unitaire.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import jeu.environnement.Zone;
import jeu.objets.Cle;
import jeu.objets.ObjetJeu;
import jeu.objets.TypeFragment;
import jeu.util.Randomiseur;

class RandomiseurTest {

	@Test
	void choisir3FragmentsParmi5RetourneTroisFragmentsDistincts() {
		Randomiseur randomiseur = new Randomiseur();

		List<TypeFragment> fragments = randomiseur.choisir3FragmentsParmi5();

		assertEquals(3, fragments.size());
		assertEquals(3, new HashSet<>(fragments).size());
		for (TypeFragment fragment : fragments) {
			assertNotNull(fragment);
		}
	}

	@Test
	void distribuerObjetsSurSolRespecteLaLimiteDeDeuxObjetsParZone() {
		Randomiseur randomiseur = new Randomiseur();
		List<Zone> zones = List.of(new Zone("z1", "zone 1", "z1", false), new Zone("z2", "zone 2", "z2", false));
		List<ObjetJeu> objets = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			objets.add(new Cle("cle" + i, "", ""));
		}

		randomiseur.distribuerObjetsSurSol(objets, zones);

		assertTrue(zones.get(0).getNombreObjetsSurSol() <= 2);
		assertTrue(zones.get(1).getNombreObjetsSurSol() <= 2);
		assertTrue(zones.get(0).getNombreObjetsSurSol() + zones.get(1).getNombreObjetsSurSol() <= 4);
	}

	@Test
	void distribuerObjetsSurSolIgnoreLesEntreesInvalides() {
		Randomiseur randomiseur = new Randomiseur();
		List<ObjetJeu> objets = List.of(new Cle("cle", "", ""));

		assertDoesNotThrow(() -> randomiseur.distribuerObjetsSurSol(null, List.of()));
		assertDoesNotThrow(() -> randomiseur.distribuerObjetsSurSol(objets, null));
		assertDoesNotThrow(() -> randomiseur.distribuerObjetsSurSol(objets, List.of()));
	}
}
