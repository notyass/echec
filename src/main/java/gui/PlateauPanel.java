package gui;

import jeu.Case;
import jeu.Partie;

import javax.swing.*;
import java.awt.*;

// Représente le panneau graphique contenant les 64 cases du plateau d’échecs
public class PlateauPanel extends JPanel {
    private final CaseGraphique[][] cases;
    private final Partie partie;
    private Case caseSelectionnee = null;

    // Initialise le plateau graphique et y place chaque CaseGraphique
    public PlateauPanel(Partie partie) {
        this.partie = partie;
        setLayout(new GridLayout(8, 8));
        cases = new CaseGraphique[8][8];

        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < 8; col++) {
                CaseGraphique cg = new CaseGraphique(partie, ligne, col, this);
                cases[ligne][col] = cg;
                add(cg);
            }
        }

        majAffichage(); // Affiche les pièces dès le début
    }

    // Méthode appelée lorsqu’un bouton du plateau est cliqué
    public void caseCliquee(CaseGraphique cg) {
        Case selection = partie.getEchiquier().getCase(cg.getLigne(), cg.getColonne());

        if (caseSelectionnee == null) {
            // On sélectionne une pièce si elle appartient au joueur courant
            if (!selection.estVide() && selection.getPiece().estBlanc() == partie.getJoueurCourant().estBlanc()) {
                caseSelectionnee = selection;
            }
        } else {
            // Tente de jouer le coup vers la case cliquée
            boolean ok = partie.effectuerTour(caseSelectionnee.getLigne(), caseSelectionnee.getColonne(), cg.getLigne(), cg.getColonne());
            caseSelectionnee = null;
            majAffichage();

            if (ok) {
                // Si le coup a été joué, on vérifie s’il provoque un état spécial
                majAffichage();

                if (partie.estEchecEtMat(partie.getAdversaire())) {
                    JOptionPane.showMessageDialog(this, "Échec et mat ! " + partie.getJoueurCourant().getNom() + " gagne !");
                    // Ici on pourrait désactiver les clics ou fermer la partie
                } else if (partie.estEchec(partie.getAdversaire())) {
                    JOptionPane.showMessageDialog(this, "Échec !");
                }
            }
        }
    }

    // Met à jour l’affichage de toutes les cases (symbole ou icône)
    public void majAffichage() {
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int col = 0; col < 8; col++) {
                cases[ligne][col].maj();
            }
        }
    }
}
