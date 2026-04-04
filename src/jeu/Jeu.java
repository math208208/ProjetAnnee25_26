package jeu;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jeu.environnement.Conteneur;
import jeu.environnement.Manoir;
import jeu.environnement.Zone;
import jeu.joueur.Joueur;
import jeu.objets.*;
import jeu.util.Randomiseur;
import jeu.util.TestAutomatique;

public class Jeu {

	private GUI gui;
	private Zone zoneCourante;
	private ArrayDeque<Zone> historiqueZones;
	private Joueur joueur;
	private List<TypeFragment> fragmentsADetruire;
	private int fragmentsDetruits = 0;
	private EtatJeu etatJeu;
	private Manoir manoir;

	private boolean eclairageActif = false;
	private boolean chemineActif = false;
	private boolean miroirActive = false;
	public Jeu() {
		// IMPORTANT : Initialiser le joueur pour éviter un NullPointerException
		joueur = new Joueur("Elias Cole");
		historiqueZones = new ArrayDeque<>();
		this.manoir = new Manoir();
		initialiserObjets();
		this.zoneCourante = manoir.getZoneDepart();
		gui = null;
		this.etatJeu = EtatJeu.EN_COURS;
	}

	private void initialiserObjets() {
		System.out.println("=== PLACEMENT DES OBJETS (AVEC RANDOMISEUR) ===");

		Random rand = new Random();
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

		// ---------------------------------------------------------
		// 1. LES OBJETS FIXES
		// ---------------------------------------------------------
		cuisine.getConteneur("Tiroir").ajouteObjet(new Allumettes("allumettes", "Boîte d'allumettes"));
		bibliotheque.ajouteObjet(new MorceauBois("bois", "Morceau de bois"));

		// ---------------------------------------------------------
		// 2. PRÉPARATION DES OBJETS À DISTRIBUER SUR LE SOL
		// ---------------------------------------------------------
		List<ObjetJeu> objetsPourLeSol = new ArrayList<>();

		objetsPourLeSol.add(new Echelle("echelle", "Une échelle"));
		objetsPourLeSol.add(new MedaillonMagique("medaillonMagique", "Médaillon magique"));

		// --- Mécanique Bureau / Armoire ---
		Conteneur meubleBureau = bureau.getConteneur("Bureau"); // Majuscule c'est plus sûr
		Conteneur armoire = bureau.getConteneur("Armoire");
		Object cachetteGagnante = null; // <-- LA NOUVELLE VARIABLE EST ICI

		if (rand.nextBoolean()) {
			// CAS 1 : LE BUREAU EST VERROUILLÉ
			meubleBureau.setVerrouille(true);
			Cle cleBur = new Cle("cle_bureau", "Clé du bureau", "Bureau");
			meubleBureau.setCleRequise(cleBur);
			
			// Placement sécurisé de la clé du bureau (qui exclut le bureau)
			List<Zone> cachettesCleBureau = new ArrayList<>(zonesSansCave);
			cachettesCleBureau.remove(bureau); 
			
			boolean placee = false;
			while (!placee) {
				Zone z = cachettesCleBureau.get(rand.nextInt(cachettesCleBureau.size()));
				if (z.getNombreObjetsSurSol() < 2) {
					z.ajouteObjet(cleBur);
					placee = true;
				}
			}

			armoire.setVerrouille(false);
			armoire.setCleRequise(null);
			
			// ICI ON DÉSIGNE LA PIÈCE COMME CACHETTE !
			cachetteGagnante = bureau; 

		} else {
			// CAS 2 : L'ARMOIRE EST VERROUILLÉE
			meubleBureau.setVerrouille(false);
			meubleBureau.setCleRequise(null);

			armoire.setVerrouille(true);
			Cle cleArm = new Cle("cle_armoire", "Clé de l'armoire", "Armoire");
			armoire.setCleRequise(cleArm);
			
			objetsPourLeSol.add(cleArm); 
			
			// ICI ON DÉSIGNE LE MEUBLE COMME CACHETTE !
			cachetteGagnante = armoire; 
		}
		
		// === APPEL DU RANDOMISEUR POUR TOUT PLACER ===
		randomiseur.distribuerObjetsSurSol(objetsPourLeSol, zonesSansCave);

		// ---------------------------------------------------------
		// 3. LES CLÉS DES COFFRES (Règle spéciale : Sol OU Armoire)
		// ---------------------------------------------------------
		Cle cleCoffre1 = new Cle("cle_coffre_1", "Clé de coffre", "Coffre");
		Cle cleCoffre2 = new Cle("cle_coffre_2", "Clé de coffre", "Coffre");

		if (rand.nextBoolean()) {
			chambre1.getConteneur("Coffre").setCleRequise(cleCoffre1);
			chambre2.getConteneur("Coffre").setCleRequise(cleCoffre2);
		} else {
			chambre1.getConteneur("Coffre").setCleRequise(cleCoffre2);
			chambre2.getConteneur("Coffre").setCleRequise(cleCoffre1);
		}

		List<Object> cachettesPossibles = new ArrayList<>(zonesSansCave);
		cachettesPossibles.add(cachetteGagnante);
		placerCleCoffre(cleCoffre1, cachettesPossibles, rand);
		placerCleCoffre(cleCoffre2, cachettesPossibles, rand);

		// ---------------------------------------------------------
		// 4. LES 3 FRAGMENTS D'ÂME
		// ---------------------------------------------------------
		List<TypeFragment> types = randomiseur.choisir3FragmentsParmi5();
		cave.getConteneur("CorpsBaron")
				.ajouteObjet(new ObjetMaudit(types.get(0).name().toLowerCase(), "Fragment", types.get(0)));

		// On utilise bien une List<Object> pour mélanger Coffres et Pièces
		List<Object> cachettesFragments = new ArrayList<>();
		cachettesFragments.add(chambre1.getConteneur("Coffre"));
		cachettesFragments.add(chambre2.getConteneur("Coffre"));
		
		cachettesFragments.add(armoire); 
		
		java.util.Collections.shuffle(cachettesFragments);

		// Création des objets
		ObjetMaudit fragment1 = new ObjetMaudit(types.get(1).name().toLowerCase(), "Fragment", types.get(1));
		ObjetMaudit fragment2 = new ObjetMaudit(types.get(2).name().toLowerCase(), "Fragment", types.get(2));

		// Placement du premier fragment
		Object cachette1 = cachettesFragments.get(0);
		if (cachette1 instanceof jeu.environnement.Zone) {
			((jeu.environnement.Zone) cachette1).ajouteObjet(fragment1);
		} else if (cachette1 instanceof jeu.environnement.Conteneur) {
			((jeu.environnement.Conteneur) cachette1).ajouteObjet(fragment1);
		}

		// Placement du deuxième fragment
		Object cachette2 = cachettesFragments.get(1);
		if (cachette2 instanceof jeu.environnement.Zone) {
			((jeu.environnement.Zone) cachette2).ajouteObjet(fragment2);
		} else if (cachette2 instanceof jeu.environnement.Conteneur) {
			((jeu.environnement.Conteneur) cachette2).ajouteObjet(fragment2);
		}
		System.out.println("=== FIN DU PLACEMENT ===");
		System.out.println("\n=== RÉCAPITULATIF DES EMPLACEMENTS (TRICHE) ===");
		for (Zone z : toutesLesZones) {
			String objetsSol = z.listerObjets();
			if (!objetsSol.equals("Il n'y a aucun objet visible ici.")) {
				System.out.println("[SOL] " + z.getNom() + " -> " + objetsSol);
			}

			for (jeu.environnement.Conteneur c : z.getConteneurs()) {
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

	// (Garde tes petites méthodes utilitaires placerCleCoffre et
	// afficherTricheConsole ici)
	// Petite méthode utilitaire pour placer les clés de coffre (car elles peuvent
	// être dans une Zone ou un Conteneur)
	private void placerCleCoffre(Cle cle, List<Object> cachettes, Random rand) {
		boolean placee = false;

		// On boucle jusqu'à trouver une cachette valide
		while (!placee) {
			Object cachette = cachettes.get(rand.nextInt(cachettes.size()));

			if (cachette instanceof Zone) {
				Zone z = (Zone) cachette;

				// LA SÉCURITÉ EST ICI : On ne pose la clé que s'il y a moins de 2 objets
				if (z.getNombreObjetsSurSol() < 2) {
					z.ajouteObjet(cle);
					placee = true; // C'est bon, on sort de la boucle
				}
			} else if (cachette instanceof Conteneur) {
				// Si c'est un conteneur (le meubleGagnant), on peut toujours y mettre la clé
				((Conteneur) cachette).ajouteObjet(cle);
				placee = true; // C'est bon, on sort de la boucle
			}
		}
	}

	public void setGUI(GUI g) {
		gui = g;
		afficherMessageDeBienvenue();
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
		afficherCommandesPossibles(); // <--- AJOUTE CETTE LIGNE ICI
		gui.afficher();
		gui.afficheImage(zoneCourante.nomImage());
	}

	private void afficherCommandesPossibles() {
		// Règle du cahier des charges : dans le noir, on ne voit presque rien
		if (!eclairageActif && !zoneCourante.getNom().equalsIgnoreCase("salon")) {
			gui.afficher(
					"Il fait nuit noire... Les commandes sont invisibles. Vous pouvez seulement tâtonner pour vous déplacer (N, S, E, O).");
			return;
		}

		List<String> cmds = new ArrayList<>();

		// Commandes de base toujours visibles (si la lumière est allumée)
		cmds.add("Déplacement (N, S, E, O)");
		cmds.add("Retour (R)");
		cmds.add("Inventaire (I)");
		cmds.add("Prendre (P) / Déposer (D)");
		cmds.add("Ouvrir (OU)");
		// rajouter commandes manquante

		// Commandes contextuelles liées à la zone
		String nomZone = zoneCourante.getNom().toLowerCase();
		if (nomZone.equals("salon")) {
			if (!eclairageActif) {
				cmds.add("Commande 1 (CMD1)");
				cmds.add("Commande 2 (CMD2)");
			} else if (eclairageActif) {
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
		// On ajoute "Ouvrir" de manière générale si la lumière est allumée
		cmds.add("Ouvrir (OU)");

		gui.afficher("Commandes disponibles : " + String.join(", ", cmds));
	}

	public void traiterCommande(String texteSaisi) {
		verifieGUI();
		gui.afficher("> " + texteSaisi + "\n");

		// CORRECTION : Séparer la commande de l'argument (ex: "PRENDRE cle" ->
		// mots[0]="PRENDRE", mots[1]="cle")
		String[] mots = texteSaisi.trim().split(" ", 2);
		String commande = mots[0].toUpperCase();
		String argument = (mots.length > 1) ? mots[1] : "";

		switch (commande) {
		case "?", "AIDE" -> afficherAide();
		case "R", "RETOUR" -> retour();
		case "N", "NORD" -> allerEn(Direction.NORD);
		case "S", "SUD" -> allerEn(Direction.SUD);
		case "E", "EST" -> allerEn(Direction.EST);
		case "O", "OUEST" -> allerEn(Direction.OUEST);
		case "B", "BRULER" -> bruler();
		case "STRESS", "STRESSTEST" -> {
			lancementStress(argument);
		}
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
		case "TP", "TELEPORTER" -> {
			
			teleporter(argument);
		}
		case "SAUV", "SAUVEGARDER" -> sauvegarderLaPartie();
		case "T", "TEST" -> testPartieGagnante();
		case "AB", "ABANDON" -> abandonSansSauv();
		case "Q", "QUITTER" -> terminer();
		
		default -> gui.afficher("Commande inconnue");
		}
	}
	
	private void lancementStress(String argument) {
		int nbParties = 100; 

		if (argument != null && !argument.trim().isEmpty()) {
			try {
				nbParties = Integer.parseInt(argument.trim());
				
				if (nbParties < 1 || nbParties > 100) {
					gui.afficher("Erreur : Le nombre de parties doit être compris entre 1 et 100.");
					return; 
				}
			} catch (NumberFormatException e) {
				gui.afficher("Erreur : '" + argument + "' n'est pas un nombre valide.");
				return;
			}
		}

		gui.afficher("Lancement du Stress Test (" + nbParties + " parties)... Regardez la console !");
		TestAutomatique testeur = new TestAutomatique();
		testeur.executerStressTest(nbParties, this.gui);
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

	private void sauvegarderLaPartie() {
		gui.afficher("Sauvegarde de la partie en cours...");
		jeu.sauvegarde.EtatPartie etatActuel = new jeu.sauvegarde.EtatPartie().capturer(this);
		jeu.sauvegarde.GestionnaireSauvegardeJSON gestionnaire = new jeu.sauvegarde.GestionnaireSauvegardeJSON();

		boolean succes = gestionnaire.sauvegarderPartie(etatActuel, joueur.getPseudo());
		if (succes) {
			gui.afficher("Partie sauvegardée avec succès !");
		} else {
			gui.afficher("Erreur lors de la sauvegarde.");
		}
	}

	private void ouvrir(String argument) {
		// 1. LE PASSAGE SECRET DE LA BIBLIOTHÈQUE
		if (zoneCourante.getNom().equalsIgnoreCase("bibliothèque") && argument.equalsIgnoreCase("livre")) {
			zoneCourante.revelerSortieCachee("Livre", Direction.SUD);
			gui.afficher(
					"Vous tirez un livre étrange... L'étagère pivote dans un grincement sourd, révélant un escalier qui descend vers le SUD ! (La Cave est maintenant accessible)");
			afficherCommandesPossibles();
			rafraichirImage();
			return;
		}

		// 2. LES VRAIS MEUBLES (Coffre, Bureau, Armoire, Tiroir...)
		Conteneur conteneur = zoneCourante.getConteneur(argument);

		if (conteneur == null) {
			gui.afficher("Il n'y a pas de '" + argument + "' à ouvrir ici.");
			return;
		}

		// --- NOUVEAU : CAS SPÉCIFIQUE DU FANTÔME / CORPS DU BARON ---
		if (conteneur.getNom().equalsIgnoreCase("CorpsBaron")) {
			if (conteneur.estVerrouille()) {
				gui.afficher("Une aura glaciale vous repousse. Le fantôme du Baron apparaît !");
				gui.afficher("Il ne vous laissera pas approcher de son corps si facilement...");

				// C'est ici que tu dois appeler ton énigme.
				// Exemple (à adapter selon le code exact de ta classe CorpsBaron) :
				// jeu.environnement.CorpsBaron baron = (jeu.environnement.CorpsBaron)
				// conteneur;
				// gui.afficher("Énigme : " + baron.getEnigme().getQuestion());
				// TODO
				gui.afficher("(Tapez votre réponse avec une commande spécifique, ex: REPONDRE <votre_texte>)");
				revelerContenu(conteneur);
				rafraichirImage();
				return; // On arrête l'exécution ici pour ne pas chercher de clé !
			}
		}

		// Si le meuble est verrouillé
		if (conteneur.estVerrouille()) {
			boolean cleTrouvee = false;

			for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
				if (obj instanceof Cle) {
					Cle cle = (Cle) obj;

					if (conteneur.deverrouillerAvecCle(cle, joueur)) {
						cleTrouvee = true;
						gui.afficher("CLIC ! Vous avez déverrouillé et ouvert : " + argument + " avec la "
								+ cle.getNom() + " !");

						// NOUVEAU : La clé est consommée (elle reste dans la serrure)
						joueur.getInventaire().retire(cle.getNom());

						// --- LA MAGIE OPÈRE ICI ---
						revelerContenu(conteneur);
						rafraichirImage();
						break;
					}
				}
			}

			if (!cleTrouvee) {
				if (conteneur.getNom().equalsIgnoreCase("Coffre")) {
					// 1. On déclenche le piège une seule fois
					joueur.perdreVie(); // (ou conteneur.declenchePiege(joueur); selon comment tu l'as nommé)

					gui.afficher(
							"Vous tentez de forcer la serrure... Un mécanisme se déclenche ! Piège ! Il vous reste "
									+ joueur.getVies() + " vies.");

					// 2. On vérifie si c'était la dernière vie avant d'appeler finDePartie !
					if (joueur.getVies() <= 0) {
						finDePartie();
					}
				} else {
					gui.afficher("C'est verrouillé. Il vous faut la bonne clé pour ouvrir " + argument + ".");
				}
			}
		} else {
			// Si le meuble n'est pas verrouillé de base (ou a déjà été déverrouillé)
			gui.afficher("Vous ouvrez " + argument + ".");

			// --- LA MAGIE OPÈRE ICI AUSSI ---
			revelerContenu(conteneur);
		}
	}

	private void revelerContenu(Conteneur conteneur) {
		// Si le meuble est déjà vide
		if (conteneur.getContenu().isEmpty()) {
			gui.afficher("Vous regardez à l'intérieur... c'est vide.");
			return;
		}

		List<String> objetsPris = new ArrayList<>();
		Iterator<ObjetJeu> it = conteneur.getContenu().iterator();

		// On parcourt les objets du conteneur
		while (it.hasNext()) {
			ObjetJeu obj = it.next();

			// On vérifie s'il reste de la place dans l'inventaire du joueur
			if (!joueur.getInventaire().estPlein()) {
				joueur.getInventaire().ajoute(obj); // Va directement dans le sac
				objetsPris.add(obj.getNom());
				it.remove(); // Disparaît du conteneur
			} else {
				gui.afficher("Votre sac est plein ! Certains objets sont restés dans : " + conteneur.getNom() + ".");
				break; // On arrête le ramassage automatique
			}
		}

		if (!objetsPris.isEmpty()) {
			gui.afficher("Vous trouvez et prenez automatiquement : " + String.join(", ", objetsPris) + ".");

			// On met à jour l'image ici, car le coffre vient de se vider (et passera donc
			// en état "ouvert")
			rafraichirImage();
		}
	}

	private void prendreObjet(String nomObjet) {
		// CORRECTION : On vérifie si le sac est plein (pas s'il est vide)
		if (joueur.getInventaire().estPlein()) {
			gui.afficher("Votre sac à dos est plein ! Capacité maximale de 5 objets atteinte.");
			return;
		}

		ObjetJeu objet = zoneCourante.retireObjet(nomObjet);
		if (objet != null) {
			joueur.getInventaire().ajoute(objet);
			gui.afficher("Vous avez pris : " + nomObjet + ".");
			gui.afficher("Le fantome du baron est present et peut s'amuser a deplacer les autres objet présent dans la piece");
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

	    // On vérifie si le joueur a le médaillon (avec ou sans accent)
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

	    // --- LA MAGIE S'ACTIVE ---
	    this.miroirActive = true; 
	    gui.afficher("Vous présentez le Médaillon. La surface du miroir se met à onduler comme de l'eau !");
	    
	    // On affiche les destinations (comme dans le message précédent)
	    List<Zone> visitees = getZonesVisitees();
	    List<String> noms = new ArrayList<>();
	    for(Zone z : visitees) noms.add(z.getNom());
	    
	    gui.afficher("Destinations possibles : " + String.join(", ", noms));
	    gui.afficher("(Tapez TELEPORTER <nom>)");
	    
	    // On met à jour les commandes pour afficher le TP
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


	    // On formate la saisie (si le joueur tape "grand couloir", on transforme en "grand_couloir")
	    String nomFormate = destinationVoulue.replace(" ", "_");

	    // On cherche si la zone demandée fait partie des zones visitées
	    jeu.environnement.Zone destinationFinale = null;
	    for (jeu.environnement.Zone z : getZonesVisitees()) {
	        if (z.getNom().equalsIgnoreCase(nomFormate) || z.getNom().equalsIgnoreCase(destinationVoulue)) {
	            destinationFinale = z;
	            break;
	        }
	    }

	    if (destinationFinale == null) {
	        gui.afficher("Le miroir est flou... Vous ne pouvez vous téléporter que vers une pièce déjà visitée, et le nom doit être exact.");
	        return;
	    }

	    if (destinationFinale == zoneCourante) {
	        gui.afficher("Vous y êtes déjà !");
	        return;
	    }

	    // --- LE DÉPLACEMENT MAGIQUE ---
	    gui.afficher("Vous touchez la surface du miroir... Le monde tourne autour de vous !");
	    
	    historiqueZones.push(zoneCourante); // On sauvegarde la salle de bain dans l'historique
	    zoneCourante = destinationFinale;   // On change de pièce !
	    
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

		if (this.etatJeu == EtatJeu.DEFAITE || this.etatJeu == EtatJeu.VICTOIRE) {
			gui.afficher("--- GAME OVER ---");
		}
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
		// Attention: assure-toi que Commande.toutesLesDescriptions() existe bien dans
		// ton code !
		gui.afficher(Commande.toutesLesDescriptions().toString());
		gui.afficher();
	}

	private void allerEn(Direction direction) {
		verifieGUI();

		if (zoneCourante.getNom().equalsIgnoreCase("chambre1") && direction == Direction.SUD) {

			Zone pieceBureau = manoir.obtientZone("bureau");

			// On récupère le meuble/verrou qui bloque l'accès
			// (Remplace "Bureau" par "Armoire" si c'est vraiment l'armoire qui bloque la
			// porte de la pièce)
			Conteneur porteVerrouillee = pieceBureau.getConteneur("Bureau");

			// Si le verrou a été activé lors de la génération de la partie
			if (porteVerrouillee != null && porteVerrouillee.estVerrouille()) {

				boolean cleTrouvee = false;

				// On fouille dans le sac du joueur
				for (ObjetJeu obj : joueur.getInventaire().getObjets()) {
					if (obj instanceof Cle) {
						Cle cle = (Cle) obj;

						// On teste si c'est la bonne clé avec notre méthode de Conteneur
						if (porteVerrouillee.deverrouillerAvecCle(cle, joueur)) {
							cleTrouvee = true;
							joueur.getInventaire().retire(cle.getNom()); // On consomme la clé
							gui.afficher("CLIC ! Vous déverrouillez la porte du bureau avec la " + cle.getNom()
									+ " et vous entrez.");
							break;
						}
					}
				}

				// Si le joueur n'a pas la bonne clé dans son sac
				if (!cleTrouvee) {
					gui.afficher(
							"La porte menant au SUD (le bureau) est fermée à clé. Il vous faut la bonne clé pour entrer.");
					return; // ON BLOQUE LE DÉPLACEMENT EN ARRÊTANT LA MÉTHODE ICI !
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
		StringBuilder sb = new StringBuilder();
		String nomZone = zoneCourante.getNom().toLowerCase();

		// 1. GESTION DU DOSSIER (ex: "salle_de_bain/", "chambre1/")
		String nomDossier = nomZone.replace(" ", "_");
		sb.append(nomDossier).append("/");

		// 2. GESTION DES PRÉFIXES SPÉCIFIQUES SELON LA PIÈCE
		switch (nomZone) {
		case "bibliothèque":
			sb.append("bibliothèque");
			// Le passage vers la cave est révélé vers le SUD, on vérifie s'il existe
			boolean passageOuvert = (zoneCourante.obtientSortie(Direction.SUD) != null);
			sb.append(passageOuvert ? "_passageOuvert" : "_passageFermé");
			sb.append(eclairageActif ? "_ON" : "_OFF");
			break;

		case "bureau":
			sb.append("bureau");
			Conteneur armoire = zoneCourante.getConteneur("Armoire");

			boolean placardOuvert = (armoire != null && armoire.estOuvert());

			sb.append(placardOuvert ? "_placard_ouvert" : "_placard_fermé");
			sb.append(eclairageActif ? "_ON" : "_OFF");
			break;

		case "chambre1":
		case "chambre2":
			sb.append(nomZone); // "chambre1" ou "chambre2"
			sb.append(eclairageActif ? "_ON" : "_OFF");
			Conteneur coffre = zoneCourante.getConteneur("Coffre");
			boolean coffreOuvert = (coffre != null && coffre.getContenu().isEmpty());
			if (eclairageActif) {
				sb.append(coffreOuvert ? "_coffre_ON" : "_coffre_OFF");
			}
			break;

		case "salon":
			sb.append("salon");
			sb.append(eclairageActif ? "_ON" : "_OFF");
			sb.append(chemineActif ? "_chemine_ON" : "_chemine_OFF");

			break;

		case "cave":
			sb.append("cave");
			sb.append(eclairageActif ? "_ON" : "_OFF");
			// Règle spécifique : on identifie l'objet lié au Baron
			Conteneur corps = zoneCourante.getConteneur("CorpsBaron");
			if (corps != null && !corps.getContenu().isEmpty()) {
				String nomObjBaron = corps.getContenu().get(0).getNom().toLowerCase();
				if (nomObjBaron.contains("journal"))
					sb.append("_journal");
				else if (nomObjBaron.contains("medaillon") || nomObjBaron.contains("médaillon"))
					sb.append("_medaillon");
				else if (nomObjBaron.contains("montre"))
					sb.append("_montre");
				else if (nomObjBaron.contains("pipe"))
					sb.append("_pipe");
				else if (nomObjBaron.contains("plume"))
					sb.append("_plume");
			}
			// On retourne la chaîne ici car la cave n'affiche pas les objets au sol !
			return sb.toString();

		default:
			// Pour cuisine, salle_de_bain, grand_couloir, petit_couloir
			sb.append(nomZone.replace(" ", "_"));
			sb.append(eclairageActif ? "_ON" : "_OFF");
			break;
		}

		if (eclairageActif) {
			// 3. AJOUT DES OBJETS AU SOL (Pour toutes les pièces sauf la cave)
			List<ObjetJeu> objetsAuSol = zoneCourante.getObjetsPresents();
			if (objetsAuSol != null && !objetsAuSol.isEmpty()) {
				List<String> motsClefs = new ArrayList<>();

				for (ObjetJeu obj : objetsAuSol) {
					String nom = obj.getNom().toLowerCase();
					// On convertit le nom de l'objet brut en mot-clé pour le nom de fichier
					if (nom.contains("bois"))
						motsClefs.add("bois");
					else if (nom.contains("cle") || nom.contains("clé"))
						motsClefs.add("cle");
					else if (nom.contains("echelle") || nom.contains("échelle"))
						motsClefs.add("echelle");
					else if (nom.contains("medaillon") || nom.contains("médaillon"))
						motsClefs.add("medaillon");
					// Ajoute d'autres conditions ici si tu as de nouveaux objets posables au sol
				}

				// TRI ALPHABÉTIQUE OBLIGATOIRE (ex: "_cle_echelle" et pas "_echelle_cle")
				java.util.Collections.sort(motsClefs);

				for (String mot : motsClefs) {
					sb.append("_").append(mot);
				}
			}
		}

		return sb.toString();
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
}