package jeu.environnement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jeu.objets.Cle;
import jeu.joueur.Joueur;

public class ArmoireTest {

    private Armoire armoire;
    private Cle cleArmoire;
    private Joueur joueur;

    @BeforeEach
    public void setUp() {
        cleArmoire = new Cle("cle_armoire", "Clé pour l'armoire", "armoire");
        armoire = new Armoire("Armoire", true, cleArmoire);
        joueur = new Joueur("Testeur");
    }

    @Test
    public void testDeverrouillerAvecBonneCle() {
        assertTrue(armoire.deverrouillerAvecCle(cleArmoire, joueur),
                "L'armoire devrait se déverrouiller avec la bonne clé");
        assertFalse(armoire.estVerrouille(), "L'armoire ne devrait plus être verrouillée");
        assertTrue(armoire.estOuvert(), "L'armoire devrait être ouverte");
    }

    @Test
    public void testDeverrouillerAvecMauvaiseCle() {
        Cle mauvaiseCle = new Cle("cle_bureau", "Mauvaise clé", "bureau");
        assertFalse(armoire.deverrouillerAvecCle(mauvaiseCle, joueur),
                "L'armoire ne doit pas s'ouvrir avec une mauvaise clé");
        assertTrue(armoire.estVerrouille(), "L'armoire doit rester verrouillée");
        assertFalse(armoire.estOuvert(), "L'armoire ne doit pas s'ouvrir");
    }
}
