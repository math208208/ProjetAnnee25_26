package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.enigmes.Enigme;
import jeu.environnement.CorpsBaron;
import jeu.joueur.Joueur;
import jeu.objets.Cle;

class CorpsBaronTest {

	@Test
	void corpsBaronBloqueLouvertureJusquaResolutionDeLEnigme() {
		Enigme enigme = new Enigme("Question", "reponse");
		CorpsBaron corps = new CorpsBaron("CorpsBaron", enigme);
		Joueur joueur = new Joueur("test");

		assertSame(enigme, corps.declencherEnigme());
		assertTrue(corps.estVerrouille());
		assertFalse(corps.ouvre(joueur));

		corps.resoudreEnigme();

		assertFalse(corps.estVerrouille());
		assertTrue(corps.ouvre(joueur));
		assertTrue(corps.estOuvert());
	}

	@Test
	void corpsBaronNeSouvrePasAvecUneCle() {
		CorpsBaron corps = new CorpsBaron("CorpsBaron", new Enigme("Question", "reponse"));

		assertFalse(corps.deverrouillerAvecCle(new Cle("cle", "", "CorpsBaron"), new Joueur("test")));
	}
}
