package jeu.environnement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeu.Direction;
import jeu.objets.Cle;

public class Manoir {
	private Map<String, Zone> zones;
	private Zone zoneDepart;

	public Manoir() {
		zones = new HashMap<>();
		creerZones();
		relierZones();
	}

	public void creerZones() {
		zones.put("salon", new Zone("salon", "le salon", "salon/salon_OFF_chemine_OFF", false));
		zones.put("cuisine", new Zone("cuisine", "la cuisine", "cuisine/cuisine_OFF", false));
		zones.put("bibliothèque", new Zone("bibliothèque", "la bibliothèque", "bibliothèque/bibliothèque_passageFermé_OFF", false));
		zones.put("bureau", new Zone("bureau", "le bureau", "bureau/bureau_placard_fermé_OFF", false));
		zones.put("grand_couloir", new Zone("grand_couloir", "le grand couloir", "grand_couloir/grand_couloir_OFF", false));
		zones.put("petit_couloir", new Zone("petit_couloir", "le petit couloir", "petit_couloir/petit_couloir_OFF", false));
		zones.put("salle_de_bain", new Zone("salle_de_bain", "la salle de bain", "salle_de_bain/salle_de_bain_OFF", false));
		zones.put("chambre1", new Zone("chambre1", "la chambre 1", "chambre1/chambre1_OFF", false));
		zones.put("chambre2", new Zone("chambre2", "la chambre 2", "chambre2/chambre2_OFF", false));
		zones.put("cave", new Zone("cave", "la cave", "cave/cave_OFF", false));

		zoneDepart = zones.get("salon");
		Cle serrureCoffre = new Cle("cle_coffre", "", "");
		Cle serrureBureau = new Cle("cle_bureau", "", "");
		Cle serrureArmoire = new Cle("cle_armoire", "", "");

		// Dans le Salon : La cheminée
		zones.get("salon").ajouteConteneur(new Cheminee("Cheminee"));

		// Dans la Cuisine : Le tiroir
		zones.get("cuisine").ajouteConteneur(new Tiroir("Tiroir"));

		// Dans le Bureau : Le bureau et l'armoire
		zones.get("bureau").ajouteConteneur(new Bureau("Bureau", true, serrureBureau));
		zones.get("bureau").ajouteConteneur(new Armoire("Armoire", true, serrureArmoire));

		// Dans les Chambres : Les coffres piégés !
		zones.get("chambre1").ajouteConteneur(new Coffre("Coffre", serrureCoffre));
		zones.get("chambre2").ajouteConteneur(new Coffre("Coffre", serrureCoffre));

		// Dans la Cave : Le fantôme du Baron et son énigme
		jeu.enigmes.BanqueEnigmes banque = new jeu.enigmes.BanqueEnigmes();
		zones.get("cave").ajouteConteneur(new CorpsBaron("CorpsBaron", banque.obtientEnigmeAleatoire()));
	}

	public List<Zone> getToutesLesZones() {
		return new ArrayList<>(zones.values());
	}

	public void relierZones() {
		Zone salon = zones.get("salon");
		Zone grandCouloir = zones.get("grand_couloir");
		Zone petitCouloir = zones.get("petit_couloir");
		Zone cuisine = zones.get("cuisine");
		Zone bibliotheque = zones.get("bibliothèque");
		Zone cave = zones.get("cave");
		Zone chambre1 = zones.get("chambre1");
		Zone chambre2 = zones.get("chambre2");
		Zone bureau = zones.get("bureau");
		Zone salleDeBain = zones.get("salle_de_bain");

		// Connexions Salon <-> Grand Couloir
		salon.ajouteSortie(Direction.NORD, grandCouloir);
		grandCouloir.ajouteSortie(Direction.SUD, salon);

		// Connexions Grand Couloir <-> Cuisine / Bibliothèque
		grandCouloir.ajouteSortie(Direction.OUEST, cuisine);
		cuisine.ajouteSortie(Direction.EST, grandCouloir);
		grandCouloir.ajouteSortie(Direction.EST, bibliotheque);
		bibliotheque.ajouteSortie(Direction.OUEST, grandCouloir);

		// Connexions Grand Couloir <-> Petit Couloir
		grandCouloir.ajouteSortie(Direction.NORD, petitCouloir);
		petitCouloir.ajouteSortie(Direction.SUD, grandCouloir);

		// Sortie cachée Bibliothèque -> Cave
		bibliotheque.ajouteSortieCachee("Livre", cave);
		cave.ajouteSortie(Direction.NORD, bibliotheque); 
		
		// Connexions Petit Couloir <-> Chambres / Salle de bain
		petitCouloir.ajouteSortie(Direction.OUEST, chambre1);
		chambre1.ajouteSortie(Direction.EST, petitCouloir);
		petitCouloir.ajouteSortie(Direction.EST, chambre2);
		chambre2.ajouteSortie(Direction.OUEST, petitCouloir);
		petitCouloir.ajouteSortie(Direction.NORD, salleDeBain);
		salleDeBain.ajouteSortie(Direction.SUD, petitCouloir);

		// Connexions Chambre 1 <-> Bureau
		chambre1.ajouteSortie(Direction.SUD, bureau);
		bureau.ajouteSortie(Direction.NORD, chambre1);
	}

	public Zone obtientZone(String nomZone) {
		return zones.get(nomZone);
	}

	public Zone getZoneDepart() {
		return zoneDepart;
	}
}