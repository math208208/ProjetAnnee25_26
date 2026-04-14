package jeu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private Jeu jeu;
    private GUI gui;

    @BeforeEach
    public void setUp() {
        jeu = new Jeu();

        gui = new GUI(jeu) {
            @Override
            public void afficher(String s) {
            }

            @Override
            public void afficher() {
            }

            @Override
            public void afficheImage(String nomDeBase) {
            }

            @Override
            public void enable(boolean ok) {
            }
        };
        jeu.setGUI(gui);

        jeu.traiterCommande("Testeur_Integration");
    }

    @Test
    public void testScenarioPerdant_SansEchelle() {
        assertEquals(EtatJeu.EN_COURS, jeu.getEtatJeu(), "Le jeu doit commencer.");

        jeu.traiterCommande("N");
        jeu.traiterCommande("E");
        jeu.traiterCommande("S");

        assertEquals("bibliothèque", jeu.getZoneCourante().getNom(), "Le joueur doit être dans la bibliothèque.");

        jeu.traiterCommande("OUVRIR livre");
        jeu.traiterCommande("S");

        assertEquals(EtatJeu.DEFAITE, jeu.getEtatJeu(),
                "La chute dans la cave sans l'échelle doit déclencher une défaite (Game Over).");
    }

    @Test
    public void testScenarioPerdant_PiegeMortel() {
        jeu.traiterCommande("N"); // Grand Couloir
        jeu.traiterCommande("N"); // Petit Couloir
        jeu.traiterCommande("O"); // Chambre 1

        jeu.traiterCommande("OUVRIR coffre");
        jeu.traiterCommande("OUVRIR coffre");
        jeu.traiterCommande("OUVRIR coffre");

        assertTrue(jeu.getJoueur().getVies() <= 0, "Le joueur doit avoir perdu toutes ses vies.");
        assertEquals(EtatJeu.DEFAITE, jeu.getEtatJeu(), "La mort par perte de PV doit déclencher une défaite.");
    }
}
