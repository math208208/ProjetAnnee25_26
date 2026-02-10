package jeu;

import java.io.Console;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * La classe {@code Jeu} représente la logique principale du jeu.
 * <p>
 * Elle gère les zones, les déplacements, les commandes utilisateur et
 * l'interaction avec l'interface graphique {@link GUI}.
 * <p>
 * Les fonctionnalités principales incluent :
 * <ul>
 * <li>Création et initialisation des zones du jeu</li>
 * <li>Affichage de la localisation et des images correspondantes</li>
 * <li>Traitement des commandes saisies par l'utilisateur</li>
 * <li>Gestion de la fin du jeu</li>
 * </ul>
 *
 * Exemple d'utilisation :
 * 
 * <pre>
 * Jeu jeu = new Jeu();
 * GUI gui = new GUI(jeu);
 * jeu.setGUI(gui);
 * </pre>
 */
public class Jeu {

	/** Interface graphique associée au jeu */
	private GUI gui;

	/** Zone actuelle dans laquelle se trouve le joueur */
	private Zone zoneCourante;

	/** Zones precedente dans laquelle se trouvait le joueur */
	private Stack<Zone> historiqueZones;

	// Création du sac a dos
	private List<Objet> sacADos = new ArrayList<>();

	// Capacité max du sac
	private final int CAPACITE_MAX = 5;

	// Nombre de fragments brulés (3 = partie gagné)
	private int fragmentsBrules = 0;
	
	private boolean cheminerAllume=false;

	private int nbVie = 3;

	/**
	 * Construit un nouveau jeu.
	 * <p>
	 * Les zones sont créées et reliées entre elles, mais l'interface graphique
	 * n'est pas encore initialisée. Utiliser {@link #setGUI(GUI)} pour associer une
	 * interface.
	 */
	public Jeu() {
		creerCarte();
		gui = null;
		historiqueZones = new Stack<>();
	}

	/**
	 * Associe une interface graphique au jeu et affiche le message de bienvenue.
	 *
	 * @param g l'instance de {@link GUI} à associer
	 */
	public void setGUI(GUI g) {
		gui = g;
		afficherMessageDeBienvenue();
	}

	/**
	 * Crée et initialise les zones du jeu et leurs sorties.
	 */
	private void creerCarte() {
		// Création de 4 zones
		Zone[] zones = new Zone[10];
		zones[0] = new Zone("le salon", "Salon.jpg");
		zones[1] = new Zone("la cuisine", "Cuisine.jpg");
		zones[2] = new Zone("la bibliothèque", "Bibliotheque.jpg");
		zones[3] = new Zone("le bureau", "Bureau.jpg");
		zones[4] = new Zone("le grand couloir", "GrandCouloir.jpg");
		zones[5] = new Zone("le petit couloir", "PetitCouloir.jpg");
		zones[6] = new Zone("la salle de bain", "SalleDeBain.jpg");
		zones[7] = new Zone("la chambre 1", "Chambre1.jpg");
		zones[8] = new Zone("la chambre 2", "Chambre2.jpg");
		zones[9] = new Zone("la cave", "Cave.jpg");

		// Définition des sorties des zones
		zones[0].ajouteSortie(Direction.NORD, zones[4]); // salon- Grand couloir
		zones[4].ajouteSortie(Direction.SUD, zones[0]); // Grand couloir - Salon

		zones[4].ajouteSortie(Direction.EST, zones[2]); // Grand couloir - Bibliothéque
		zones[2].ajouteSortie(Direction.OUEST, zones[4]); // Bibliothéque - Grand couloir

		zones[4].ajouteSortie(Direction.OUEST, zones[1]); // grand couloir - Cuisine
		zones[1].ajouteSortie(Direction.EST, zones[4]); // Cuisine -Grand couloir

		zones[4].ajouteSortie(Direction.NORD, zones[5]); // Grand couloir - petit couloir
		zones[5].ajouteSortie(Direction.SUD, zones[4]); // petit couloir -Grand couloir

		zones[2].ajouteSortie(Direction.SUD, zones[9]); // Bibliothéque - Cave
		zones[9].ajouteSortie(Direction.NORD, zones[2]); // Cave Bibliothéque

		zones[5].ajouteSortie(Direction.OUEST, zones[7]); // Petit couloir - Chambre 1
		zones[7].ajouteSortie(Direction.EST, zones[5]); // Chambre 1 - Petit Couloir

		zones[7].ajouteSortie(Direction.SUD, zones[3]); // Chambre 1 - Bureau
		zones[3].ajouteSortie(Direction.NORD, zones[7]); // Bureau - Chambre 1

		zones[5].ajouteSortie(Direction.EST, zones[8]); // Petit couloir - Chambre 2
		zones[8].ajouteSortie(Direction.OUEST, zones[5]); // Chambre 2 - Petit Couloir

		zones[5].ajouteSortie(Direction.NORD, zones[6]); // Petit couloir - Salle de bain
		zones[6].ajouteSortie(Direction.SUD, zones[5]); // Salle de bain - Petit Couloir

		// Gerer le miroir

		// Zone de départ du joueur
		zoneCourante = zones[0];
	}

	/**
	 * Vérifie l'initialisation de gui
	 */
	private void verifieGUI() {
		if (gui == null) {
			throw new IllegalStateException("GUI non initialisée !");
		}
	}

	/**
	 * Affiche la description complète de la zone actuelle via l'interface
	 * graphique.
	 */
	private void afficherLocalisation() {
		verifieGUI();
		gui.afficher(zoneCourante.descriptionLongue());
		gui.afficher();
	}

	/**
	 * Affiche le message de bienvenue et la localisation initiale.
	 */
	private void afficherMessageDeBienvenue() {
		verifieGUI();
		gui.afficher("Bienvenue !");
		gui.afficher();
		gui.afficher("Tapez '?' pour obtenir de l'aide.");
		gui.afficher();
		afficherLocalisation();
		gui.afficheImage(zoneCourante.nomImage());
	}

	/**
	 * Traite une commande saisie par l'utilisateur.
	 * <p>
	 * Les commandes reconnues sont :
	 * <ul>
	 * <li>?: affiche l'aide</li>
	 * <li>AIDE: affiche l'aide</li>
	 * <li>R ou RETOUR: revenir a la salle précédente</li<li>N ou NORD: se déplacer
	 * vers le nord</li>
	 * <li>S ou SUD: se déplacer vers le sud</li>
	 * <li>E ou EST: se déplacer vers l'est</li>
	 * <li>O ou OUEST: se déplacer vers l'ouest</li>
	 * <li>Q ou QUITTER: termine le jeu</li>
	 * </ul>
	 * Toute commande non reconnue génère un message d'erreur.
	 *
	 * @param commande la commande saisie par l'utilisateur
	 */
	public void traiterCommande(String commande) {
		verifieGUI();
		gui.afficher("> " + commande + "\n");
		switch (commande.toUpperCase()) {
		case "?", "AIDE" -> afficherAide();
		case "R", "RETOUR" -> retour();
		case "N", "NORD" -> allerEn(Direction.NORD);
		case "S", "SUD" -> allerEn(Direction.SUD);
		case "E", "EST" -> allerEn(Direction.EST);
		case "O", "OUEST" -> allerEn(Direction.OUEST);
		case "B", "BRULER" -> bruler();
		case "Q", "QUITTER" -> terminer();
		default -> gui.afficher("Commande inconnue");
		}
	}

	/**
	 * Affiche l'aide à l'utilisateur, incluant toutes les commandes disponibles.
	 */
	private void afficherAide() {
		verifieGUI();
		gui.afficher("Etes-vous perdu ?");
		gui.afficher();
		gui.afficher("Les commandes autorisées sont :");
		gui.afficher();
		gui.afficher(Commande.toutesLesDescriptions().toString());
		gui.afficher();
	}

	/**
	 * Déplace le joueur vers une nouvelle zone dans la direction donnée.
	 *
	 * @param direction ("NORD", "SUD", "EST" ou "OUEST")
	 */
	private void allerEn(Direction direction) {
		verifieGUI();
		Zone nouvelle = zoneCourante.obtientSortie(direction);
		if (nouvelle == null) {
			gui.afficher("Pas de sortie " + direction);
			gui.afficher();
		} else {
			historiqueZones.push(zoneCourante);
			zoneCourante = nouvelle;
			gui.afficher(zoneCourante.descriptionLongue());
			gui.afficher();
			gui.afficheImage(zoneCourante.nomImage());
		}
	}

	private void retour() {
		verifieGUI();
		if (historiqueZones.isEmpty()) {
			gui.afficher("Pas de retour possible ");
			gui.afficher();
		} else if (!retourEstPossible()) { // Vérification bidirectionnelle
			gui.afficher("Retour impossible : la communication n'est pas bidirectionnelle");
			gui.afficher();
		} else {
			zoneCourante = historiqueZones.pop();
			gui.afficher(zoneCourante.descriptionLongue());
			gui.afficher();
			gui.afficheImage(zoneCourante.nomImage());
		}

	}

	private boolean retourEstPossible() {
		Zone zonePrecedente = historiqueZones.peek();
		System.out.println(zonePrecedente);
		for (Direction dir : Direction.values()) {
			if (zoneCourante.obtientSortie(dir) == zonePrecedente) {
				return true;
			}
		}
		return false;
	}

	private void bruler() {
		
		//TODO ajouter verif cheminé allumé
		if (!zoneCourante.toString().equals("le salon")) {
			gui.afficher("Vous ne pouvez brûler des objets que dans la cheminer du salon !");
			return;
		}
		
		if(cheminerAllume == false) {
			gui.afficher("Vous ne pouvez brûler des objets que si la cheminé est allumé !");
			return;
		}

		if (sacADos.isEmpty()) {
			gui.afficher("Il n'y a aucun objet dans votre sac");
			return;
		}

		int nbObjetBruleCetteFois = 0;
		
		Iterator<Objet> it = sacADos.iterator();
	    while (it.hasNext()) {
	        Objet obj = it.next();
	        
	        if (obj.estFragment()) {
	            String nom = obj.getNom(); 
	            it.remove(); 
	            fragmentsBrules++;
	            nbObjetBruleCetteFois++;
	            gui.afficher(nom + " a été purifié dans les flammes.");
	        }
	    }

		if (nbObjetBruleCetteFois == 0) {
			gui.afficher("Aucun objet ne peut être brulé");
		} else {
			gui.afficher("Tout les objets disponible on etaient brulés");
		}

		if (fragmentsBrules == 3) {
			gui.afficher("Félicitations ! Le Baron Cole est libéré. Vous avez gagné !");
			finDePartie();
		}
	}

	private void finDePartie() {
		// TODO
	}

	/**
	 * Termine le jeu, affiche un message d'au revoir et désactive l'interface.
	 */
	private void terminer() {
		verifieGUI();
		gui.afficher("Au revoir...");
		gui.enable(false);
	}
}
