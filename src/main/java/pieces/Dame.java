package pieces;

import jeu.Case;
import jeu.Echiquier;

// Représente une dame (reine) avec sa logique de déplacement
public class Dame extends Piece {

    // Constructeur avec position initiale
    public Dame(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (assignée plus tard)
    public Dame(boolean estBlanc) {
        super(estBlanc);
    }

    // Vérifie si le déplacement est valide selon les règles de la dame
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int dx = destination.getLigne() - this.position.getLigne();
        int dy = destination.getColonne() - this.position.getColonne();

        // La dame peut se déplacer en ligne droite ou en diagonale
        if (dx == 0 || dy == 0 || Math.abs(dx) == Math.abs(dy)) {
            int pasX = Integer.compare(dx, 0); // direction de déplacement sur les lignes
            int pasY = Integer.compare(dy, 0); // direction sur les colonnes
            int x = this.position.getLigne() + pasX;
            int y = this.position.getColonne() + pasY;

            // Vérifie que toutes les cases entre départ et arrivée sont libres
            while (x != destination.getLigne() || y != destination.getColonne()) {
                if (!echiquier.getCase(x, y).estVide()) return false;
                x += pasX;
                y += pasY;
            }

            // Arrivée possible si la case est vide ou occupée par une pièce ennemie
            return destination.estVide() || destination.getPiece().estBlanc() != estBlanc;
        }

        return false; // Ni ligne, ni colonne, ni diagonale : déplacement invalide
    }

    // Renvoie le symbole de la dame (D pour blanc, d pour noir)
    @Override
    public char getSymbole() {
        return estBlanc ? 'D' : 'd';
    }
}
