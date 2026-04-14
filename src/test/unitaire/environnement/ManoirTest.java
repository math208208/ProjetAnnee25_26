package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.Direction;
import jeu.environnement.Manoir;
import jeu.environnement.Zone;

class ManoirTest {

	@Test
	void manoirCreeToutesLesZonesEtLesConteneursPrincipaux() {
		Manoir manoir = new Manoir();

		assertEquals(10, manoir.getToutesLesZones().size());
		assertEquals("salon", manoir.getZoneDepart().getNom());
		assertNotNull(manoir.obtientZone("salon").getConteneur("Cheminee"));
		assertNotNull(manoir.obtientZone("cuisine").getConteneur("Tiroir"));
		assertNotNull(manoir.obtientZone("bureau").getConteneur("Bureau"));
		assertNotNull(manoir.obtientZone("bureau").getConteneur("Armoire"));
		assertNotNull(manoir.obtientZone("chambre1").getConteneur("Coffre"));
		assertNotNull(manoir.obtientZone("chambre2").getConteneur("Coffre"));
		assertNotNull(manoir.obtientZone("cave").getConteneur("CorpsBaron"));
	}

	@Test
	void sortiesRelientLeParcoursEtLaCaveEstCacheeAuDepart() {
		Manoir manoir = new Manoir();
		Zone salon = manoir.obtientZone("salon");
		Zone grandCouloir = manoir.obtientZone("grand_couloir");
		Zone bibliotheque = manoir.getToutesLesZones().stream()
				.filter(zone -> zone.getNom().toLowerCase().startsWith("biblioth")).findFirst().orElseThrow();

		assertSame(grandCouloir, salon.obtientSortie(Direction.NORD));
		assertSame(salon, grandCouloir.obtientSortie(Direction.SUD));
		assertNull(bibliotheque.obtientSortie(Direction.SUD));

		bibliotheque.revelerSortieCachee("Livre", Direction.SUD);

		assertSame(manoir.obtientZone("cave"), bibliotheque.obtientSortie(Direction.SUD));
	}

	@Test
	void obtenirZoneInconnueRetourneNull() {
		assertNull(new Manoir().obtientZone("grenier"));
	}
}
