package jeu;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;


public class GUI implements ActionListener {
    private final Jeu jeu;
    private final JFrame fenetre;
    private final JTextField entree;
    private final JTextArea texte;
    private final JLabel image;
    
    private static final String NomRepertoireImages = "jeu/images/";

    public GUI(Jeu jeu) {
    	this.jeu = jeu;

        fenetre = new JFrame("Jeu");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        texte = new JTextArea();
        texte.setEditable(false);
        texte.setLineWrap(true);
        texte.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(texte);
        scroll.setPreferredSize(new Dimension(800, 600));

        entree = new JTextField();
        entree.addActionListener(this);
        
        image = new JLabel();
        image.setHorizontalAlignment(SwingConstants.CENTER);

        var panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panel.add(image, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(entree, BorderLayout.SOUTH);

        fenetre.getContentPane().add(panel);
        fenetre.pack();
        fenetre.setLocationRelativeTo(null); 
        fenetre.setVisible(true);
        
        SwingUtilities.invokeLater(entree::requestFocusInWindow);
    }

    public void afficher(String s) {
    	SwingUtilities.invokeLater(() -> {
            texte.append(s + "\n");
            texte.setCaretPosition(texte.getDocument().getLength());
        });
    }
    
    public void afficher() {
        afficher("");
    }

    public void afficheImage(String nomDeBase) {
        SwingUtilities.invokeLater(() -> {
            URL imageURL = null;
            
            // On teste le JPG, puis le PNG
            String[] extensions = {".jpg", ".png"};
            for (String ext : extensions) {
                imageURL = getClass().getClassLoader().getResource(NomRepertoireImages + nomDeBase + ext);
                if (imageURL != null) break; // On a trouvé l'image, on sort de la boucle !
            }

            if (imageURL != null) {
                ImageIcon iconeOriginale = new ImageIcon(imageURL);
                int largeurVoulue = 500;
                int hauteurVoulue = 300;
                Image imageRedimensionnee = iconeOriginale.getImage().getScaledInstance(largeurVoulue, hauteurVoulue, Image.SCALE_SMOOTH);
                ImageIcon iconeFinale = new ImageIcon(imageRedimensionnee);
                image.setIcon(iconeFinale);
            } else {
                System.err.println("Image introuvable (ni JPG, ni PNG) : " + nomDeBase);
                image.setIcon(null);
            }
        });
    }

    public void enable(boolean ok) {
        entree.setEditable(ok);
        if (! ok) entree.getCaret().setBlinkRate(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        executerCommande();
    }

    private void executerCommande() {
        var commande = entree.getText().trim();
        if (commande.isEmpty()) return;
        entree.setText("");
        jeu.traiterCommande( commande);
    }

	public void mettreAJourInventaire() {
		// TODO Auto-generated method stub
		
	}
}