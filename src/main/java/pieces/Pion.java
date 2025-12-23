package pieces;

import jeu.Case;
import jeu.Echiquier;

// Représente un pion avec ses règles spécifiques de déplacement
public class Pion extends Piece {

    // Constructeur avec position initiale
    public Pion(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (à assigner ensuite)
    public Pion(boolean estBlanc) {
        super(estBlanc);
    }

    // Vérifie si le déplacement est valide pour un pion
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int ligneDepart = this.estBlanc ? 6 : 1; // Ligne initiale du pion
        int dir = this.estBlanc ? -1 : 1;        // Direction du déplacement (vers le haut si blanc)

        int dx = destination.getLigne() - this.position.getLigne();
        int dy = destination.getColonne() - this.position.getColonne();

        // Avance simple d’une case
        if (dy == 0 && dx == dir && destination.estVide()) return true;

        // Avance double depuis la position de départ
        if (dy == 0 && dx == 2 * dir && this.position.getLigne() == ligneDepart) {
            Case inter = echiquier.getCase(this.position.getLigne() + dir, this.position.getColonne());
            return destination.estVide() && inter.estVide();
        }

        // Prise diagonale
        if (Math.abs(dy) == 1 && dx == dir && !destination.estVide()) {
            return destination.getPiece().estBlanc() != this.estBlanc;
        }

        return false;
    }

    // Renvoie le symbole du pion (P pour blanc, p pour noir)
    @Override
    public char getSymbole() {
        return estBlanc ? 'P' : 'p';
    }
}
