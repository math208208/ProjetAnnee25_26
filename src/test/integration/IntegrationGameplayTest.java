package test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jeu.Direction;
import jeu.EtatJeu;
import jeu.FausseGUI;
import jeu.Jeu;
import jeu.environnement.Zone;
import jeu.objets.MorceauBois;
import jeu.objets.ObjetMaudit;
import jeu.objets.TypeFragment;

class IntegrationGameplayTest {

    private Jeu jeu;
    private FausseGUI fausseGui;
    private final String PSEUDO_TEST = "TesteurGame";


    @BeforeEach
    public void setup() {
        jeu = new Jeu();
        fausseGui = new FausseGUI(jeu);
        jeu.setGUI(fausseGui);
        
        jeu.traiterCommande(PSEUDO_TEST); 
        jeu.traiterCommande("NON");
    }

    @Test
    public void LimiteInventaireTest() {
        Zone zone = jeu.getZoneCourante();
        for (int i = 1; i <= 7; i++) {
            zone.ajouteObjet(new MorceauBois("objet" + i, "Objet test"));
            jeu.traiterCommande("PRENDRE objet" + i);
        }

        assertEquals(6, jeu.getJoueur().getInventaire().getObjets().size(), "La limite d'inventaire n'est pas respectée !");
        assertNotNull(zone.retireObjet("objet7"), "Le 7ème objet aurait dû rester au sol !");
    }

    @Test
    public void PiegeMortelTest() {
        Zone chambre = jeu.getManoir().obtientZone("chambre1");
        jeu.setZoneCourante(chambre);
        chambre.getConteneur("Coffre").setVerrouille(true);
        
        int viesAvant = jeu.getJoueur().getVies();
        jeu.traiterCommande("OUVRIR coffre");

        assertEquals(viesAvant - 1, jeu.getJoueur().getVies(), "Le joueur aurait dû perdre 1 vie !");
    }

    @Test
    public void VictoireTest() {
        jeu.setZoneCourante(jeu.getManoir().obtientZone("salon"));
        jeu.setEclairageActif(true);
        jeu.setChemineActif(true);
        
        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("fragment1", "F",TypeFragment.JOURNAL_INTIME));
        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("fragment2", "F", TypeFragment.PLUME));
        jeu.getJoueur().getInventaire().ajoute(new ObjetMaudit("fragment3", "F",TypeFragment.MONTRE_GOUSSET));

        jeu.traiterCommande("BRULER");

        assertEquals(EtatJeu.VICTOIRE, jeu.getEtatJeu(), "Les 3 fragments brûlés auraient dû donner la victoire !");
    }

    @Test
    public void DefaiteSoftLockCaveTest() {
        Zone biblio = jeu.getManoir().obtientZone("bibliothèque");
        jeu.setZoneCourante(biblio);
        biblio.revelerSortieCachee("Livre", Direction.SUD);

        jeu.traiterCommande("SUD");

        assertEquals(EtatJeu.DEFAITE, jeu.getEtatJeu(), "Descendre à la cave sans échelle devrait causer un Game Over !");
    }
}