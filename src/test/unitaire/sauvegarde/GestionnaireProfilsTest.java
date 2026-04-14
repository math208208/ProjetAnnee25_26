package test.unitaire.sauvegarde;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.joueur.ProfilJoueur;
import jeu.sauvegarde.GestionnaireProfils;

class GestionnaireProfilsTest {

	@Test
	void creerProfilLeStockeEtAuthentifierLeRetrouve() {
		GestionnaireProfils gestionnaire = new GestionnaireProfils();

		assertNull(gestionnaire.authentifier("Alice"));

		ProfilJoueur profil = gestionnaire.creerProfil("Alice");

		assertSame(profil, gestionnaire.authentifier("Alice"));
		assertEquals("Alice", profil.getPseudo());
	}

	@Test
	void sauvegarderProfilsJsonEstActuellementUneOperationVide() {
		GestionnaireProfils gestionnaire = new GestionnaireProfils();

		assertDoesNotThrow(gestionnaire::sauvegarderProfilsJSON);
	}
}
