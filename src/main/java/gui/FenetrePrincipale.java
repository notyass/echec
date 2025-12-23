package gui;

import jeu.Partie;
import joueurs.Joueur;
import jeu.Case;
import pieces.Piece;

import javax.swing.*;
import java.awt.*;

// Fenêtre principale du jeu d'échecs (interface graphique)
public class FenetrePrincipale extends JFrame {
    private final Partie partie;
    private final JLabel chronoHaut;
    private final JLabel chronoBas;
    private final JButton[][] boutons = new JButton[8][8];
    private Case caseSelectionnee = null;
    private boolean partieTerminee = false;

    // Initialise la fenêtre avec le plateau, les chronos et la logique de départ
    public FenetrePrincipale(Partie partie) {
        this.partie = partie;
        setTitle("Jeu d'Échecs");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Création des chronos affichés en haut et en bas
        chronoHaut = new JLabel(partie.getJoueurNoir().getNom() + ": " + partie.getJoueurNoir().getTempsFormate(), SwingConstants.CENTER);
        chronoBas = new JLabel(partie.getJoueurBlanc().getNom() + ": " + partie.getJoueurBlanc().getTempsFormate(), SwingConstants.CENTER);
        add(chronoHaut, BorderLayout.NORTH);
        add(chronoBas, BorderLayout.SOUTH);

        // Création du plateau de jeu sous forme de grille de boutons
        JPanel plateau = new JPanel(new GridLayout(8, 8));
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < 8; col++) {
                JButton bouton = new JButton();
                bouton.setOpaque(true);

                // Alterne les couleurs des cases façon échiquier
                bouton.setBackground((ligne + col) % 2 == 0 ? new Color(240, 217, 181) : new Color(181, 136, 99));

                int l = ligne, c = col;
                bouton.addActionListener(e -> gererClic(l, c)); // Gestion du clic sur une case

                boutons[ligne][col] = bouton;
                plateau.add(bouton);
            }
        }
        add(plateau, BorderLayout.CENTER);
        setVisible(true);
        rafraichir(); // Met à jour l'affichage initial du plateau

        // Vérifie si la partie est déjà finie (cas d'un échec et mat immédiat)
        if (partie.estEchecEtMat(partie.getJoueurBlanc())) {
            partieTerminee = true;
            JOptionPane.showMessageDialog(this, "Échec et mat dès le début ! " + partie.getJoueurNoir().getNom() + " gagne la partie.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        if (partie.estEchecEtMat(partie.getJoueurNoir())) {
            partieTerminee = true;
            JOptionPane.showMessageDialog(this, "Échec et mat dès le début ! " + partie.getJoueurBlanc().getNom() + " gagne la partie.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        // Timer qui gère le temps restant des joueurs (appelé chaque seconde)
        javax.swing.Timer chronoTimer = new javax.swing.Timer(1000, e -> {
            partie.getJoueurCourant().arreterChrono(); // Stoppe puis redémarre le chrono pour actualiser le temps
            partie.getJoueurCourant().demarrerChrono();

            chronoHaut.setText(partie.getJoueurNoir().getNom() + ": " + partie.getJoueurNoir().getTempsFormate());
            chronoBas.setText(partie.getJoueurBlanc().getNom() + ": " + partie.getJoueurBlanc().getTempsFormate());

            // Vérifie si un joueur a perdu à cause du temps
            if (partie.getJoueurBlanc().getTempsRestantMillis() <= 0) {
                JOptionPane.showMessageDialog(this, "Temps écoulé ! " + partie.getJoueurNoir().getNom() + " gagne !");
                System.exit(0);
            }
            if (partie.getJoueurNoir().getTempsRestantMillis() <= 0) {
                JOptionPane.showMessageDialog(this, "Temps écoulé ! " + partie.getJoueurBlanc().getNom() + " gagne !");
                System.exit(0);
            }
        });
        chronoTimer.start();
    }

    // Gère la sélection d'une pièce et le déplacement d'une pièce
    private void gererClic(int ligne, int col) {
        if (partieTerminee) return;

        Case cible = partie.getEchiquier().getCase(ligne, col);

        if (caseSelectionnee == null) {
            // Sélection d'une pièce si elle appartient au joueur courant
            if (!cible.estVide() && cible.getPiece().estBlanc() == partie.getJoueurCourant().estBlanc()) {
                caseSelectionnee = cible;
                boutons[ligne][col].setBackground(Color.YELLOW); // Mise en surbrillance
            }
        } else {
            Joueur attaquant = partie.getJoueurCourant();
            Joueur defenseur = partie.getAdversaire();

            boolean ok = partie.effectuerTour(caseSelectionnee.getLigne(), caseSelectionnee.getColonne(), ligne, col);
            caseSelectionnee = null;
            rafraichir();

            if (ok) {
                // Vérifie si la partie est finie après le coup
                if (partie.estEchecEtMat(defenseur)) {
                    partieTerminee = true;
                    JOptionPane.showMessageDialog(this, "Échec et mat ! Le joueur " + attaquant.getNom() + " gagne !", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                } else if (partie.estEchec(defenseur)) {
                    JOptionPane.showMessageDialog(this, "Échec !");
                } else if (partie.estPat(defenseur)) {
                    partieTerminee = true;
                    JOptionPane.showMessageDialog(this, "Pat ! Partie nulle.", "Fin de partie", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        }
    }

    // Rafraîchit l'affichage du plateau en fonction de l'état de l'échiquier
    private void rafraichir() {
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < 8; col++) {
                Case c = partie.getEchiquier().getCase(ligne, col);
                JButton b = boutons[ligne][col];
                b.setIcon(null);
                b.setText("");
                b.setBackground((ligne + col) % 2 == 0 ? new Color(240, 217, 181) : new Color(181, 136, 99));

                if (!c.estVide()) {
                    char symbole = c.getPiece().getSymbole();
                    String couleur = Character.isUpperCase(symbole) ? "B" : "N";
                    String typeNom;
                    switch (Character.toUpperCase(symbole)) {
                        case 'P': typeNom = "pion"; break;
                        case 'T': typeNom = "tour"; break;
                        case 'C': typeNom = "cavalier"; break;
                        case 'F': typeNom = "fou"; break;
                        case 'D': typeNom = "reine"; break;
                        case 'R': typeNom = "roi"; break;
                        default: typeNom = null;
                    }
                    if (typeNom != null) {
                        String chemin = "resources/images/" + typeNom + couleur + ".png";
                        java.net.URL url = getClass().getResource(chemin);

                        if (url != null) {
                            ImageIcon icon = new ImageIcon(url);
                            Image scaled = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                            b.setIcon(new ImageIcon(scaled));
                        } else {
                            b.setText(String.valueOf(symbole)); // Affiche le caractère si l'image est absente
                        }
                    }
                }
            }
        }

        // Mise à jour des chronos
        chronoHaut.setText(partie.getJoueurNoir().getNom() + ": " + partie.getJoueurNoir().getTempsFormate());
        chronoBas.setText(partie.getJoueurBlanc().getNom() + ": " + partie.getJoueurBlanc().getTempsFormate());
    }

    // Point d'entrée du programme
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Demande le nom des deux joueurs
            String nom1 = JOptionPane.showInputDialog("Nom du premier joueur :");
            String nom2 = JOptionPane.showInputDialog("Nom du deuxième joueur :");

            if (nom1 == null || nom2 == null || nom1.isEmpty() || nom2.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Les deux pseudos doivent être fournis.");
                System.exit(0);
            }

            // Choix aléatoire du joueur qui commence avec les blancs
            boolean joueur1EstBlanc = Math.random() < 0.5;
            String blanc = joueur1EstBlanc ? nom1 : nom2;
            String noir = joueur1EstBlanc ? nom2 : nom1;

            // Demande la durée de jeu
            String input = JOptionPane.showInputDialog("Temps de jeu par joueur (en minutes) :");
            if (input == null || input.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vous devez entrer une durée.");
                System.exit(0);
            }

            int minutes = 5; // Valeur par défaut
            try {
                minutes = Integer.parseInt(input);
                if (minutes <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Durée invalide. Format attendu : nombre entier positif.");
                System.exit(0);
            }

            long tempsMillis = minutes * 60L * 1000L;
            Partie partie = new Partie(blanc, noir, tempsMillis);

            new FenetrePrincipale(partie);
        });
    }
}
