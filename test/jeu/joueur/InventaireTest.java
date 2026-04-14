package jeu.joueur;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jeu.objets.ObjetJeu;
import jeu.Jeu;

public class InventaireTest {

    private Inventaire inventaire;

    @BeforeEach
    public void setUp() {
        inventaire = new Inventaire();
    }

    private ObjetJeu creerObjet(String nom) {
        return new ObjetJeu(nom, "Description", false) {
            @Override
            public void utiliser(Joueur joueur, Jeu jeu) {
            }
        };
    }

    @Test
    public void testAjouteObjetEtCapacite() {
        assertTrue(inventaire.estVide());

        for (int i = 0; i < 6; i++) {
            assertTrue(inventaire.ajoute(creerObjet("Objet" + i)), "L'ajout de l'objet " + i + " devrait réussir");
        }

        assertTrue(inventaire.estPlein(), "L'inventaire devrait être plein après 6 ajouts");
        assertFalse(inventaire.ajoute(creerObjet("ObjetTrop")), "L'ajout devrait échouer si l'inventaire est plein");
    }

    @Test
    public void testRetireObjet() {
        ObjetJeu objet = creerObjet("Cle");
        inventaire.ajoute(objet);

        assertTrue(inventaire.possede("cle"));

        ObjetJeu retire = inventaire.retire("cle");
        assertNotNull(retire);
        assertEquals("cle", retire.getNom().toLowerCase());

        assertFalse(inventaire.possede("cle"));
        assertNull(inventaire.retire("cle"), "Retirer un objet inexistant doit retourner null");
    }

    @Test
    public void testPossedeBoisEtAllumettes() {
        inventaire.ajoute(creerObjet("bois"));
        assertTrue(inventaire.possedeBois());
        assertFalse(inventaire.possedeAllumettes());

        inventaire.ajoute(creerObjet("allumettes"));
        assertTrue(inventaire.possedeAllumettes());
    }

    @Test
    public void testListerObjets() {
        assertEquals("Votre sac à dos est vide.", inventaire.listerObjets());

        inventaire.ajoute(creerObjet("Livre"));
        inventaire.ajoute(creerObjet("Cle"));

        String liste = inventaire.listerObjets();
        assertTrue(liste.contains("- Livre"));
        assertTrue(liste.contains("- Cle"));
    }
}
