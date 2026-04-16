package jeu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jeu.enigmes.BanqueEnigmes;
import jeu.enigmes.Enigme;
import jeu.environnement.Conteneur;
import jeu.environnement.Manoir;
import jeu.environnement.Zone;
import jeu.joueur.Joueur;
import jeu.objets.*;
import jeu.sauvegarde.EtatPartie;
import jeu.sauvegarde.GestionnaireSauvegardeJSON;
import jeu.util.Randomiseur;
import jeu.util.TestAutomatique;

/**
 * Classe principale gérant la logique du jeu.
 *
 * <p>Elle contient l'état courant (zone, joueur, inventaire, etc.) et expose des
 * méthodes pour traiter les commandes, gérer l'initialisation et l'avancement
 * de la partie.
 *
 * <p>La documentation des méthodes importantes est fournie pour faciliter la
 * compréhension par des débutants.
 */
public class Jeu {

	private GUI gui;
	private Zone zoneCourante;
	private ArrayDeque<Zone> historiqueZones;
	private Joueur joueur;
	private int fragmentsDetruits = 0;
	private EtatJeu etatJeu;
	private Manoir manoir;
	private BanqueEnigmes banqueEnigmes = new BanqueEnigmes();
	private Enigme enigmeEnCours;

	private GestionnaireSauvegardeJSON gestionnaireSauvegarde = new GestionnaireSauvegardeJSON();
	private boolean enMenuAccueil = true;
	private String nomJoueurTemp = "";
	private String etapeMenu = "DEMANDER_NOM";

	private boolean eclairageActif = false;
	private boolean chemineActif = false;
	private boolean miroirActive = false;

	/**
	 * Crée une instance de jeu. La GUI doit être fournie ultérieurement via
	 * {@link #setGUI(GUI)} avant d'appeler des méthodes affichant des informations.
	 */
	public Jeu() {
		gui = null;
		this.enMenuAccueil = true;
		this.etapeMenu = "DEMANDER_NOM";
	}

	/** Affiche l'écran titre et demande le nom du joueur. */
	public void demarrerEcranTitre() {
		gui.afficher("BIENVENUE DANS LE MANOIR");
		gui.afficher("Veuillez entrer votre nom de joueur : ");
	}

	/**
	 * Initialise et place les objets dans le manoir.
	 *
	 * <p>Cette méthode prépare les objets fixes, répartit aléatoirement des objets
	 * sur le sol et place les clés/objets maudits selon des règles du jeu.
	 */
	private void initialiserObjets() {
		Random random = new Random();
		Randomiseur randomiseur = new Randomiseur();
		List<Zone> toutesLesZones = manoir.getToutesLesZones();

		Zone cuisine = manoir.obtientZone("cuisine");
		Zone bibliotheque = manoir.obtientZone("bibliothèque");
		Zone cave = manoir.obtientZone("cave");
		Zone bureau = manoir.obtientZone("bureau");
		Zone chambre1 = manoir.obtientZone("chambre1");
		Zone chambre2 = manoir.obtientZone("chambre2");

		List<Zone> zonesSansCave = new ArrayList<>(toutesLesZones);
		zonesSansCave.remove(cave);

		// Objets fixes
		cuisine.getConteneur("Tiroir").ajouteObjet(new Allumettes("allumettes", "Boîte d'allumettes"));
		bibliotheque.ajouteObjet(new MorceauBois("bois", "Morceau de bois"));

		// Préparer la liste d'objets à distribuer
		List<ObjetJeu> objetsPourLeSol = new ArrayList<>();
		objetsPourLeSol.add(new Echelle("echelle", "Une échelle"));
		objetsPourLeSol.add(new MedaillonMagique("medaillonMagique", "Médaillon magique"));

		// Bureau / armoire : l'une des deux peut être verrouillée
		Conteneur meubleBureau = bureau.getConteneur("Bureau");
		Conteneur armoire = bureau.getConteneur("Armoire");
		Object cachetteGagnante = null;

		if (random.nextBoolean()) {
			// Le bureau est verrouillé
			meubleBureau.setVerrouille(true);
			Cle cleBur = new Cle("cle_bureau", "Clé du bureau", "Bureau");
			meubleBureau.setCleRequise(cleBur);

			// Placer la clé du bureau ailleurs
			List<Zone> cachettesCleBureau = new ArrayList<>(zonesSansCave);
			cachettesCleBureau.remove(bureau);

			boolean placee = false;
			while (!placee) {
				Zone z = cachettesCleBureau.get(random.nextInt(cachettesCleBureau.size()));
				if (z.getNombreObjetsSurSol() < 2) {
					z.ajouteObjet(cleBur);
					placee = true;
				}
			}

			armoire.setVerrouille(false);
			armoire.setCleRequise(null);
			cachetteGagnante = bureau;
		} else {
			// L'armoire est verrouillée
			meubleBureau.setVerrouille(false);
			meubleBureau.setCleRequise(null);

			armoire.setVerrouille(true);
			Cle cleArm = new Cle("cle_armoire", "Clé de l'armoire", "Armoire");
			armoire.setCleRequise(cleArm);

			objetsPourLeSol.add(cleArm);
			cachetteGagnante = armoire;
		}

		// Distribuer les objets sur le sol
		randomiseur.distribuerObjetsSurSol(objetsPourLeSol, zonesSansCave);

		// Clés des coffres
		Cle cleCoffre1 = new Cle("cle_coffre_1", "Clé de coffre", "Coffre");
		Cle cleCoffre2 = new Cle("cle_coffre_2", "Clé de coffre", "Coffre");

		if (random.nextBoolean()) {
			chambre1.getConteneur("Coffre").setCleRequise(cleCoffre1);
			chambre2.getConteneur("Coffre").setCleRequise(cleCoffre2);
		} else {
			chambre1.getConteneur("Coffre").setCleRequise(cleCoffre2);
			chambre2.getConteneur("Coffre").setCleRequise(cleCoffre1);
		}

		List<Object> cachettesPossibles = new ArrayList<>(zonesSansCave);
		cachettesPossibles.add(cachetteGagnante);
		placerCleCoffre(cleCoffre1, cachettesPossibles, random);
		placerCleCoffre(cleCoffre2, cachettesPossibles, random);

		// Fragments d'âme
		List<TypeFragment> types = randomiseur.choisir3FragmentsParmi5();
		cave.getConteneur("CorpsBaron")
				.ajouteObjet(new ObjetMaudit(types.get(0).name().toLowerCase(), "Fragment", types.get(0)));

		List<Object> cachettesFragments = new ArrayList<>();
		cachettesFragments.add(chambre1.getConteneur("Coffre"));
		cachettesFragments.add(chambre2.getConteneur("Coffre"));
		cachettesFragments.add(armoire);

		Collections.shuffle(cachettesFragments);

		ObjetMaudit fragment1 = new ObjetMaudit(types.get(1).name().toLowerCase(), "Fragment", types.get(1));
		ObjetMaudit fragment2 = new ObjetMaudit(types.get(2).name().toLowerCase(), "Fragment", types.get(2));

		// Placement des fragments (peut être une zone ou un conteneur)
		Object cachette1 = cachettesFragments.get(0);
		if (cachette1 instanceof Zone) {
			((Zone) cachette1).ajouteObjet(fragment1);
		} else if (cachette1 instanceof Conteneur) {
			((Conteneur) cachette1).ajouteObjet(fragment1);
		}

		Object cachette2 = cachettesFragments.get(1);
		if (cachette2 instanceof Zone) {
			((Zone) cachette2).ajouteObjet(fragment2);
		} else if (cachette2 instanceof Conteneur) {
			((Conteneur) cachette2).ajouteObjet(fragment2);
		}

		//voirPositionObjetConsole(toutesLesZones);
	}

	/**
	 * Cherche une cachette valide pour placer une clé de coffre.
	 *
	 * @param cle     clé à placer
	 * @param cachettes liste de zones ou conteneurs possibles
	 * @param random  générateur aléatoire
	 */
	private void placerCleCoffre(Cle cle, List<Object> cachettes, Random random) {
		boolean placee = false;
		while (!placee) {
			Object cachette = cachettes.get(random.nextInt(cachettes.size()));
			if (cachette instanceof Zone) {
				Zone z = (Zone) cachette;
				if (z.getNombreObjetsSurSol() < 2) {
					z.ajouteObjet(cle);
					placee = true;
				}
			} else if (cachette instanceof Conteneur) {
				((Conteneur) cachette).ajouteObjet(cle);
				placee = true;
			}
		}
	}

	
	public void setGUI(GUI g) {
		gui = g;
	}

	private void verifieGUI() {
		if (gui == null) {
			throw new IllegalStateException("GUI non initialisée !");
		}
	}

	private void afficherLocalisation() {
		verifieGUI();
		gui.afficher(zoneCourante.descriptionLongue());
		if (zoneCourante.getNom().equalsIgnoreCase("bibliothèque")) {
			if (eclairageActif) {
				gui.afficher(
						"\n Votre regard est attiré par un livre étrange dont la reliure dépasse légèrement des autres sur l'étagère...");
			}
		}
		gui.afficher();
	}

	private void afficherMessageDeBienvenue() {
		verifieGUI();
		gui.afficher("Bienvenue dans l'Héritage Maudit !");
		gui.afficher();
		gui.afficher(
				"Le manoir est plongé dans une obscurité glaciale. Tant que la lumière est éteinte, les objets dissimulés sur le sol resteront invisibles à vos yeux...");
		gui.afficher();
		gui.afficher("Tapez '?' pour obtenir de l'aide.");
		gui.afficher();
		afficherLocalisation();
		afficherCommandesPossibles();
		gui.afficher();
		gui.afficheImage(zoneCourante.nomImage());
	}

	private void afficherCommandesPossibles() {
		if (!eclairageActif && !zoneCourante.getNom().equalsIgnoreCase("salon")) {
			gui.afficher(
					"Il fait nuit noire... Les commandes sont invisibles. Vous pouvez seulement tâtonner pour vous déplacer (N, S, E, O).");
			return;
		}

		List<String> cmds = new ArrayList<>();
		cmds.add("Déplacement (N, S, E, O)");
		cmds.add("Retour (R)");
		cmds.add("Inventaire (I)");
		cmds.add("Prendre (P) / Déposer (D)");
		cmds.add("Ouvrir (OU)");
		cmds.add("Quitter (Q)");
		cmds.add("Abandonner (A)");
		cmds.add("Sauvegarder (SAUV)");

		String nomZone = zoneCourante.getNom().toLowerCase();
		if (nomZone.equals("salon")) {
			if (!eclairageActif) {
				cmds.add("Commande 1 (CMD1)");
				cmds.add("Commande 2 (CMD2)");
			} else {
				cmds.remove("Commande 1 (CMD1)");
				cmds.remove("Commande 2 (CMD2)");
				if (!chemineActif) {
					cmds.add("Allumer feu (AF)");
				} else {
					cmds.add("Brûler objet maudit (B)");
				}
			}
			if (nomZone.equals("la salle de bain")) {
				cmds.add("Miroir (M)");
			}
		}
		cmds.add("Ouvrir (OU)");

		gui.afficher("Commandes disponibles : " + String.join(", ", cmds));
	}

	/**
	 * Traite une chaîne saisie par le joueur et exécute la commande correspondante.
	 *
	 * @param texteSaisi texte complet entré par le joueur
	 */
	public void traiterCommande(String texteSaisi) {
		verifieGUI();
		gui.afficher("> " + texteSaisi + "\n");

		String[] mots = texteSaisi.trim().split(" ", 2);
		String commande = mots[0].toUpperCase();
		String argument = (mots.length > 1) ? mots[1] : "";

		if (enMenuAccueil) {
			gererMenuAccueil(texteSaisi.trim());
			return;
		}

		if (this.etatJeu != EtatJeu.EN_COURS) {
			if (commande.equals("OUI") || commande.equals("O")) {
				relancerPartie();
			} else if (commande.equals("NON") || commande.equals("N") || commande.equals("QUITTER")
					|| commande.equals("Q")) {
				terminer();
			} else {
				gui.afficher("La partie est terminée. Voulez-vous recommencer ? (OUI / NON)");
			}
			return;
		}

		if (this.enigmeEnCours != null) {
			if (commande.equals("REP") || commande.equals("REPONDRE")) {
				traiterReponse(argument);
			} else {
				gui.afficher("Le fantôme du Baron bloque votre chemin. Vous ne pouvez rien faire d'autre !");
				gui.afficher("Le Baron murmure à nouveau : " + enigmeEnCours.getQuestion());
				gui.afficher("(Tapez REPONDRE <votre_texte>)");
			}
			return;
		}

		switch (commande) {
		case "?", "AIDE" -> afficherAide();
		case "R", "RETOUR" -> retour();
		case "N", "NORD" -> allerEn(Direction.NORD);
		case "S", "SUD" -> allerEn(Direction.SUD);
		case "E", "EST" -> allerEn(Direction.EST);
		case "O", "OUEST" -> allerEn(Direction.OUEST);
		case "B", "BRULER" -> bruler();
		case "P", "PRENDRE" -> {
			if (argument.isEmpty()) {
				if (!eclairageActif && !zoneCourante.getNom().equalsIgnoreCase("salon")) {
					gui.afficher("Prendre quoi ? (Il fait trop sombre pour distinguer les objets au sol...)");
				} else {
					gui.afficher("Prendre quoi ?\nObjets visibles au sol : " + zoneCourante.listerObjets());
				}
			} else {
				prendreObjet(argument);
			}
		}
		case "OU", "OUVRIR" -> {
			if (argument.isEmpty())
				gui.afficher("Ouvrir quoi ? (ex: OUVRIR coffre, OUVRIR livre, OUVRIR tiroir, OUVRIR corpsBaron)");
			else
				ouvrir(argument);
		}
		case "CMD1", "COMMANDE 1" -> allumerLumiere();
		case "AF", "ALLUMER_FEU" -> allumerFeu();
		case "CMD2", "COMMANDE 2" -> allumerFeu();
		case "I", "INVENTAIRE" -> ouvrirInventaire();
		case "M", "MIROIR" -> utiliserLeMiroir();
		case "REP", "REPONDRE" -> {
			if (argument.isEmpty()) {
				gui.afficher("Que voulez-vous répondre ?");
			} else {
				traiterReponse(argument);
			}
		}
		case "TP", "TELEPORTER" -> teleporter(argument);
		case "SAUV", "SAUVER" -> {
			EtatPartie etat = new EtatPartie().capturer(this);
			if (gestionnaireSauvegarde.sauvegarderPartie(etat, joueur.getPseudo())) {
				gui.afficher("Partie sauvegardée avec succès dans joueur_" + joueur.getPseudo() + ".json !");
			} else {
				gui.afficher("Erreur lors de la sauvegarde.");
			}
		}
		case "T", "TEST" -> {
			if (zoneCourante.getNom().equalsIgnoreCase("salon")) {
				testPartieGagnante();
			} else {
				gui.afficher("Il faut être dans le salon pour activer le test");
			}
		}
		case "AB", "ABANDON" -> abandonSansSauv();
		case "Q", "QUITTER" -> terminer();
		default -> gui.afficher("Commande inconnue");
		}
	}

	private void relancerPartie() {
		gui.afficher("========== NOUVELLE PARTIE =========");

		this.joueur = new Joueur("Elias Cole");
		this.historiqueZones.clear();
		this.manoir = new Manoir();
		this.eclairageActif = false;
		this.chemineActif = false;
		this.miroirActive = false;
		this.fragmentsDetruits = 0;
		this.etatJeu = EtatJeu.EN_COURS;

		initialiserObjets();

		this.zoneCourante = manoir.getZoneDepart();
		afficherMessageDeBienvenue();
	}

	private void abandonSansSauv() {
		this.etatJeu = EtatJeu.DEFAITE;
		gui.afficher("Vous avez abandonné. L'esprit du Baron Cole a eu raison de vous...");
		finDePartie();
	}

	private void testPartieGagnante() {
		gui.afficher("Lancement de la séquence de TEST automatique...");
		TestAutomatique testeur = new TestAutomatique();
		testeur.executerSequenceVictoire(this);
	}

	private void ouvrir(String argument) {
		if (zoneCourante.getNom().equalsIgnoreCase("bibliothèque") && argument.equalsIgnoreCase("livre")) {
			zoneCourante.revelerSortieCachee("Livre", Direction.SUD);
			gui.afficher(
					"Vous tirez un livre étrange... L'étagère pivote dans un grincement sourd, révélant un escalier qui descend vers le SUD ! (La Cave est maintenant accessible)");
			afficherCommandesPossibles();
			rafraichirImage();
			return;
		}

		Conteneur conteneur = zoneCourante.getConteneur(argument);

		if (conteneur == null) {
			gui.afficher("Il n'y a pas de '" + argument + "' à ouvrir ici.");
			return;
		}

		if (conteneur.getNom().equalsIgnoreCase("CorpsBaron")) {
			if (conteneur.estVerrouille()) {
				gui.afficher("Une aura glaciale vous repousse. Le fantôme du Baron apparaît !");
				gui.afficher("Il ne vous laissera pas approcher de son corps si facilement...");

				enigmeEnCours = banqueEnigmes.obtientEnigmeAleatoire();
				if (enigmeEnCours != null) {
					gui.afficher("Le Baron murmure : " + enigmeEnCours.getQuestion());
					gui.afficher("(Tapez votre réponse avec : REPONDRE <votre_texte>)");
				}
				return;
			}
		}

		if (conteneur.estVerrouille()) {
			boolean cleTrouvee = false;
			for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
				if (obj instanceof Cle) {
					Cle cle = (Cle) obj;
					if (conteneur.deverrouillerAvecCle(cle, joueur)) {
						cleTrouvee = true;
						gui.afficher("CLIC ! Vous avez déverrouillé et ouvert : " + argument + " avec la "
								+ cle.getNom() + " !");
						joueur.getInventaire().retire(cle.getNom());
						revelerContenu(conteneur);
						rafraichirImage();
						break;
					}
				}
			}

			if (!cleTrouvee) {
				if (conteneur.getNom().equalsIgnoreCase("Coffre")) {
					joueur.perdreVie();
					gui.afficher(
							"Vous tentez de forcer la serrure... Un mécanisme se déclenche ! Piège ! Il vous reste "
									+ joueur.getVies() + " vies.");
					if (joueur.getVies() <= 0) {
						finDePartie();
					}
				} else {
					gui.afficher("C'est verrouillé. Il vous faut la bonne clé pour ouvrir " + argument + ".");
				}
			}
		} else {
			conteneur.setEstOuvert(true);
			gui.afficher("Vous ouvrez " + argument + ".");
			revelerContenu(conteneur);
			rafraichirImage();
		}
	}

	public void traiterReponse(String reponseJoueur) {
		if (enigmeEnCours == null) {
			gui.afficher("Il n'y a aucune énigme à résoudre pour le moment.");
			return;
		}

		if (enigmeEnCours.verifierReponse(reponseJoueur)) {
			gui.afficher("Le Baron pousse un hurlement et s'évapore... Le passage est libre.");
			Conteneur corpsBaron = zoneCourante.getConteneur("CorpsBaron");
			if (corpsBaron != null) {
				corpsBaron.setVerrouille(false);
				revelerContenu(corpsBaron);
				rafraichirImage();
			}
			this.enigmeEnCours = null;
		} else {
			gui.afficher("Le Baron ricane : 'Ce n'est pas la bonne réponse...' L'aura vous glace le sang.");
			joueur.perdreVie();
			gui.afficher(
					" Piège ! Il vous reste "
							+ joueur.getVies() + " vies.");
			if (joueur.getVies() <= 0) {
				finDePartie();
			}
		}
	}

	private void revelerContenu(Conteneur conteneur) {
		if (conteneur.getContenu().isEmpty()) {
			gui.afficher("Vous regardez à l'intérieur... c'est vide.");
			return;
		}

		List<String> objetsPris = new ArrayList<>();
		Iterator<ObjetJeu> it = conteneur.getContenu().iterator();
		while (it.hasNext()) {
			ObjetJeu obj = it.next();
			if (!joueur.getInventaire().estPlein()) {
				joueur.getInventaire().ajoute(obj);
				objetsPris.add(obj.getNom());
				it.remove();
			} else {
				gui.afficher("Votre sac est plein ! Certains objets sont restés dans : " + conteneur.getNom() + ".");
				break;
			}
		}

		if (!objetsPris.isEmpty()) {
			gui.afficher("Vous trouvez et prenez automatiquement : " + String.join(", ", objetsPris) + ".");
			rafraichirImage();
		}
	}

	private void prendreObjet(String nomObjet) {
		if (joueur.getInventaire().estPlein()) {
			gui.afficher("Votre sac à dos est plein ! Capacité maximale de 5 objets atteinte.");
			return;
		}

		ObjetJeu objet = zoneCourante.retireObjet(nomObjet);
		if (objet != null) {
			joueur.getInventaire().ajoute(objet);
			gui.afficher("Vous avez pris : " + nomObjet + ".");
			gui.afficher(
					"Le fantome du baron est present et peut s'amuser a deplacer les autres objet présent dans la piece");
			rafraichirImage();
		} else {
			gui.afficher("Cet objet n'est pas ici.");
		}
	}

	private void utiliserLeMiroir() {
		if (!zoneCourante.getNom().equalsIgnoreCase("salle_de_bain")) {
			gui.afficher("Il n'y a pas de miroir magique ici.");
			return;
		}

		boolean aMedaillon = false;
		for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
			if (obj.getNom().toLowerCase().contains("medaillon") || obj.getNom().toLowerCase().contains("médaillon")) {
				aMedaillon = true;
				break;
			}
		}

		if (!aMedaillon) {
			gui.afficher("Le miroir reste sombre. Une force ancienne semble exiger un artefact pour s'éveiller...");
			return;
		}

		this.miroirActive = true;
		gui.afficher("Vous présentez le Médaillon. La surface du miroir se met à onduler comme de l'eau !");

		List<Zone> visitees = getZonesVisitees();
		List<String> noms = new ArrayList<>();
		for (Zone z : visitees)
			noms.add(z.getNom());

		gui.afficher("Destinations possibles : " + String.join(", ", noms));
		gui.afficher("(Tapez TELEPORTER <nom>)");

		afficherCommandesPossibles();
	}

	private void teleporter(String destinationVoulue) {
		if (!zoneCourante.getNom().equalsIgnoreCase("salle_de_bain")) {
			gui.afficher("Vous ne pouvez pas invoquer la magie du miroir en dehors de la salle de bain.");
			return;
		}

		if (!miroirActive) {
			gui.afficher("Le miroir est éteint. Vous devez d'abord utiliser le miroir (M) avec le médaillon.");
			return;
		}

		if (destinationVoulue.isEmpty()) {
			gui.afficher("Où voulez-vous vous téléporter ? (Ex: TELEPORTER salon)");
			return;
		}

		String nomFormate = destinationVoulue.replace(" ", "_");

		Zone destinationFinale = null;
		for (Zone z : getZonesVisitees()) {
			if (z.getNom().equalsIgnoreCase(nomFormate) || z.getNom().equalsIgnoreCase(destinationVoulue)) {
				destinationFinale = z;
				break;
			}
		}

		if (destinationFinale == null) {
			gui.afficher(
					"Le miroir est flou... Vous ne pouvez vous téléporter que vers une pièce déjà visitée, et le nom doit être exact.");
			return;
		}

		if (destinationFinale == zoneCourante) {
			gui.afficher("Vous y êtes déjà !");
			return;
		}

		gui.afficher("Vous touchez la surface du miroir... Le monde tourne autour de vous !");

		historiqueZones.push(zoneCourante);
		zoneCourante = destinationFinale;

		afficherLocalisation();
		afficherCommandesPossibles();
		gui.afficher();
		rafraichirImage();
	}

	private void finDePartie() {
		if (joueur.getVies() <= 0) {
			this.etatJeu = EtatJeu.DEFAITE;
			gui.afficher("Vous n'avez plus de vies. Vous rejoignez les fantômes du manoir...");
		} else if (zoneCourante.getNom().equalsIgnoreCase("la cave") && !joueur.getInventaire().possede("Echelle")) {
			this.etatJeu = EtatJeu.DEFAITE;
			gui.afficher("Vous êtes bloqué dans la cave sans échelle. Personne ne viendra vous chercher...");
		} else if (this.etatJeu == EtatJeu.DEFAITE) {
			gui.afficher("Fin de la partie.");
		}

		if (this.etatJeu == EtatJeu.DEFAITE || this.etatJeu != EtatJeu.VICTOIRE) {
			gui.afficher("--- GAME OVER ---");
		}
		gui.afficher("La partie est terminée. Voulez-vous recommencer ? (OUI / NON)");
	}

	private void ouvrirInventaire() {
		if (joueur.getInventaire().estVide()) {
			gui.afficher("Votre inventaire est vide.");
		} else {
			gui.afficher("Contenu de votre sac à dos :\n" + joueur.getInventaire().listerObjets());
		}
	}

	private void afficherAide() {
		verifieGUI();
		gui.afficher("Etes-vous perdu ?");
		gui.afficher();
		gui.afficher("Les commandes autorisées sont :");
		gui.afficher();
		gui.afficher(Commande.toutesLesDescriptions().toString());
		gui.afficher();
	}

	private void allerEn(Direction direction) {
		verifieGUI();

		if (zoneCourante.getNom().equalsIgnoreCase("chambre1") && direction == Direction.SUD) {
			Zone pieceBureau = manoir.obtientZone("bureau");
			Conteneur porteVerrouillee = pieceBureau.getConteneur("Bureau");

			if (porteVerrouillee != null && porteVerrouillee.estVerrouille()) {
				boolean cleTrouvee = false;
				for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
					if (obj instanceof Cle) {
						Cle cle = (Cle) obj;
						if (porteVerrouillee.deverrouillerAvecCle(cle, joueur)) {
							cleTrouvee = true;
							joueur.getInventaire().retire(cle.getNom());
							gui.afficher("CLIC ! Vous déverrouillez la porte du bureau avec la " + cle.getNom()
									+ " et vous entrez.");
							break;
						}
					}
				}

				if (!cleTrouvee) {
					gui.afficher(
							"La porte menant au SUD (le bureau) est fermée à clé. Il vous faut la bonne clé pour entrer.");
					return;
				}
			}
		}

		Zone nouvelle = zoneCourante.obtientSortie(direction);
		if (nouvelle == null) {
			gui.afficher("Pas de sortie au " + direction);
			gui.afficher();
		} else {
			historiqueZones.push(zoneCourante);
			zoneCourante = nouvelle;
			if (zoneCourante.getNom().equalsIgnoreCase("cave")) {
				boolean possedeEchelle = false;
				for (jeu.objets.ObjetJeu obj : joueur.getInventaire().getObjets()) {
					if (obj.getNom().toLowerCase().contains("echelle")
							|| obj.getNom().toLowerCase().contains("échelle")) {
						possedeEchelle = true;
						break;
					}
				}

				if (!possedeEchelle) {
					gui.afficher(zoneCourante.descriptionLongue());
					gui.afficheImage(zoneCourante.nomImage());

					this.etatJeu = EtatJeu.DEFAITE;
					gui.afficher("Vous avez sauté dans la cave... mais la trappe se referme au-dessus de vous !");
					gui.afficher("Sans échelle pour remonter, vous êtes coincé ici pour l'éternité.");
					finDePartie();
					return;
				}
			}
			afficherLocalisation();
			gui.afficher();
			afficherCommandesPossibles();
			gui.afficher();
			gui.afficheImage(zoneCourante.nomImage());
			rafraichirImage();
		}
	}

	private void retour() {
		verifieGUI();
		if (historiqueZones.isEmpty()) {
			gui.afficher("Pas de retour possible.");
			gui.afficher();
		} else if (!retourEstPossible()) {
			gui.afficher("Retour impossible : la communication n'est pas bidirectionnelle");
			gui.afficher();
		} else {
			zoneCourante = historiqueZones.pop();
			afficherLocalisation();
			gui.afficher();
			afficherCommandesPossibles();
			gui.afficher();
			gui.afficheImage(zoneCourante.nomImage());
			rafraichirImage();
		}
	}

	private boolean retourEstPossible() {
		Zone zonePrecedente = historiqueZones.peek();
		for (Direction dir : Direction.values()) {
			if (zoneCourante.obtientSortie(dir) == zonePrecedente) {
				return true;
			}
		}
		return false;
	}

	private void bruler() {
		if (!zoneCourante.getNom().equalsIgnoreCase("salon")) {
			gui.afficher("Vous ne pouvez brûler des objets que dans la cheminée du salon !");
			return;
		}

		if (!chemineActif) {
			gui.afficher("Vous ne pouvez brûler des objets que si la cheminée est allumée !");
			return;
		}

		if (joueur.getInventaire().estVide()) {
			gui.afficher("Il n'y a aucun objet dans votre sac.");
			return;
		}

		int nbObjetBruleCetteFois = 0;
		Iterator<ObjetJeu> it = joueur.getInventaire().getObjets().iterator();
		while (it.hasNext()) {
			ObjetJeu obj = it.next();
			if (obj.estFragment()) {
				String nom = obj.getNom();
				it.remove();
				fragmentsDetruits++;
				nbObjetBruleCetteFois++;
				gui.afficher(nom + " a été purifié dans les flammes.");
			}
		}

		if (nbObjetBruleCetteFois == 0) {
			gui.afficher("Aucun objet maudit à brûler dans votre inventaire.");
		}

		if (fragmentsDetruits >= 3) {
			this.etatJeu = EtatJeu.VICTOIRE;
			gui.afficher(
					"Félicitations ! Les 3 fragments d'âme ont été détruits. Le Baron Cole est libéré. Vous avez gagné !");
			gui.afficheImage("salon/salon_victoire");
			finDePartie();
		}
	}

	private void allumerLumiere() {
		if (!zoneCourante.getNom().equalsIgnoreCase("salon")) {
			gui.afficher("Vous ne pouvez allumer la lumière que depuis l'interrupteur du salon !");
			return;
		}
		eclairageActif = true;
		gui.afficher("Clic ! La lumière inonde le manoir. Vous y voyez beaucoup plus clair.");
		afficherCommandesPossibles();
		rafraichirImage();
	}

	private void allumerFeu() {
		if (!zoneCourante.getNom().equalsIgnoreCase("salon")) {
			gui.afficher("Vous ne pouvez allumer le feu que dans la cheminée du salon !");
			return;
		}

		ObjetJeu bois = null;
		ObjetJeu allumettes = null;
		for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
			if (obj.getNom().equalsIgnoreCase("bois"))
				bois = obj;
			if (obj.getNom().equalsIgnoreCase("allumettes"))
				allumettes = obj;
		}

		if (bois != null && allumettes != null) {
			joueur.getInventaire().getObjets().remove(bois);
			joueur.getInventaire().getObjets().remove(allumettes);
			chemineActif = true;
			gui.afficher(
					"Vous craquez une allumette et enflammez le bois. La cheminée est prête pour la purification.");
			rafraichirImage();
		} else {
			gui.afficher(
					"Il vous manque des éléments. Vous avez besoin de bois et d'allumettes dans votre inventaire.");
		}
		afficherCommandesPossibles();
	}

	private void terminer() {
		verifieGUI();
		gui.afficher("Au revoir...");
		gui.enable(false);
		System.exit(0);
	}

	public GUI getGui() {
		return this.gui;
	}

	private void rafraichirImage() {
		verifieGUI();
		String nomImageBase = genererNomImageBase();
		gui.afficheImage(nomImageBase);
	}

	private String genererNomImageBase() {
		StringBuilder builder = new StringBuilder();
		String nomZone = zoneCourante.getNom().toLowerCase();
		String nomDossier = nomZone.replace(" ", "_");
		builder.append(nomDossier).append("/");

		switch (nomZone) {
		case "bibliothèque":
			builder.append("bibliothèque");
			boolean passageOuvert = (zoneCourante.obtientSortie(Direction.SUD) != null);
			builder.append(passageOuvert ? "_passageOuvert" : "_passageFermé");
			builder.append(eclairageActif ? "_ON" : "_OFF");
			break;

		case "bureau":
			builder.append("bureau");
			Conteneur armoire = zoneCourante.getConteneur("Armoire");
			boolean placardOuvert = (armoire != null && armoire.estOuvert());
			builder.append(placardOuvert ? "_placard_ouvert" : "_placard_fermé");
			builder.append(eclairageActif ? "_ON" : "_OFF");
			break;

		case "chambre1":
		case "chambre2":
			builder.append(nomZone);
			builder.append(eclairageActif ? "_ON" : "_OFF");
			Conteneur coffre = zoneCourante.getConteneur("Coffre");
			boolean coffreOuvert = (coffre != null && coffre.getContenu().isEmpty());
			if (eclairageActif) {
				builder.append(coffreOuvert ? "_coffre_ON" : "_coffre_OFF");
			}
			break;

		case "salon":
			builder.append("salon");
			builder.append(eclairageActif ? "_ON" : "_OFF");
			builder.append(chemineActif ? "_chemine_ON" : "_chemine_OFF");
			break;

		case "cave":
			builder.append("cave");
			builder.append(eclairageActif ? "_ON" : "_OFF");
			Conteneur corps = zoneCourante.getConteneur("CorpsBaron");
			if (corps != null && !corps.getContenu().isEmpty()) {
				String nomObjBaron = corps.getContenu().get(0).getNom().toLowerCase();
				if (nomObjBaron.contains("journal"))
					builder.append("_journal");
				else if (nomObjBaron.contains("medaillon") || nomObjBaron.contains("médaillon"))
					builder.append("_medaillon");
				else if (nomObjBaron.contains("montre"))
					builder.append("_montre");
				else if (nomObjBaron.contains("pipe"))
					builder.append("_pipe");
				else if (nomObjBaron.contains("plume"))
					builder.append("_plume");
			}
			return builder.toString();

		default:
			builder.append(nomZone.replace(" ", "_"));
			builder.append(eclairageActif ? "_ON" : "_OFF");
			break;
		}

		if (eclairageActif) {
			List<ObjetJeu> objetsAuSol = zoneCourante.getObjetsPresents();
			if (objetsAuSol != null && !objetsAuSol.isEmpty()) {
				List<String> motsClefs = new ArrayList<>();
				for (ObjetJeu obj : objetsAuSol) {
					String nom = obj.getNom().toLowerCase();
					if (nom.contains("bois"))
						motsClefs.add("bois");
					else if (nom.contains("cle") || nom.contains("clé"))
						motsClefs.add("cle");
					else if (nom.contains("echelle") || nom.contains("échelle"))
						motsClefs.add("echelle");
					else if (nom.contains("medaillon") || nom.contains("médaillon"))
						motsClefs.add("medaillon");
				}
				Collections.sort(motsClefs);
				for (String mot : motsClefs) {
					builder.append("_").append(mot);
				}
			}
		}

		return builder.toString();
	}

	private List<Zone> getZonesVisitees() {
		List<Zone> visitees = new ArrayList<>();
		visitees.add(zoneCourante);
		for (Zone z : historiqueZones) {
			if (!visitees.contains(z)) {
				visitees.add(z);
			}
		}
		return visitees;
	}

	public EtatJeu getEtatJeu() {
		return this.etatJeu;
	}

	/*
	public void voirPositionObjetConsole(List<Zone> toutesLesZones) {
		System.out.println("\n=== RÉCAPITULATIF DES EMPLACEMENTS (TRICHE) ===");
		for (Zone z : toutesLesZones) {
			String objetsSol = z.listerObjets();
			if (!objetsSol.equals("Il n'y a aucun objet visible ici.")) {
				System.out.println("[SOL] " + z.getNom() + " -> " + objetsSol);
			}

			for (Conteneur c : z.getConteneurs()) {
				if (!c.getContenu().isEmpty()) {
					java.util.List<String> nomsObjets = new java.util.ArrayList<>();
					for (ObjetJeu obj : c.getContenu()) {
						nomsObjets.add(obj.getNom());
					}
					System.out.println("[MEUBLE] " + z.getNom() + " (dans " + c.getNom() + ") -> "
							+ String.join(", ", nomsObjets));
				}
			}
		}
		System.out.println("===============================================\n");
	}
	 */
	private void gererMenuAccueil(String reponse) {
		if (etapeMenu.equals("DEMANDER_NOM")) {
			if (reponse.isEmpty()) {
				gui.afficher("Le nom ne peut pas être vide. Quel est votre nom ?");
				return;
			}
			this.nomJoueurTemp = reponse;

			if (gestionnaireSauvegarde.verifierExistenceFichier(nomJoueurTemp)) {
				gui.afficher("Une sauvegarde a été trouvée pour '" + nomJoueurTemp + "'.");
				gui.afficher("Voulez-vous la charger ? (OUI / NON)");
				etapeMenu = "CONFIRMER_CHARGEMENT";
			} else {
				gui.afficher("Création d'un nouveau profil pour '" + nomJoueurTemp + "'...");
				lancerLaPartie(nomJoueurTemp, false);
			}

		} else if (etapeMenu.equals("CONFIRMER_CHARGEMENT")) {
			if (reponse.equalsIgnoreCase("OUI") || reponse.equalsIgnoreCase("O")) {
				lancerLaPartie(nomJoueurTemp, true);
			} else if (reponse.equalsIgnoreCase("NON") || reponse.equalsIgnoreCase("N")) {
				gui.afficher("Création d'une nouvelle partie (la sauvegarde sera écrasée à la prochaine sauvegarde).");
				lancerLaPartie(nomJoueurTemp, false);
			} else {
				gui.afficher("Veuillez répondre par OUI ou NON.");
			}
		}
	}

	private void lancerLaPartie(String nom, boolean chargerSauvegarde) {
		this.enMenuAccueil = false;
		this.joueur = new jeu.joueur.Joueur(nom);

		this.historiqueZones = new ArrayDeque<>();
		this.manoir = new Manoir();
		this.fragmentsDetruits = 0;
		initialiserObjets();
		this.zoneCourante = manoir.getZoneDepart();
		this.etatJeu = EtatJeu.EN_COURS;

		if (chargerSauvegarde) {
			EtatPartie etat = gestionnaireSauvegarde.chargerPartie(nom);
			if (etat != null) {
				etat.restaurer(this);
				gui.afficher("Partie chargée avec succès ! Bon retour, " + nom + ".");
			} else {
				gui.afficher("Erreur lors du chargement. Démarrage d'une nouvelle partie...");
				afficherMessageDeBienvenue();
			}
		} else {
			afficherMessageDeBienvenue();
		}
		gui.afficher(zoneCourante.descriptionLongue());
		rafraichirImage();
	}

	public Zone getZoneCourante() {
		return zoneCourante;
	}

	public Joueur getJoueur() {
		return joueur;
	}

	public boolean isEclairageActif() {
		return eclairageActif;
	}

	public int getFragmentsDetruits() {
		return fragmentsDetruits;
	}

	public void setFragmentsDetruits(int fragmentsDetruits) {
		this.fragmentsDetruits = fragmentsDetruits;
	}

	public Manoir getManoir() {
		return manoir;
	}

	public void setZoneCourante(Zone zoneCourante) {
		this.zoneCourante = zoneCourante;
	}

	public void setEclairageActif(boolean eclairageActif) {
		this.eclairageActif = eclairageActif;
	}

	public boolean isChemineActif() {
		return chemineActif;
	}

	public void setChemineActif(boolean chemineActif) {
		this.chemineActif = chemineActif;
	}
}