package test.unitaire.jeu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jeu.Direction;

class DirectionTest {

	@Test
	void directionsOntLeursAbreviationsEtDescriptions() {
		assertEquals("N", Direction.NORD.getAbreviation());
		assertEquals("S", Direction.SUD.getAbreviation());
		assertEquals("E", Direction.EST.getAbreviation());
		assertEquals("O", Direction.OUEST.getAbreviation());
		assertTrue(Direction.NORD.getDescription().contains("nord"));
		assertTrue(Direction.OUEST.getDescription().contains("ouest"));
		assertSame(Direction.NORD, Direction.valueOf("NORD"));
		assertEquals(4, Direction.values().length);
	}
}
