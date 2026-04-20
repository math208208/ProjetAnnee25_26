package jeu;

import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;

public class GUI implements ActionListener {
	private final Jeu jeu;
	private final JFrame fenetre;
	private final JTextField entree;
	private final JTextArea texte;
	private final JLabel image;
	private final JPanel panneauImage;
	private ImageIcon imageOriginale;

	private static final String NOM_REPERTOIRE_IMAGES = "jeu/images/";
	private static final Color COULEUR_FOND = new Color(13, 15, 17);
	private static final Color COULEUR_TEXTE = new Color(224, 217, 202);
	private static final Color COULEUR_MUTED = new Color(169, 161, 145);
	private static final Color COULEUR_ACCENT = new Color(168, 39, 43);
	private static final Color COULEUR_CHAMP = new Color(8, 10, 12);

	public GUI(Jeu jeu) {
		this.jeu = jeu;

		fenetre = new JFrame("L'Heritage Maudit");
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fenetre.setMinimumSize(new Dimension(900, 650));

		texte = new JTextArea();
		texte.setEditable(false);
		texte.setLineWrap(true);
		texte.setWrapStyleWord(true);
		texte.setFont(new Font(Font.SERIF, Font.PLAIN, 17));
		texte.setForeground(COULEUR_TEXTE);
		texte.setBackground(COULEUR_CHAMP);
		texte.setCaretColor(COULEUR_TEXTE);
		texte.setMargin(new Insets(18, 20, 18, 20));

		JScrollPane scroll = new JScrollPane(texte);
		scroll.setPreferredSize(new Dimension(860, 360));
		scroll.setBorder(BorderFactory.createLineBorder(new Color(46, 43, 39), 1));
		scroll.getViewport().setBackground(COULEUR_CHAMP);

		entree = new JTextField();
		entree.addActionListener(this);
		entree.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		entree.setForeground(COULEUR_TEXTE);
		entree.setCaretColor(COULEUR_TEXTE);
		entree.setBackground(new Color(18, 20, 23));
		entree.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COULEUR_ACCENT, 2),
				BorderFactory.createEmptyBorder(9, 12, 9, 12)));

		image = new JLabel();
		image.setHorizontalAlignment(SwingConstants.CENTER);
		image.setVerticalAlignment(SwingConstants.CENTER);
		image.setForeground(COULEUR_MUTED);

		panneauImage = new JPanel(new BorderLayout());
		panneauImage.setBackground(Color.BLACK);
		panneauImage.setPreferredSize(new Dimension(860, 330));
		panneauImage.setBorder(BorderFactory.createLineBorder(new Color(54, 48, 42), 1));
		panneauImage.add(image, BorderLayout.CENTER);
		panneauImage.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				rafraichirImageAffichee();
			}
		});

		JLabel etiquetteCommande = new JLabel("Commande");
		etiquetteCommande.setForeground(COULEUR_TEXTE);
		etiquetteCommande.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		JLabel aideCommande = new JLabel("Entree pour valider | ? pour l'aide");
		aideCommande.setForeground(COULEUR_MUTED);
		aideCommande.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

		JPanel enteteSaisie = new JPanel(new BorderLayout());
		enteteSaisie.setOpaque(false);
		enteteSaisie.add(etiquetteCommande, BorderLayout.WEST);
		enteteSaisie.add(aideCommande, BorderLayout.EAST);

		JPanel panneauSaisie = new JPanel(new BorderLayout(0, 6));
		panneauSaisie.setOpaque(false);
		panneauSaisie.add(enteteSaisie, BorderLayout.NORTH);
		panneauSaisie.add(entree, BorderLayout.CENTER);

		var panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(COULEUR_FOND);
		panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		panel.add(panneauImage, BorderLayout.NORTH);
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(panneauSaisie, BorderLayout.SOUTH);

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

			String[] extensions = { ".jpg", ".png" };
			for (String ext : extensions) {
				imageURL = getClass().getClassLoader().getResource(NOM_REPERTOIRE_IMAGES + nomDeBase + ext);
				if (imageURL != null)
					break;
			}

			if (imageURL != null) {
				imageOriginale = new ImageIcon(imageURL);
				rafraichirImageAffichee();
			} else {
				System.err.println("Image introuvable (ni JPG, ni PNG) : " + nomDeBase);
				imageOriginale = null;
				image.setIcon(null);
				image.setText("Image introuvable : " + nomDeBase);
			}
		});
	}

	private void rafraichirImageAffichee() {
		if (imageOriginale == null) {
			return;
		}

		int largeurDisponible = Math.max(1, panneauImage.getWidth() - 2);
		int hauteurDisponible = Math.max(1, panneauImage.getHeight() - 2);
		int largeurOriginale = imageOriginale.getIconWidth();
		int hauteurOriginale = imageOriginale.getIconHeight();

		if (largeurOriginale <= 0 || hauteurOriginale <= 0) {
			return;
		}

		double ratio = Math.min((double) largeurDisponible / largeurOriginale,
				(double) hauteurDisponible / hauteurOriginale);
		int largeur = Math.max(1, (int) Math.round(largeurOriginale * ratio));
		int hauteur = Math.max(1, (int) Math.round(hauteurOriginale * ratio));

		Image imageRedimensionnee = imageOriginale.getImage().getScaledInstance(largeur, hauteur, Image.SCALE_SMOOTH);
		image.setText("");
		image.setIcon(new ImageIcon(imageRedimensionnee));
	}

	public void enable(boolean ok) {
		entree.setEditable(ok);
		if (!ok)
			entree.getCaret().setBlinkRate(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		executerCommande();
	}

	private void executerCommande() {
		var commande = entree.getText().trim();
		if (commande.isEmpty())
			return;
		entree.setText("");
		jeu.traiterCommande(commande);
	}

}
