package test.unitaire.sauvegarde;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jeu.sauvegarde.EtatPartie;
import jeu.sauvegarde.GestionnaireSauvegardeJSON;

class GestionnaireSauvegardeJSONTest {

	private final String pseudo = "test_unitaire_sauvegarde";
	private final File fichier = new File("joueur_" + pseudo + ".json");

	@AfterEach
	void nettoyer() {
		if (fichier.exists()) {
			fichier.delete();
		}
	}

	@Test
	void sauvegarderPartieCreeUnFichierEtChargerPartieLeRelit() {
		GestionnaireSauvegardeJSON gestionnaire = new GestionnaireSauvegardeJSON();
		EtatPartie etat = new EtatPartie();
		Map<String, String> positions = new HashMap<>();
		positions.put("bois", "ZONE:salon");
		positions.put("cle", "CONTENEUR:Coffre:chambre1");

		etat.setPseudo(pseudo);
		etat.setVies(2);
		etat.setZoneActuelle("salon");
		etat.setFragmentsDetruits(1);
		etat.setEclairageActif(true);
		etat.setChemineActif(true);
		etat.setInventaire(Arrays.asList("allumettes", "echelle"));
		etat.setPositionsObjets(positions);
		etat.setEtatsConteneurs(Arrays.asList("salon|Cheminee|true|false", "chambre1|Coffre|false|true"));

		assertTrue(gestionnaire.sauvegarderPartie(etat, pseudo));
		assertTrue(gestionnaire.verifierExistenceFichier(pseudo));

		EtatPartie charge = gestionnaire.chargerPartie(pseudo);

		assertNotNull(charge);
		assertEquals(pseudo, charge.getPseudo());
		assertEquals(2, charge.getVies());
		assertEquals("salon", charge.getZoneActuelle());
		assertEquals(1, charge.getFragmentsDetruits());
		assertTrue(charge.isEclairageActif());
		assertTrue(charge.isChemineActif());
		assertEquals(Arrays.asList("allumettes", "echelle"), charge.getInventaire());
		assertEquals("ZONE:salon", charge.getPositionsObjets().get("bois"));
		assertTrue(charge.getEtatsConteneurs().contains("salon|Cheminee|true|false"));
	}

	@Test
	void chargerPartieRetourneNullSiLeFichierNExistePas() {
		GestionnaireSauvegardeJSON gestionnaire = new GestionnaireSauvegardeJSON();

		assertFalse(gestionnaire.verifierExistenceFichier(pseudo));
		assertNull(gestionnaire.chargerPartie(pseudo));
	}
}
