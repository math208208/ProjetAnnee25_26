package test.unitaire.jeu;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jeu.Direction;
import jeu.EtatJeu;
import jeu.FausseGUI;
import jeu.Jeu;
import jeu.environnement.Zone;
import jeu.objets.Allumettes;
import jeu.objets.Echelle;
import jeu.objets.MedaillonMagique;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetMaudit;
import jeu.objets.TypeFragment;

class JeuTest {

    private Jeu jeu;
    private FausseGUI gui;

    @BeforeEach
    void setUp() {
        jeu = new Jeu();
        gui = new FausseGUI(jeu);
        jeu.setGUI(gui);

        jeu.demarrerEcranTitre();
        jeu.traiterCommande("TesteurUnitaire");
        
        if (gui.getHistorique().contains("Voulez-vous la charger")) {
            jeu.traiterCommande("NON");
        }
        
        gui.nettoyerHistorique();
    }

    @Test
    void testInitialisationNouvellePartie() {
        assertEquals(EtatJeu.EN_COURS, jeu.getEtatJeu());
        assertNotNull(jeu.getJoueur());
        assertEquals("TesteurUnitaire", jeu.getJoueur().getPseudo());
        assertEquals("salon", jeu.getZoneCourante().getNom());
        assertFalse(jeu.isEclairageActif());
        assertFalse(jeu.isChemineActif());
    }

    @Test
    void testDeplacementEtRetour() {
        assertEquals("salon", jeu.getZoneCourante().getNom());

        jeu.traiterCommande("NORD");
        assertEquals("grand_couloir", jeu.getZoneCourante().getNom());

        jeu.traiterCommande("RETOUR");
        assertEquals("salon", jeu.getZoneCourante().getNom());
    }

    @Test
    void testPrendreObjetEtInventaire() {
        Echelle echelle = new Echelle("echelle_test", "Une belle echelle");
        jeu.getZoneCourante().ajouteObjet(echelle);

        jeu.traiterCommande("PRENDRE echelle_test");
        
        assertTrue(jeu.getJoueur().getInventaire().possede("echelle_test"));
        assertNull(jeu.getZoneCourante().retireObjet("echelle_test"));
    }

    @Test
    void testAllumerLumiere() {
        assertFalse(jeu.isEclairageActif());
        
        jeu.traiterCommande("CMD1");
        
        assertTrue(jeu.isEclairageActif());
        assertTrue(gui.getHistorique().contains("La lumière inonde le manoir"));
    }

    @Test
    void testAllumerFeuChemine() {
        jeu.traiterCommande("CMD1");

        jeu.getJoueur().getInventaire().ajoute(new MorceauBois("bois", "Un bois"));
        jeu.getJoueur().getInventaire().ajoute(new Allumettes("allumettes", "Allumettes"));

        jeu.traiterCommande("ALLUMER_FEU");

        assertTrue(jeu.isChemineActif());
        assertFalse(jeu.getJoueur().getInventaire().possede("bois"), "Le bois doit être consommé");
        assertFalse(jeu.getJoueur().getInventaire().possede("allumettes"), "Les allumettes doivent être consommées");
    }

    @Test
    void testVictoireBrulerFragments() {
        jeu.traiterCommande("CMD1");
        jeu.setChemineActif(true); 

        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("frag1", "Fragment", TypeFragment.MEDAILLON));
        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("frag2", "Fragment", TypeFragment.JOURNAL_INTIME));
        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("frag3", "Fragment", TypeFragment.PIPE_BOIS));

        jeu.traiterCommande("BRULER");

        assertEquals(3, jeu.getFragmentsDetruits());
        assertEquals(EtatJeu.VICTOIRE, jeu.getEtatJeu());
        assertTrue(gui.getHistorique().contains("Félicitations"));
    }

    @Test
    void testDefaitePiegeDeLaCaveSansEchelle() {
        jeu.traiterCommande("N"); 
        jeu.traiterCommande("E"); 

        jeu.traiterCommande("OUVRIR livre");
        
        jeu.traiterCommande("S");

        assertEquals("cave", jeu.getZoneCourante().getNom());
        assertEquals(EtatJeu.DEFAITE, jeu.getEtatJeu());
    }

    @Test
    void testOuvrirPassageSecretBibliotheque() {
        jeu.traiterCommande("N"); 
        jeu.traiterCommande("E"); 
        
        Zone cave = jeu.getManoir().obtientZone("cave");
        assertNull(jeu.getZoneCourante().obtientSortie(Direction.SUD), "La sortie SUD doit être cachée initialement");

        jeu.traiterCommande("OUVRIR livre");

        assertNotNull(jeu.getZoneCourante().obtientSortie(Direction.SUD), "La sortie SUD doit être révélée");
        assertEquals(cave, jeu.getZoneCourante().obtientSortie(Direction.SUD));
    }

    @Test
    void testTeleportationMiroirSalleDeBain() {
        jeu.getJoueur().getInventaire().ajoute(new MedaillonMagique("medaillonMagique", "Medaillon"));

        jeu.traiterCommande("N");
        jeu.traiterCommande("N");
        jeu.traiterCommande("N");
        assertEquals("salle_de_bain", jeu.getZoneCourante().getNom());

        jeu.traiterCommande("MIROIR");
        
        jeu.traiterCommande("TELEPORTER salon");

        assertEquals("salon", jeu.getZoneCourante().getNom());
        assertTrue(gui.getHistorique().contains("Le monde tourne autour de vous"));
    }

    @Test
    void testAbandonner() {
        jeu.traiterCommande("ABANDON");
        assertEquals(EtatJeu.DEFAITE, jeu.getEtatJeu());
        assertTrue(gui.getHistorique().contains("Vous avez abandonné"));
    }
}