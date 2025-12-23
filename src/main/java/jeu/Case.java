package jeu;

import pieces.Piece;

// Représente une case de l’échiquier avec sa position et une éventuelle pièce dessus
public class Case {
    private final int ligne;
    private final int colonne;
    private Piece piece;

    // Initialise une case vide à une position donnée
    public Case(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
        this.piece = null;
    }

    // Renvoie la ligne de la case (0 en haut, 7 en bas)
    public int getLigne() {
        return ligne;
    }

    // Renvoie la colonne de la case (0 à gauche, 7 à droite)
    public int getColonne() {
        return colonne;
    }

    // Retourne la pièce présente sur cette case (null si vide)
    public Piece getPiece() {
        return piece;
    }

    // Place une pièce sur cette case (ou null pour la vider)
    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    // Indique si la case ne contient aucune pièce
    public boolean estVide() {
        return piece == null;
    }

    // Renvoie une représentation en notation échiquéenne (ex: "e4")
    @Override
    public String toString() {
        char lettreColonne = (char) ('a' + colonne);
        return lettreColonne + "" + (8 - ligne); // Ligne inversée pour correspondre à l'affichage standard
    }
}
