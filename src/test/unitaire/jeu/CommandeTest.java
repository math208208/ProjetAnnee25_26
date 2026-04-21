package test.unitaire.jeu;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import jeu.Commande;
import jeu.CommandeNonDirectionnelle;
import jeu.Direction;

class CommandeTest {

	@Test
	void toutesRegroupeDirectionsPuisCommandesNonDirectionnelles() {
		List<Commande> commandes = Commande.toutes();

		assertEquals(Direction.values().length + CommandeNonDirectionnelle.values().length, commandes.size());
		assertEquals(Direction.NORD, commandes.get(0));
		assertTrue(commandes.contains(CommandeNonDirectionnelle.AIDE));
	}


	@Test
	void commandesNonDirectionnellesExposentAbreviationEtDescription() {
		assertEquals("Q", CommandeNonDirectionnelle.QUITTER.getAbreviation());
		assertTrue(CommandeNonDirectionnelle.QUITTER.getDescription().contains("quitter"));
		assertEquals("TP", CommandeNonDirectionnelle.TELEPORTER.getAbreviation());
	}
}
