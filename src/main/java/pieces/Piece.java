package pieces;

import jeu.Case;
import jeu.Echiquier;

// Classe abstraite représentant une pièce d'échecs (superclasse de toutes les pièces)
public abstract class Piece {
    protected boolean estBlanc;
    protected Case position;

    // Constructeur avec position initiale connue
    public Piece(boolean estBlanc, Case position) {
        this.estBlanc = estBlanc;
        setPosition(position);
    }

    // Constructeur sans position (sera placée plus tard sur l’échiquier)
    public Piece(boolean estBlanc) {
        this.estBlanc = estBlanc;
        this.position = null;
    }

    public boolean estBlanc() {
        return this.estBlanc;
    }

    public Case getPosition() {
        return this.position;
    }

    // Met à jour la position de la pièce et met à jour la case correspondante
    public void setPosition(Case nouvellePosition) {
        // Nettoie l’ancienne case si elle contient encore cette pièce
        if (this.position != null && this.position.getPiece() == this) {
            this.position.setPiece(null);
        }

        this.position = nouvellePosition;

        // Place cette pièce dans la nouvelle case si ce n’est pas déjà fait
        if (nouvellePosition != null && nouvellePosition.getPiece() != this) {
            nouvellePosition.setPiece(this);
        }
    }

    // À implémenter par chaque sous-classe : vérifie si le déplacement est autorisé
    public abstract boolean estDeplacementValide(Case destination, Echiquier echiquier);

    // Renvoie un symbole ASCII de la pièce ('T', 'F', etc.)
    public abstract char getSymbole();
}
