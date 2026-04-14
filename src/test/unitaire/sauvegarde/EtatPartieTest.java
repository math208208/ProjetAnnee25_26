package test.unitaire.sauvegarde;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jeu.Jeu;
import jeu.environnement.Conteneur;
import jeu.environnement.Zone;
import jeu.objets.Allumettes;
import jeu.objets.Cle;
import jeu.objets.Echelle;
import jeu.objets.MorceauBois;
import jeu.sauvegarde.EtatPartie;
import test.unitaire.TestSupport;

class EtatPartieTest {

	@Test
	void capturerCopieLEtatDuJeuLesObjetsEtLesConteneurs() {
		Jeu jeu = TestSupport.jeuPret("Alice");
		jeu.setEclairageActif(true);
		jeu.setChemineActif(true);
		jeu.setFragmentsDetruits(1);
		jeu.getJoueur().setVies(2);
		jeu.getJoueur().getInventaire().ajoute(new MorceauBois("bois", "Bois"));
		Zone cuisine = jeu.getManoir().obtientZone("cuisine");
		cuisine.ajouteObjet(new Allumettes("allumettes", "Allumettes"));
		Conteneur tiroir = cuisine.getConteneur("Tiroir");
		tiroir.ajouteObjet(new Cle("cle_test", "Cle", "Tiroir"));
		tiroir.setEstOuvert(true);

		EtatPartie etat = new EtatPartie().capturer(jeu);

		assertEquals("Alice", etat.getPseudo());
		assertEquals(2, etat.getVies());
		assertEquals("salon", etat.getZoneActuelle());
		assertEquals(1, etat.getFragmentsDetruits());
		assertTrue(etat.isEclairageActif());
		assertTrue(etat.isChemineActif());
		assertEquals(Arrays.asList("bois"), etat.getInventaire());
		assertEquals("ZONE:cuisine", etat.getPositionsObjets().get("allumettes"));
		assertEquals("CONTENEUR:Tiroir:cuisine", etat.getPositionsObjets().get("cle_test"));
		assertTrue(etat.getEtatsConteneurs().contains("cuisine|Tiroir|true|false"));
	}

	@Test
	void restaurerReplaceInventairePositionsObjetsEtEtatsConteneurs() {
		Jeu jeu = TestSupport.jeuPret("Bob");
		EtatPartie etat = new EtatPartie();
		Map<String, String> positions = new HashMap<>();
		positions.put("echelle", "ZONE:cuisine");
		positions.put("allumettes", "CONTENEUR:Tiroir:cuisine");

		etat.setPseudo("Bob");
		etat.setVies(1);
		etat.setZoneActuelle("cuisine");
		etat.setFragmentsDetruits(2);
		etat.setEclairageActif(true);
		etat.setChemineActif(true);
		etat.setInventaire(Arrays.asList("bois", "cle_coffre_1"));
		etat.setPositionsObjets(positions);
		etat.setEtatsConteneurs(Arrays.asList("cuisine|Tiroir|true|false", "chambre1|Coffre|false|true"));

		etat.restaurer(jeu);

		assertEquals(1, jeu.getJoueur().getVies());
		assertEquals(2, jeu.getFragmentsDetruits());
		assertTrue(jeu.isEclairageActif());
		assertTrue(jeu.isChemineActif());
		assertEquals("cuisine", jeu.getZoneCourante().getNom());
		assertTrue(jeu.getJoueur().possede("bois"));
		assertTrue(jeu.getJoueur().possede("cle_coffre_1"));
		assertTrue(jeu.getJoueur().getInventaire().getObjets().get(0) instanceof MorceauBois);
		assertTrue(jeu.getManoir().obtientZone("cuisine").retireObjet("echelle") instanceof Echelle);
		assertTrue(jeu.getManoir().obtientZone("cuisine").getConteneur("Tiroir").retireObjet("allumettes") instanceof Allumettes);
		assertTrue(jeu.getManoir().obtientZone("cuisine").getConteneur("Tiroir").estOuvert());
		assertTrue(jeu.getManoir().obtientZone("chambre1").getConteneur("Coffre").estVerrouille());
	}
}
