package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Cheminee;
import jeu.joueur.Joueur;
import jeu.objets.Allumettes;
import jeu.objets.Cle;
import jeu.objets.MorceauBois;

class ChemineeTest {

	@Test
	void allumerFeuNecessiteBoisEtAllumettesPuisLesConsomme() {
		Cheminee cheminee = new Cheminee("Cheminee");
		Joueur joueur = new Joueur("test");

		assertFalse(cheminee.verifierPresenceBoisAllumettes(joueur.getInventaire()));
		assertFalse(cheminee.allumerFeu(joueur));

		joueur.getInventaire().ajoute(new MorceauBois("bois", "Bois"));
		joueur.getInventaire().ajoute(new Allumettes("allumettes", "Allumettes"));

		assertTrue(cheminee.verifierPresenceBoisAllumettes(joueur.getInventaire()));
		assertTrue(cheminee.allumerFeu(joueur));
		assertFalse(joueur.possede("bois"));
		assertFalse(joueur.possede("allumettes"));
	}

	@Test
	void chemineeNestPasDeverrouillableParCle() {
		assertFalse(new Cheminee("Cheminee").deverrouillerAvecCle(new Cle("cle", "", "Cheminee"), new Joueur("test")));
	}
}
