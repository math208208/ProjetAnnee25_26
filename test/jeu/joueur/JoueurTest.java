package jeu.joueur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jeu.objets.ObjetJeu;
import jeu.Jeu;

public class JoueurTest {

    private Joueur joueur;

    @BeforeEach
    public void setUp() {
        joueur = new Joueur("Testeur");
    }

    @Test
    public void testConstructeur() {
        assertEquals("Testeur", joueur.getPseudo());
        assertEquals(3, joueur.getVies(), "Le joueur doit commencer avec 3 vies");
        assertNotNull(joueur.getInventaire(), "L'inventaire ne doit pas être nul");
        assertNotNull(joueur.getZonesVisitees(), "Les zones visitées ne doivent pas être nulles");
    }

    @Test
    public void testPerdreVie() {
        joueur.perdreVie();
        assertEquals(2, joueur.getVies(), "Le joueur devrait avoir 2 vies après en avoir perdu une");

        joueur.perdreVie();
        joueur.perdreVie();
        assertEquals(0, joueur.getVies());

        joueur.perdreVie(); // Ne doit pas descendre en dessous de 0
        assertEquals(0, joueur.getVies());
    }

    @Test
    public void testDiminuerPV() {
        joueur.diminuerPV(2);
        assertEquals(1, joueur.getVies());

        joueur.diminuerPV(5); // Trop de dégâts, doit s'arrêter à 0
        assertEquals(0, joueur.getVies());
    }

    @Test
    public void testPossedeObjet() {
        ObjetJeu objetTest = new ObjetJeu("cle", "Une clé de test", false) {
            @Override
            public void utiliser(Joueur j, Jeu jeu) {
            }
        };

        assertFalse(joueur.possede("cle"));
        joueur.getInventaire().ajoute(objetTest);
        assertTrue(joueur.possede("cle"));
    }
}
