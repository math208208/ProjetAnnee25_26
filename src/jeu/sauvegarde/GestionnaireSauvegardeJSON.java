package jeu.sauvegarde;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireSauvegardeJSON {

	private final String FORMAT_FICHIER = "joueur_%s.json";

	public boolean sauvegarderPartie(EtatPartie etat, String pseudo) {
		String nomFichier = String.format(FORMAT_FICHIER, pseudo);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
			writer.write("{");
			writer.write("\n  \"pseudo\": \"" + etat.getPseudo() + "\",");
			writer.write("\n  \"vies\": " + etat.getVies() + ",");
			writer.write("\n  \"zoneActuelle\": \"" + etat.getZoneActuelle() + "\",");
			writer.write("\n  \"fragmentsDetruits\": " + etat.getFragmentsDetruits() + ",");
			writer.write("\n  \"eclairageActif\": " + etat.isEclairageActif() + ",");
			writer.write("\n  \"chemineActif\": " + etat.isChemineActif() + ",");

			// Écrire les Positions (Converties en tableau pour ton lecteur)
			writer.write("\n  \"positionsObjets\": [");
			if (etat.getPositionsObjets() != null) {
				int count = 0;
				int size = etat.getPositionsObjets().size();
				for (Map.Entry<String, String> entry : etat.getPositionsObjets().entrySet()) {
					writer.write("\"" + entry.getKey() + "=" + entry.getValue() + "\"");
					if (count < size - 1)
						writer.write(", ");
					count++;
				}
			}
			writer.write("],");

			// Écrire les États des Meubles
			writer.write("\n  \"etatsConteneurs\": [");
			List<String> etats = etat.getEtatsConteneurs();
			if (etats != null) {
				for (int i = 0; i < etats.size(); i++) {
					writer.write("\"" + etats.get(i) + "\"");
					if (i < etats.size() - 1)
						writer.write(", ");
				}
			}
			writer.write("],");

			// Écrire l'Inventaire
			writer.write("\n  \"inventaire\": [");
			List<String> inv = etat.getInventaire();
			if (inv != null) {
				for (int i = 0; i < inv.size(); i++) {
					writer.write("\"" + inv.get(i) + "\"");
					if (i < inv.size() - 1)
						writer.write(", ");
				}
			}
			writer.write("]");

			writer.write("\n}");
			return true;
		} catch (IOException e) {
			System.err.println("Erreur de sauvegarde : " + e.getMessage());
			return false;
		}
	}

	public EtatPartie chargerPartie(String pseudo) {
		String nomFichier = String.format(FORMAT_FICHIER, pseudo);
		File fichier = new File(nomFichier);

		if (!fichier.exists())
			return null;

		EtatPartie etat = new EtatPartie();

		try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
			String ligne;
			while ((ligne = reader.readLine()) != null) {
				if (ligne.contains("\"pseudo\"")) {
					etat.setPseudo(extraireValeurTexte(ligne));
				} else if (ligne.contains("\"vies\"")) {
					etat.setVies(Integer.parseInt(extraireValeurNombre(ligne)));
				} else if (ligne.contains("\"zoneActuelle\"")) {
					etat.setZoneActuelle(extraireValeurTexte(ligne));
				} else if (ligne.contains("\"fragmentsDetruits\"")) {
					etat.setFragmentsDetruits(Integer.parseInt(extraireValeurNombre(ligne)));
				} else if (ligne.contains("\"eclairageActif\"")) {
					etat.setEclairageActif(ligne.contains("true"));
				} else if (ligne.contains("\"chemineActif\"")) {
					etat.setChemineActif(ligne.contains("true"));
					// LECTURE DES POSITIONS
				} else if (ligne.contains("\"positionsObjets\"")) {
					List<String> listPos = extraireTableau(ligne);
					Map<String, String> mapPos = new HashMap<>();
					for (String s : listPos) {
						String[] parts = s.split("=");
						if (parts.length == 2)
							mapPos.put(parts[0], parts[1]);
					}
					etat.setPositionsObjets(mapPos);

					// LECTURE DES MEUBLES
				} else if (ligne.contains("\"etatsConteneurs\"")) {
					etat.setEtatsConteneurs(extraireTableau(ligne));

				} else if (ligne.contains("\"inventaire\"")) {
					etat.setInventaire(extraireTableau(ligne));
				}
			}
			return etat;
		} catch (Exception e) {
			System.err.println("Erreur de lecture du JSON : " + e.getMessage());
			return null;
		}
	}

	public boolean verifierExistenceFichier(String pseudo) {
		return new File(String.format(FORMAT_FICHIER, pseudo)).exists();
	}

	private String extraireValeurTexte(String ligne) {
		int debut = ligne.indexOf(":") + 1;
		int fin = ligne.lastIndexOf("\"");
		int premierGuillemet = ligne.indexOf("\"", debut);
		if (premierGuillemet != -1 && fin != -1 && premierGuillemet < fin) {
			return ligne.substring(premierGuillemet + 1, fin);
		}
		return "";
	}

	private String extraireValeurNombre(String ligne) {
		int debut = ligne.indexOf(":") + 1;
		int fin = ligne.indexOf(",");
		if (fin == -1)
			fin = ligne.length();
		return ligne.substring(debut, fin).trim();
	}

	private List<String> extraireTableau(String ligne) {
		List<String> liste = new ArrayList<>();
		int debut = ligne.indexOf("[") + 1;
		int fin = ligne.indexOf("]");
		if (debut > 0 && fin > debut) {
			String contenu = ligne.substring(debut, fin);
			String[] elements = contenu.split(",");
			for (String el : elements) {
				String clean = el.replace("\"", "").trim();
				if (!clean.isEmpty())
					liste.add(clean);
			}
		}
		return liste;
	}
}