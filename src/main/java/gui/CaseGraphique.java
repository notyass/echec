package gui;

import jeu.Case;
import jeu.Partie;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Représente un bouton graphique associé à une case de l'échiquier
public class CaseGraphique extends JButton {
    private final int ligne;
    private final int colonne;
    private final Partie partie;
    private final PlateauPanel parent;

    // Chaque bouton correspond à une case de l'échiquier dans l'interface
    public CaseGraphique(Partie partie, int ligne, int colonne, PlateauPanel parent) {
        this.partie = partie;
        this.ligne = ligne;
        this.colonne = colonne;
        this.parent = parent;

        // Quand on clique sur une case, on informe le plateau qu’elle a été cliquée
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parent.caseCliquee(CaseGraphique.this);
            }
        });
    }

    // Met à jour l'affichage du texte sur la case en fonction de son contenu
    public void maj() {
        Case c = partie.getEchiquier().getCase(ligne, colonne);
        if (c.estVide()) {
            setText(""); // Rien à afficher si la case est vide
        } else {
            setText(Character.toString(c.getPiece().getSymbole())); // Affiche le symbole de la pièce
        }
    }

    // Fournit la ligne de la case sur l’échiquier
    public int getLigne() {
        return ligne;
    }

    // Fournit la colonne de la case sur l’échiquier
    public int getColonne() {
        return colonne;
    }
}
