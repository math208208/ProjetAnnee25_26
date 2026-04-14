package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.Direction;
import jeu.environnement.Tiroir;
import jeu.environnement.Zone;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetJeu;

class ZoneTest {

	@Test
	void zoneExposeNomImageEclairageEtDescription() {
		Zone zone = new Zone("salon", "le salon", "salon/salon_OFF", false);

		assertEquals("salon", zone.getNom());
		assertEquals("salon/salon_OFF", zone.nomImage());
		assertFalse(zone.estEclairee());

		zone.setEclairee(true);

		assertTrue(zone.estEclairee());
		assertEquals("le salon", zone.toString());
		assertTrue(zone.descriptionLongue().contains("le salon"));
	}

	@Test
	void sortiesClassiquesEtCacheesFonctionnent() {
		Zone salon = new Zone("salon", "le salon", "salon", false);
		Zone couloir = new Zone("couloir", "le couloir", "couloir", false);
		Zone cave = new Zone("cave", "la cave", "cave", false);

		salon.ajouteSortie(Direction.NORD, couloir);
		salon.ajouteSortieCachee("Livre", cave);

		assertSame(couloir, salon.obtientSortie(Direction.NORD));
		assertNull(salon.obtientSortie(Direction.SUD));
		salon.revelerSortieCachee("Livre", Direction.SUD);
		assertSame(cave, salon.obtientSortie(Direction.SUD));
	}

	@Test
	void objetsAuSolSontListesRetiresEtIgnoresQuandNull() {
		Zone zone = new Zone("cuisine", "la cuisine", "cuisine", false);
		ObjetJeu bois = new MorceauBois("bois", "Bois");

		assertEquals("Il n'y a aucun objet visible ici.", zone.listerObjets());
		zone.ajouteObjet(null);
		zone.ajouteObjet(bois);

		assertEquals(1, zone.getNombreObjetsSurSol());
		assertEquals("bois", zone.listerObjets());
		assertSame(bois, zone.retireObjet("BOIS"));
		assertNull(zone.retireObjet("bois"));
		assertTrue(zone.getObjetsPresents().isEmpty());
	}

	@Test
	void conteneursSontRetrouvesSansTenirCompteDeLaCasse() {
		Zone zone = new Zone("cuisine", "la cuisine", "cuisine", false);
		Tiroir tiroir = new Tiroir("Tiroir");

		zone.ajouteConteneur(tiroir);

		assertSame(tiroir, zone.getConteneur("tiroir"));
		assertNull(zone.getConteneur("coffre"));
		assertEquals(1, zone.getConteneurs().size());
	}
}
