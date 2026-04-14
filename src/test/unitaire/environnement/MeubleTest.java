package test.unitaire.environnement;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.environnement.Meuble;
import jeu.environnement.TypeMeuble;

class MeubleTest {

	@Test
	void meubleExposeSonNomEtSonType() {
		Meuble meuble = new Meuble("Miroir", TypeMeuble.MIROIR_MAGIQUE);

		assertEquals("Miroir", meuble.getNom());
		assertEquals(TypeMeuble.MIROIR_MAGIQUE, meuble.getType());
		assertArrayEquals(new TypeMeuble[] { TypeMeuble.INTERRUPTEUR, TypeMeuble.MIROIR_MAGIQUE,
				TypeMeuble.LIVRE_SECRET }, TypeMeuble.values());
	}
}
