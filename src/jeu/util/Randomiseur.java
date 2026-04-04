package jeu.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jeu.environnement.Zone;
import jeu.objets.ObjetJeu;
import jeu.objets.TypeFragment;

public class Randomiseur {
	
	private Random random = new Random();

	public List<TypeFragment> choisir3FragmentsParmi5() {
		List<TypeFragment> tousLesFragments = new ArrayList<>(Arrays.asList(TypeFragment.values()));
		
		Collections.shuffle(tousLesFragments, random);
		
		return tousLesFragments.subList(0, 3); 
	}
    
	/**
	 * Distribue une liste d'objets sur le sol des zones fournies.
	 * Règle stricte : Maximum 2 objets sur le sol par zone.
	 */
	public void distribuerObjetsSurSol(List<ObjetJeu> objets, List<Zone> zones) {
		if (objets == null || zones == null || zones.isEmpty()) {
			return;
		}

		for (ObjetJeu obj : objets) {
			boolean place = false;
			int securite = 0; // Pour éviter une boucle infinie s'il n'y a plus aucune place
			
			while (!place && securite < 100) {
				Zone zoneAleatoire = zones.get(random.nextInt(zones.size()));
				
				// On vérifie qu'il y a moins de 2 objets sur le sol
				if (zoneAleatoire.getNombreObjetsSurSol() < 2) {
					zoneAleatoire.ajouteObjet(obj);
					place = true;
				}
				securite++;
			}
			
			if (!place) {
				System.out.println("[Erreur Randomiseur] Plus de place pour placer : " + obj.getNom());
			}
		}
	}
}