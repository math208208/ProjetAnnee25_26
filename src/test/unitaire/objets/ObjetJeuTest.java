package test.unitaire.objets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.Jeu;
import jeu.joueur.Joueur;
import jeu.objets.Allumettes;
import jeu.objets.Echelle;
import jeu.objets.MedaillonMagique;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetJeu;
import jeu.objets.ObjetMaudit;
import jeu.objets.TypeFragment;

class ObjetJeuTest {

	private static class FauxObjet extends ObjetJeu {
		private boolean utilise;

		FauxObjet(String nom, String description, boolean fragment) {
			super(nom, description, fragment);
		}

		@Override
		public void utiliser(Joueur joueur, Jeu jeu) {
			utilise = true;
		}
	}

	@Test
	void objetJeuExposeNomDescriptionFragmentEtToString() {
		FauxObjet objet = new FauxObjet("objet", "description", true);

		assertEquals("objet", objet.getNom());
		assertEquals("description", objet.getDescription());
		assertTrue(objet.estFragment());
		assertEquals("objet", objet.toString());

		objet.utiliser(new Joueur("test"), new Jeu());

		assertTrue(objet.utilise);
	}

	@Test
	void objetsConcretsOntLeBonTypeDeFragment() {
		assertFalse(new Allumettes("allumettes", "Allumettes").estFragment());
		assertFalse(new MorceauBois("bois", "Bois").estFragment());
		assertFalse(new Echelle("echelle", "Echelle").estFragment());
		assertFalse(new MedaillonMagique("medaillonMagique", "Medaillon").estFragment());

		ObjetMaudit fragment = new ObjetMaudit("journal_intime", "Fragment", TypeFragment.JOURNAL_INTIME);

		assertTrue(fragment.estFragment());
		assertEquals(TypeFragment.JOURNAL_INTIME, fragment.getTypeFragment());
	}

	@Test
	void methodesUtiliserDesObjetsConcretsNecessitentUneGuiDansLeJeuActuel() {
		Jeu jeuSansGui = new Jeu();
		Joueur joueur = new Joueur("test");

		assertThrows(NullPointerException.class,
				() -> new Allumettes("allumettes", "Allumettes").utiliser(joueur, jeuSansGui));
		assertThrows(NullPointerException.class, () -> new MorceauBois("bois", "Bois").utiliser(joueur, jeuSansGui));
		assertThrows(NullPointerException.class, () -> new Echelle("echelle", "Echelle").utiliser(joueur, jeuSansGui));
		assertThrows(NullPointerException.class,
				() -> new MedaillonMagique("medaillonMagique", "Medaillon").utiliser(joueur, jeuSansGui));
		assertThrows(NullPointerException.class,
				() -> new ObjetMaudit("pipe_bois", "Fragment", TypeFragment.PIPE_BOIS).utiliser(joueur, jeuSansGui));
	}

	@Test
	void enumTypeFragmentContientLesCinqFragmentsPossibles() {
		assertArrayEquals(new TypeFragment[] { TypeFragment.MONTRE_GOUSSET, TypeFragment.PIPE_BOIS,
				TypeFragment.JOURNAL_INTIME, TypeFragment.PLUME, TypeFragment.MEDAILLON }, TypeFragment.values());
	}
}
