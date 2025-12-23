package jeu;

import joueurs.Joueur;
import pieces.*;

// Gère l'état global d'une partie d'échecs : joueurs, échiquier, règles du jeu
public class Partie {
    private final Echiquier echiquier;
    private final Joueur joueurBlanc;
    private final Joueur joueurNoir;
    private Joueur joueurCourant;

    // Initialise la partie avec deux joueurs et le temps imparti
    public Partie(String nomBlanc, String nomNoir, long tempsInitialMillis) {
        this.joueurBlanc = new Joueur(nomBlanc, true, tempsInitialMillis);
        this.joueurNoir = new Joueur(nomNoir, false, tempsInitialMillis);
        this.echiquier = new Echiquier();
        this.echiquier.initialiser(joueurBlanc, joueurNoir);
        this.joueurCourant = joueurBlanc;
    }

    public Joueur getJoueurCourant() {
        return joueurCourant;
    }

    public Joueur getAdversaire() {
        return joueurCourant == joueurBlanc ? joueurNoir : joueurBlanc;
    }

    public Echiquier getEchiquier() {
        return echiquier;
    }

    public Joueur getJoueurBlanc() {
        return joueurBlanc;
    }

    public Joueur getJoueurNoir() {
        return joueurNoir;
    }

    // Tente d’effectuer un coup, gère l’historique, le chrono, et alterne les joueurs
    public boolean effectuerTour(int ligneSrc, int colSrc, int ligneDest, int colDest) {
        Case depart = echiquier.getCase(ligneSrc, colSrc);
        Case destination = echiquier.getCase(ligneDest, colDest);
        boolean deplacementValide = echiquier.deplacerPiece(depart, destination, joueurCourant, getAdversaire());

        if (deplacementValide) {
            String coup = depart + " --> " + destination;
            joueurCourant.ajouterHistorique(coup);

            System.out.println("---- Historique des coups ----");
            System.out.println(joueurBlanc.getNom() + " :");
            joueurBlanc.getHistorique().forEach(c -> System.out.println("  - " + c));
            System.out.println(joueurNoir.getNom() + " :");
            joueurNoir.getHistorique().forEach(c -> System.out.println("  - " + c));
            System.out.println("-------------------------------");

            joueurCourant.arreterChrono();
            joueurCourant = getAdversaire();
            joueurCourant.demarrerChrono();
        }
        return deplacementValide;
    }

    // Vérifie si le joueur est en situation de pat (aucun coup légal et pas en échec)
    public boolean estPat(Joueur joueur) {
        if (estEchec(joueur)) return false;

        for (Piece piece : joueur.getPieces()) {
            Case origine = piece.getPosition();
            if (origine == null) continue;

            for (int ligne = 0; ligne < 8; ligne++) {
                for (int col = 0; col < 8; col++) {
                    Case destination = echiquier.getCase(ligne, col);
                    if (destination == null || destination == origine) continue;

                    Piece pieceCapturee = destination.getPiece();

                    if (piece.estDeplacementValide(destination, echiquier)) {
                        // Simulation
                        origine.setPiece(null);
                        destination.setPiece(piece);
                        piece.setPosition(destination);

                        boolean encoreEchec = estEchec(joueur);

                        // Annule simulation
                        piece.setPosition(origine);
                        destination.setPiece(pieceCapturee);
                        origine.setPiece(piece);

                        if (!encoreEchec) return false;
                    }
                }
            }
        }

        return true;
    }

    // Vérifie si le roi du joueur est en échec
    public boolean estEchec(Joueur joueur) {
        Roi roi = joueur.getRoi();
        if (roi == null || roi.getPosition() == null) return false;

        Case roiPos = roi.getPosition();

        for (Piece piece : getAdversaire().getPieces()) {
            if (piece.getPosition() != null && piece.estDeplacementValide(roiPos, echiquier)) {
                return true;
            }
        }
        return false;
    }

    // Vérifie si le joueur est en échec et mat (aucune sortie possible de l’échec)
    public boolean estEchecEtMat(Joueur joueur) {
        Roi roi = joueur.getRoi();
        if (roi == null || roi.getPosition() == null) return true;

        Case roiPos = roi.getPosition();

        // 1. Le roi peut-il s’échapper ?
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                int newLigne = roiPos.getLigne() + dx;
                int newCol = roiPos.getColonne() + dy;
                Case destination = echiquier.getCase(newLigne, newCol);

                if (destination == null) continue;
                Piece ancienneCible = destination.getPiece();
                Case ancienneCase = roi.getPosition();

                if (roi.estDeplacementValide(destination, echiquier, getAdversaire())) {
                    // Simulation
                    ancienneCase.setPiece(null);
                    destination.setPiece(roi);
                    roi.setPosition(destination);

                    boolean enEchec = estEchec(joueur);

                    // Annuler
                    roi.setPosition(ancienneCase);
                    ancienneCase.setPiece(roi);
                    destination.setPiece(ancienneCible);

                    if (!enEchec) return false;
                }
            }
        }

        // 2. Une autre pièce peut-elle intercepter ou bloquer ?
        for (Piece piece : joueur.getPieces()) {
            if (piece instanceof Roi || piece.getPosition() == null) continue;

            Case origine = piece.getPosition();

            for (int l = 0; l < 8; l++) {
                for (int c = 0; c < 8; c++) {
                    Case destination = echiquier.getCase(l, c);
                    if (destination == null || destination == origine) continue;

                    if (!piece.estDeplacementValide(destination, echiquier)) continue;

                    // Simulation
                    Piece cible = destination.getPiece();
                    origine.setPiece(null);
                    destination.setPiece(piece);
                    piece.setPosition(destination);

                    boolean enEchec = estEchec(joueur);

                    // Annuler
                    piece.setPosition(origine);
                    origine.setPiece(piece);
                    destination.setPiece(cible);

                    if (!enEchec) return false;
                }
            }
        }

        return true;
    }

    public boolean estTerminee() {
        return estEchecEtMat(joueurBlanc) || estEchecEtMat(joueurNoir);
    }

    public Joueur getVainqueur() {
        if (estEchecEtMat(joueurBlanc)) return joueurNoir;
        if (estEchecEtMat(joueurNoir)) return joueurBlanc;
        return null;
    }

    // Affiche l'état actuel de la partie (utile pour debug console)
    public void afficherEtat() {
        echiquier.afficherEchiquier();
        System.out.println("Tour de: " + joueurCourant.getNom());
        System.out.println(joueurBlanc.getNom() + " (Blanc): " + joueurBlanc.getTempsFormate());
        System.out.println(joueurNoir.getNom() + " (Noir): " + joueurNoir.getTempsFormate());
    }
}
