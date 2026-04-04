package jeu.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import jeu.EtatJeu;
import jeu.GUI;
import jeu.Jeu;

public class TestAutomatique {

	private List<String> getTesCommandes() {
		List<String> commandes = new ArrayList<>();

		// PHASE 1 : LE FEU (Libère l'inventaire)
		commandes.addAll(Arrays.asList("CMD1", "N", "O", "OUVRIR tiroir", "E", "E", "PRENDRE bois", "O", "S", "AF"));

		// PHASE 2 : GRAND BALAYAGE OPTIMISÉ (1 seule fouille par pièce)
		ajouterFouille(commandes); // SALON
		commandes.add("N"); // Entre dans le Grand Couloir
		ajouterFouille(commandes);
		commandes.add("O"); // Entre dans la Cuisine
		ajouterFouille(commandes);
		commandes.add("E");
		commandes.add("N"); // Entre dans le Petit Couloir
		ajouterFouille(commandes);
		commandes.add("N"); // Entre dans la Salle de Bain
		ajouterFouille(commandes);
		commandes.add("S");
		commandes.add("E"); // Entre dans la Chambre 2
		ajouterFouille(commandes);
		commandes.add("O");
		commandes.add("O"); // Entre dans la Chambre 1
		ajouterFouille(commandes);
		commandes.add("E");
		commandes.add("S");
		commandes.add("E"); // Entre dans la Bibliothèque
		ajouterFouille(commandes);
		commandes.add("O");
		commandes.add("N");
		commandes.add("O");
		commandes.add("S"); // Entre dans le Bureau
		ajouterFouille(commandes);

		// PHASE 3 : L'OUVERTURE DES MEUBLES (Avec les clés en poche)
		String[] parcoursOuverture = { 
			"OUVRIR armoire", 
			"p journal_intime", "p montre_gousset", "p pipe_bois", "p medaillon", "p plume", // ⚠️ Voir ma petite remarque en bas !
			"N", "OUVRIR coffre", 
			"E", "E", "OUVRIR coffre", 
			"O", "S" 
		};
		commandes.addAll(Arrays.asList(parcoursOuverture));

		// PHASE 4 : LA CAVE ET LA VICTOIRE
		commandes.addAll(Arrays.asList("E", "OUVRIR livre", "S", "OUVRIR corpsBaron", "N", "O", "S", "B")); // BRÛLER TOUT !

		return commandes;
	}

	private void ajouterFouille(List<String> liste) {
		liste.add("PRENDRE cle_coffre_1");
		liste.add("PRENDRE cle_coffre_2");
		liste.add("PRENDRE cle_bureau");
		liste.add("PRENDRE cle_armoire");
		liste.add("PRENDRE echelle");
	}
	
	public void executerSequenceVictoire(Jeu jeu) {
		System.out.println("Lancement du Speedrun Automatique...");
		
		// On récupère tes commandes
		List<String> commandes = getTesCommandes(); 

		new Thread(() -> {
			for (String cmd : commandes) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(() -> {
					jeu.traiterCommande(cmd);
				});
			}
		}).start();
	}

	// ---------------------------------------------------------
	// 3. LE NOUVEAU STRESS TEST (Lance N parties instantanément)
	// ---------------------------------------------------------
	public void executerStressTest(int nbTests, GUI guiActuelle) {
		System.out.println("=== DÉBUT DU STRESS TEST (" + nbTests + " parties) ===");
		int victoires = 0;
		int defaites = 0;

		List<String> commandes = getTesCommandes();

		for (int i = 0; i < nbTests; i++) {
			Jeu partieTest = new Jeu();
			partieTest.setGUI(guiActuelle); // On utilise l'interface actuelle

			// On exécute tout d'un coup, sans le Thread.sleep !
			for (String cmd : commandes) {
				partieTest.traiterCommande(cmd);
			}

			// On vérifie si on a gagné
			if (partieTest.getEtatJeu() == EtatJeu.VICTOIRE) {
				victoires++;
			} else {
				defaites++;
				System.out.println("BLOCAGE à la partie n°" + (i + 1));
			}
		}

		System.out.println("\n=== RÉSULTATS DU STRESS TEST ===");
		System.out.println("Parties simulées : " + nbTests);
		System.out.println("Victoires        : " + victoires);
		System.out.println("Échecs           : " + defaites);
	}
}
