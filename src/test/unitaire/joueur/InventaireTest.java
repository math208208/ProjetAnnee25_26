package test.unitaire.joueur;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.joueur.Inventaire;
import jeu.objets.Allumettes;
import jeu.objets.Cle;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetJeu;

class InventaireTest {

	@Test
	void inventaireAjouteRetireListeEtChercheLesObjets() {
		Inventaire inventaire = new Inventaire();
		ObjetJeu bois = new MorceauBois("bois", "Bois");

		assertTrue(inventaire.estVide());
		assertTrue(inventaire.ajoute(bois));
		assertFalse(inventaire.estVide());
		assertTrue(inventaire.possede("BOIS"));
		assertTrue(inventaire.possedeBois());
		assertTrue(inventaire.listerObjets().contains("- bois"));
		assertSame(bois, inventaire.retire("BOIS"));
		assertNull(inventaire.retire("bois"));
		assertFalse(inventaire.possede("bois"));
	}

	@Test
	void capaciteMaximaleEstRespecteeEtConfigurable() {
		Inventaire inventaire = new Inventaire();
		inventaire.setCapaciteMax(2);

		assertEquals(2, inventaire.getCapaciteMax());
		assertTrue(inventaire.ajoute(new Cle("cle1", "", "")));
		assertTrue(inventaire.ajoute(new Cle("cle2", "", "")));
		assertFalse(inventaire.verifierCapacite());
		assertTrue(inventaire.estPlein());
		assertFalse(inventaire.ajoute(new Cle("cle3", "", "")));
		assertEquals(2, inventaire.getObjets().size());
	}

	@Test
	void helpersBoisEtAllumettesDetectentLesObjetsSpecifiques() {
		Inventaire inventaire = new Inventaire();

		assertFalse(inventaire.possedeBois());
		assertFalse(inventaire.possedeAllumettes());

		inventaire.ajoute(new MorceauBois("morceaux de bois", "Bois"));
		inventaire.ajoute(new Allumettes("allumettes", "Allumettes"));

		assertTrue(inventaire.possedeBois());
		assertTrue(inventaire.possedeAllumettes());
	}

	@Test
	void setObjetsRemplaceLaListeInterne() {
		Inventaire inventaire = new Inventaire();
		java.util.List<ObjetJeu> objets = new java.util.ArrayList<>();
		objets.add(new Cle("cle", "", ""));

		inventaire.setObjets(objets);

		assertSame(objets, inventaire.getObjets());
		assertTrue(inventaire.possede("cle"));
	}
}
