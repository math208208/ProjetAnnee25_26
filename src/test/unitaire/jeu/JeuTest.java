package test.unitaire.jeu;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import jeu.Direction;
import jeu.EtatJeu;
import jeu.Jeu;
import jeu.environnement.Manoir;
import jeu.environnement.Zone;
import jeu.joueur.Joueur;
import jeu.objets.Cle;
import jeu.objets.MorceauBois;
import test.unitaire.TestSupport;

class JeuTest {

	@Test
	void constructeurDemarreSansGuiEtCommandeSansGuiEstRefusee() {
		Jeu jeu = new Jeu();

		assertNull(jeu.getGui());
		assertThrows(IllegalStateException.class, () -> jeu.traiterCommande("?"));
	}

	@Test
	void gettersEtSettersExposentLEtatPrincipalPreparePourLesSauvegardes() {
		Jeu jeu = TestSupport.jeuPret("Alice");
		Manoir manoir = jeu.getManoir();
		Zone cuisine = manoir.obtientZone("cuisine");

		jeu.setZoneCourante(cuisine);
		jeu.setEclairageActif(true);
		jeu.setChemineActif(true);
		jeu.setFragmentsDetruits(2);

		assertEquals("Alice", jeu.getJoueur().getPseudo());
		assertSame(manoir, jeu.getManoir());
		assertSame(cuisine, jeu.getZoneCourante());
		assertTrue(jeu.isEclairageActif());
		assertTrue(jeu.isChemineActif());
		assertEquals(2, jeu.getFragmentsDetruits());
		assertEquals(EtatJeu.EN_COURS, jeu.getEtatJeu());
	}

	@Test
	void etatJeuContientLesTroisEtatsAttendus() {
		assertArrayEquals(new EtatJeu[] { EtatJeu.EN_COURS, EtatJeu.VICTOIRE, EtatJeu.DEFAITE }, EtatJeu.values());
	}

	@Test
	void fieldsPrivesPeuventRepresenterUnJoueurEtUnManoirInitialises() {
		Jeu jeu = new Jeu();
		Joueur joueur = new Joueur("Bob");
		Manoir manoir = new Manoir();

		TestSupport.setField(jeu, "joueur", joueur);
		TestSupport.setField(jeu, "manoir", manoir);
		TestSupport.setField(jeu, "zoneCourante", manoir.getZoneDepart());
		TestSupport.setField(jeu, "etatJeu", EtatJeu.VICTOIRE);

		assertSame(joueur, jeu.getJoueur());
		assertSame(manoir, jeu.getManoir());
		assertEquals("salon", jeu.getZoneCourante().getNom());
		assertEquals(EtatJeu.VICTOIRE, jeu.getEtatJeu());
	}

	@Test
	void genererNomImageBaseSuitLEtatDeLaZoneEtDesObjetsVisibles() {
		Jeu jeu = TestSupport.jeuPret("Alice");

		String salonEteint = TestSupport.invokePrivate(jeu, "genererNomImageBase", new Class<?>[] {});
		assertEquals("salon/salon_OFF_chemine_OFF", salonEteint);

		Zone bibliotheque = jeu.getManoir().getToutesLesZones().stream()
				.filter(zone -> zone.getNom().toLowerCase().startsWith("biblioth")).findFirst().orElseThrow();
		bibliotheque.ajouteObjet(new MorceauBois("bois", "Bois"));
		jeu.setZoneCourante(bibliotheque);
		jeu.setEclairageActif(true);

		String imageBibliotheque = TestSupport.invokePrivate(jeu, "genererNomImageBase", new Class<?>[] {});

		assertTrue(imageBibliotheque.contains("_ON"));
		assertTrue(imageBibliotheque.endsWith("_bois"));
	}

	@Test
	void retourEstPossibleSeulementSiLaZoneCouranteAPourSortieLaZonePrecedente() {
		Jeu jeu = TestSupport.jeuPret("Alice");
		Manoir manoir = jeu.getManoir();
		ArrayDeque<Zone> historique = new ArrayDeque<>();
		historique.push(manoir.obtientZone("salon"));
		TestSupport.setField(jeu, "historiqueZones", historique);
		jeu.setZoneCourante(manoir.obtientZone("grand_couloir"));

		Boolean possible = TestSupport.invokePrivate(jeu, "retourEstPossible", new Class<?>[] {});

		assertTrue(possible);

		jeu.setZoneCourante(manoir.obtientZone("cuisine"));

		Boolean impossible = TestSupport.invokePrivate(jeu, "retourEstPossible", new Class<?>[] {});

		assertFalse(impossible);
	}

	@Test
	void getZonesVisiteesDedoublonneLaZoneCouranteEtLHistorique() {
		Jeu jeu = TestSupport.jeuPret("Alice");
		Manoir manoir = jeu.getManoir();
		ArrayDeque<Zone> historique = new ArrayDeque<>();
		historique.push(manoir.obtientZone("salon"));
		historique.push(manoir.obtientZone("cuisine"));
		historique.push(manoir.obtientZone("salon"));
		TestSupport.setField(jeu, "historiqueZones", historique);
		jeu.setZoneCourante(manoir.obtientZone("salon"));

		List<Zone> visitees = TestSupport.invokePrivate(jeu, "getZonesVisitees", new Class<?>[] {});

		assertEquals(2, visitees.size());
		assertTrue(visitees.contains(manoir.obtientZone("salon")));
		assertTrue(visitees.contains(manoir.obtientZone("cuisine")));
	}

	@Test
	void placerCleCoffreAjouteLaCleDansUneZoneDisponible() {
		Jeu jeu = TestSupport.jeuPret("Alice");
		Zone zone = new Zone("test", "zone test", "test", false);
		Cle cle = new Cle("cle_coffre_1", "Cle", "Coffre");

		TestSupport.invokePrivate(jeu, "placerCleCoffre",
				new Class<?>[] { Cle.class, List.class, Random.class }, cle, List.of(zone), new Random(1));

		assertSame(cle, zone.retireObjet("cle_coffre_1"));
	}
}
