package pieces;

import jeu.Case;
import jeu.Echiquier;
import joueurs.Joueur;

// Représente le roi, pièce centrale à protéger dans une partie d’échecs
public class Roi extends Piece {

    // Constructeur avec position initiale
    public Roi(boolean estBlanc, Case position) {
        super(estBlanc, position);
    }

    // Constructeur sans position (à placer plus tard)
    public Roi(boolean estBlanc) {
        super(estBlanc);
    }

    // Déplacement classique du roi : 1 case dans n'importe quelle direction
    @Override
    public boolean estDeplacementValide(Case destination, Echiquier echiquier) {
        if (destination == null || destination == this.position) return false;

        int dx = Math.abs(destination.getLigne() - this.position.getLigne());
        int dy = Math.abs(destination.getColonne() - this.position.getColonne());

        return dx <= 1 && dy <= 1 &&
               (destination.estVide() || destination.getPiece().estBlanc() != estBlanc);
    }

    // Surcharge : déplacement + vérification que le roi n'entre pas sur une case menacée
    public boolean estDeplacementValide(Case destination, Echiquier echiquier, Joueur adversaire) {
        if (destination == null || destination == this.position) return false;

        int dx = Math.abs(destination.getLigne() - this.position.getLigne());
        int dy = Math.abs(destination.getColonne() - this.position.getColonne());

        if (dx > 1 || dy > 1) return false;

        // Ne pas capturer une pièce de sa propre couleur
        if (!destination.estVide() && destination.getPiece().estBlanc() == this.estBlanc) return false;

        // Simulation du déplacement pour tester la sécurité
        Case origine = this.position;
        Piece cible = destination.getPiece();

        origine.setPiece(null);
        destination.setPiece(this);
        this.position = destination;

        // La case est-elle menacée par une pièce adverse ?
        boolean caseMenacee = false;
        for (Piece p : adversaire.getPieces()) {
            if (p.getPosition() != null && p.estDeplacementValide(destination, echiquier)) {
                caseMenacee = true;
                break;
            }
        }

        // Annuler la simulation
        destination.setPiece(cible);
        this.position = origine;
        origine.setPiece(this);

        return !caseMenacee;
    }

    // Renvoie le symbole du roi (R ou r)
    @Override
    public char getSymbole() {
        return estBlanc ? 'R' : 'r';
    }
}
