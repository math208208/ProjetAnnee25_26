package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Conteneur;
import jeu.joueur.Joueur;
import jeu.objets.Cle;
import jeu.objets.ObjetJeu;
import jeu.objets.MorceauBois;

class ConteneurTest {

	private static class FauxConteneur extends Conteneur {
		FauxConteneur(String nom, boolean verrouille, Cle cle, boolean piege) {
			super(nom, verrouille, cle, piege);
		}

		@Override
		public boolean deverrouillerAvecCle(Cle cle, Joueur joueur) {
			if (estVerrouille && cleRequise != null && cle.getNom().equalsIgnoreCase(cleRequise.getNom())) {
				estVerrouille = false;
				estOuvert = true;
				return true;
			}
			return false;
		}
	}

	@Test
	void ouvrirDependDuVerrouillageEtModifieEtatOuvert() {
		Joueur joueur = new Joueur("test");
		FauxConteneur verrouille = new FauxConteneur("Boite", true, null, false);
		FauxConteneur libre = new FauxConteneur("Boite", false, null, false);

		assertFalse(verrouille.ouvre(joueur));
		assertFalse(verrouille.estOuvert());
		assertTrue(libre.ouvre(joueur));
		assertTrue(libre.estOuvert());
	}

	@Test
	void ajouteEtRetireLeContenuParNomSansAccepterNull() {
		FauxConteneur conteneur = new FauxConteneur("Boite", false, null, false);
		ObjetJeu bois = new MorceauBois("bois", "Bois");

		conteneur.ajouteObjet(null);
		conteneur.ajouteObjet(bois);

		assertEquals("Boite", conteneur.getNom());
		assertEquals(1, conteneur.getContenu().size());
		assertSame(bois, conteneur.retireObjet("BOIS"));
		assertNull(conteneur.retireObjet("bois"));
	}

	@Test
	void piegeRetireUneVieSeulementQuandIlEstActif() {
		Joueur joueur = new Joueur("test");

		new FauxConteneur("Sans piege", false, null, false).declenchePiege(joueur);
		assertEquals(3, joueur.getVies());

		new FauxConteneur("Piege", false, null, true).declenchePiege(joueur);
		assertEquals(2, joueur.getVies());
	}

	@Test
	void settersModifientVerrouEtOuvertureEtCleRequise() {
		FauxConteneur conteneur = new FauxConteneur("Boite", true, null, false);

		conteneur.setVerrouille(false);
		conteneur.setEstOuvert(true);
		conteneur.setCleRequise(new Cle("cle", "", "Boite"));

		assertFalse(conteneur.estVerrouille());
		assertTrue(conteneur.estOuvert());
	}
}
