package jeu;

import java.awt.Window;

public class FausseGUI implements GUI {
    private StringBuilder historique = new StringBuilder();

    public FausseGUI(Jeu jeu) {
        super(jeu);
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            window.dispose();
        }
    }

    @Override
    public void afficher(String s) {
        historique.append(s).append("\n");
    }

    @Override
    public void afficher() {
        historique.append("\n");
    }

    @Override
    public void afficheImage(String nomDeBase) {
    }

    public String getHistorique() {
        return historique.toString();
    }

    public void nettoyerHistorique() {
        historique.setLength(0);
    }
}
