package pieces;

import jeu.Case;
import jeu.Echiquier;

// Représente une tour avec sa logique de déplacement en ligne ou en colonne
public class Tour extends Piece {

    // Constructeur avec position initiale
    public Tour(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (à placer plus tard)
    public Tour(boolean estBlanc) {
        super(estBlanc);
    }

    // Vérifie si le déplacement proposé est valide pour une tour
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int dx = destination.getLigne() - this.position.getLigne();
        int dy = destination.getColonne() - this.position.getColonne();

        // La tour ne peut se déplacer que horizontalement ou verticalement
        if (dx != 0 && dy != 0) return false;

        int pasX = Integer.compare(dx, 0); // Direction verticale
        int pasY = Integer.compare(dy, 0); // Direction horizontale
        int x = this.position.getLigne() + pasX;
        int y = this.position.getColonne() + pasY;

        // Vérifie que le chemin jusqu'à destination est libre
        while (x != destination.getLigne() || y != destination.getColonne()) {
            if (!echiquier.getCase(x, y).estVide()) return false;
            x += pasX;
            y += pasY;
        }

        // Accepte destination vide ou pièce ennemie
        return destination.estVide() || destination.getPiece().estBlanc() != estBlanc;
    }

    // Renvoie le symbole de la tour (T pour blanc, t pour noir)
    @Override
    public char getSymbole() {
        return estBlanc ? 'T' : 't';
    }
}
