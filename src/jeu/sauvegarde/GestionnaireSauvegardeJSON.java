package jeu.sauvegarde;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestionnaireSauvegardeJSON {
    
    private final String FORMAT_FICHIER = "joueur_%s.json";

    // --- SAUVEGARDE MANUELLE EN JSON ---
    public boolean sauvegarderPartie(EtatPartie etat, String pseudo) {
        String nomFichier = String.format(FORMAT_FICHIER, pseudo);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier))) {
            // On construit le texte JSON à la main
            writer.write("{");
            writer.write("\n  \"pseudo\": \"" + etat.getPseudo() + "\",");
            writer.write("\n  \"vies\": " + etat.getVies() + ",");
            writer.write("\n  \"zoneActuelle\": \"" + etat.getZoneActuelle() + "\",");
            writer.write("\n  \"fragmentsDetruits\": " + etat.getFragmentsDetruits() + ",");
            writer.write("\n  \"eclairageActif\": " + etat.isEclairageActif() + ",");
            
            // Pour la liste de l'inventaire
            writer.write("\n  \"inventaire\": [");
            List<String> inv = etat.getInventaire();
            for (int i = 0; i < inv.size(); i++) {
                writer.write("\"" + inv.get(i) + "\"");
                if (i < inv.size() - 1) writer.write(", ");
            }
            writer.write("]");
            
            writer.write("\n}");
            return true;
        } catch (IOException e) {
            System.err.println("Erreur de sauvegarde : " + e.getMessage());
            return false;
        }
    }

    // --- CHARGEMENT MANUEL DEPUIS LE JSON ---
    public EtatPartie chargerPartie(String pseudo) {
        String nomFichier = String.format(FORMAT_FICHIER, pseudo);
        File fichier = new File(nomFichier);
        
        if (!fichier.exists()) {
            return null;
        }

        EtatPartie etat = new EtatPartie();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                // Analyse très basique ligne par ligne
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

    // --- METHODES UTILITAIRES POUR LIRE LE TEXTE ---
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
        if (fin == -1) fin = ligne.length(); // Si c'est la dernière ligne
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
                if (!clean.isEmpty()) liste.add(clean);
            }
        }
        return liste;
    }
}