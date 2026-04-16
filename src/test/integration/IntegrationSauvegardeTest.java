package test.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jeu.FausseGUI;
import jeu.Jeu;
import jeu.environnement.Conteneur;
import jeu.environnement.Zone;

class IntegrationSauvegardeTest {

    private Jeu jeu;
    private FausseGUI fausseGui;
    private final String PSEUDO_TEST = "TesteurSauv";

    @BeforeEach
    public void setup() {
        jeu = new Jeu();
        fausseGui = new FausseGUI(jeu);
        jeu.setGUI(fausseGui);
        
        jeu.traiterCommande(PSEUDO_TEST); 
        jeu.traiterCommande("NON"); // Force une nouvelle partie propre
    }

    @Test
    public void PersistanceInventaireTest() {
        Zone zoneCourante = jeu.getZoneCourante();
        zoneCourante.ajouteObjet(new jeu.objets.MorceauBois("bois", "bois test"));
        
        jeu.traiterCommande("PRENDRE bois");
        jeu.traiterCommande("SAUVER");

        Jeu jeuRelance = new Jeu();
        FausseGUI guiRelance = new FausseGUI(jeuRelance);
        jeuRelance.setGUI(guiRelance);
        jeuRelance.traiterCommande(PSEUDO_TEST); 
        jeuRelance.traiterCommande("OUI"); // On charge

        assertTrue(jeuRelance.getJoueur().getInventaire().possede("bois"), "Le bois n'a pas été sauvegardé !");
        assertNull(jeuRelance.getZoneCourante().retireObjet("bois"), "Le bois a été dupliqué au sol !");
    }

    @Test
    public void SyndromeDuCloneDesClesTest() {
        jeu.getJoueur().getInventaire().ajoute(new jeu.objets.Cle("cle_armoire", "Clé", "Armoire"));
        Zone bureau = jeu.getManoir().obtientZone("bureau");
        jeu.setZoneCourante(bureau);
        bureau.getConteneur("Armoire").setVerrouille(true);

        jeu.traiterCommande("SAUVER");

        Jeu jeuRelance = new Jeu();
        FausseGUI guiRelance = new FausseGUI(jeuRelance);
        jeuRelance.setGUI(guiRelance);
        jeuRelance.traiterCommande(PSEUDO_TEST);
        jeuRelance.traiterCommande("OUI");

        jeuRelance.traiterCommande("OUVRIR armoire");

        Conteneur armoireApres = jeuRelance.getManoir().obtientZone("bureau").getConteneur("Armoire");
        assertTrue(armoireApres.estOuvert(), "L'armoire a refusé la clé rechargée !");
    }

    @Test
    public void EtatEnvironnementTest() {
        jeu.setZoneCourante(jeu.getManoir().obtientZone("salon"));
        jeu.setEclairageActif(true);
        jeu.setChemineActif(true);

        jeu.traiterCommande("SAUVER");

        Jeu jeuRelance = new Jeu();
        FausseGUI guiRelance = new FausseGUI(jeuRelance);
        jeuRelance.setGUI(guiRelance);
        jeuRelance.traiterCommande(PSEUDO_TEST);
        jeuRelance.traiterCommande("OUI");

        assertTrue(jeuRelance.isEclairageActif(), "La lumière s'est éteinte au chargement !");
        assertTrue(jeuRelance.isChemineActif(), "La cheminée s'est éteinte !");
    }
}