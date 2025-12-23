package pieces;

import jeu.Case;
import jeu.Echiquier;

// Représente un fou avec son comportement de déplacement en diagonale
public class Fou extends Piece {

    // Constructeur avec position initiale
    public Fou(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (à assigner ensuite)
    public Fou(boolean estBlanc) {
        super(estBlanc);
    }

    // Vérifie si un déplacement proposé est valide pour un fou
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int dx = destination.getLigne() - this.position.getLigne();
        int dy = destination.getColonne() - this.position.getColonne();

        // Le fou ne peut se déplacer que en diagonale (|dx| = |dy|)
        if (Math.abs(dx) != Math.abs(dy)) return false;

        int pasX = Integer.compare(dx, 0); // direction ligne
        int pasY = Integer.compare(dy, 0); // direction colonne
        int x = this.position.getLigne() + pasX;
        int y = this.position.getColonne() + pasY;

        // Vérifie que toutes les cases entre départ et arrivée sont libres
        while (x != destination.getLigne() || y != destination.getColonne()) {
            if (!echiquier.getCase(x, y).estVide()) return false;
            x += pasX;
            y += pasY;
        }

        // Autorise l'arrivée sur une case vide ou occupée par une pièce adverse
        return destination.estVide() || destination.getPiece().estBlanc() != estBlanc;
    }

    // Renvoie le symbole du fou (F pour blanc, f pour noir)
    @Override
    public char getSymbole() {
        return estBlanc ? 'F' : 'f';
    }
}
