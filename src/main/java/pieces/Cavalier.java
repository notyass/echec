package pieces;

import jeu.Case;
import jeu.Echiquier;

// Représente un cavalier (cheval) avec sa logique de déplacement
public class Cavalier extends Piece {

    // Constructeur avec position initiale
    public Cavalier(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (peut être placée plus tard)
    public Cavalier(boolean estBlanc) {
        super(estBlanc);
    }

    // Vérifie si le déplacement demandé est valide pour un cavalier
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int dx = Math.abs(destination.getLigne() - this.position.getLigne());
        int dy = Math.abs(destination.getColonne() - this.position.getColonne());

        // Le cavalier bouge en L : 2 cases dans une direction, 1 dans l'autre
        return ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) &&
               (destination.estVide() || destination.getPiece().estBlanc() != estBlanc);
    }

    // Retourne le caractère représentant le cavalier (C/c)
    @Override
    public char getSymbole() {
        return estBlanc ? 'C' : 'c';
    }
}
