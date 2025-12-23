package jeu;

import joueurs.Joueur;
import pieces.*;

// Représente l'échiquier du jeu avec sa grille de cases et la logique associée
public class Echiquier {
    private final Case[][] grille;

    // Création des 64 cases vides de l’échiquier
    public Echiquier() {
        grille = new Case[8][8];
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 8; colonne++) {
                grille[ligne][colonne] = new Case(ligne, colonne);
            }
        }
    }

    // Récupère une case à partir de ses coordonnées (ou null si hors limites)
    public Case getCase(int ligne, int colonne) {
        if (ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8) {
            return grille[ligne][colonne];
        }
        return null;
    }

    // Place une pièce sur une case donnée et l’ajoute à la liste du joueur
    private void placerEtAjouter(Piece piece, int ligne, int colonne, Joueur joueur) {
        Case c = grille[ligne][colonne];
        piece.setPosition(c);
        joueur.ajouterPiece(piece);
    }

    // Gère le déplacement d’une pièce (y compris la capture et la promotion)
    public boolean deplacerPiece(Case depart, Case destination, Joueur joueur, Joueur adversaire) {
        Piece piece = depart.getPiece();
        if (piece != null && piece.estDeplacementValide(destination, this)) {
            Piece cible = destination.getPiece();

            // Capture éventuelle
            if (cible != null && cible.estBlanc() != joueur.estBlanc()) {
                adversaire.retirerPiece(cible);
                if (cible instanceof Roi) {
                    System.out.println("Le roi a été capturé. Fin de la partie.");
                }
            }

            piece.setPosition(destination);
            verifierPromotion(piece, joueur);
            return true;
        }
        return false;
    }

    // Place les pièces des deux joueurs au début de la partie
    public void initialiser(Joueur joueurBlanc, Joueur joueurNoir) {
        // Pièces blanches
        placerEtAjouter(new Tour(true), 7, 0, joueurBlanc);
        placerEtAjouter(new Cavalier(true), 7, 1, joueurBlanc);
        placerEtAjouter(new Fou(true), 7, 2, joueurBlanc);
        placerEtAjouter(new Dame(true), 7, 3, joueurBlanc);
        placerEtAjouter(new Roi(true), 7, 4, joueurBlanc);
        placerEtAjouter(new Fou(true), 7, 5, joueurBlanc);
        placerEtAjouter(new Cavalier(true), 7, 6, joueurBlanc);
        placerEtAjouter(new Tour(true), 7, 7, joueurBlanc);
        for (int col = 0; col < 8; col++) {
            placerEtAjouter(new Pion(true), 6, col, joueurBlanc);
        }

        // Pièces noires
        placerEtAjouter(new Tour(false), 0, 0, joueurNoir);
        placerEtAjouter(new Cavalier(false), 0, 1, joueurNoir);
        placerEtAjouter(new Fou(false), 0, 2, joueurNoir);
        placerEtAjouter(new Dame(false), 0, 3, joueurNoir);
        placerEtAjouter(new Roi(false), 0, 4, joueurNoir);
        placerEtAjouter(new Fou(false), 0, 5, joueurNoir);
        placerEtAjouter(new Cavalier(false), 0, 6, joueurNoir);
        placerEtAjouter(new Tour(false), 0, 7, joueurNoir);
        for (int col = 0; col < 8; col++) {
            placerEtAjouter(new Pion(false), 1, col, joueurNoir);
        }
    }

    // Promotion automatique des pions en dame lorsqu’ils atteignent l’extrémité
    public void verifierPromotion(Piece piece, Joueur joueur) {
        if (!(piece instanceof Pion)) return;

        int ligne = piece.getPosition().getLigne();
        boolean estBlanc = piece.estBlanc();

        if ((estBlanc && ligne == 0) || (!estBlanc && ligne == 7)) {
            Case caseActuelle = piece.getPosition();
            caseActuelle.setPiece(null);
            joueur.retirerPiece(piece);
            Piece dame = new Dame(estBlanc);
            dame.setPosition(caseActuelle);
            joueur.ajouterPiece(dame);
        }
    }

    // Vérifie si le joueur est en échec et mat
    public boolean estEchecEtMat(Joueur joueur, Joueur adversaire) {
        Roi roi = joueur.getRoi();
        if (roi == null || roi.getPosition() == null) return true;

        Case roiCase = roi.getPosition();

        // Si une pièce adverse peut atteindre le roi
        for (Piece p : adversaire.getPieces()) {
            if (p.estDeplacementValide(roiCase, this)) {

                // Teste tous les mouvements possibles du joueur pour tenter de sortir de l’échec
                for (Piece piece : joueur.getPieces()) {
                    if (piece.getPosition() == null) continue;

                    for (int l = 0; l < 8; l++) {
                        for (int c = 0; c < 8; c++) {
                            Case to = getCase(l, c);
                            if (to == null) continue;

                            if (piece.estDeplacementValide(to, this)) {
                                Case from = piece.getPosition();
                                Piece sauvegarde = to.getPiece();

                                // Simulation du coup
                                from.setPiece(null);
                                piece.setPosition(to);

                                boolean encoreEchec = false;
                                for (Piece ennemi : adversaire.getPieces()) {
                                    if (ennemi.estDeplacementValide(roi.getPosition(), this)) {
                                        encoreEchec = true;
                                        break;
                                    }
                                }

                                // Annule la simulation
                                piece.setPosition(from);
                                from.setPiece(piece);
                                to.setPiece(sauvegarde);

                                if (!encoreEchec) return false;
                            }
                        }
                    }
                }

                // Aucun coup ne peut sauver le roi → mat
                return true;
            }
        }
        return false;
    }

    // Affiche l’échiquier en console (utile pour debug)
    public void afficherEchiquier() {
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 8; colonne++) {
                Case c = grille[ligne][colonne];
                if (c.estVide()) {
                    System.out.print(". ");
                } else {
                    System.out.print(c.getPiece().getSymbole() + " ");
                }
            }
            System.out.println(8 - ligne);
        }
        System.out.println("a b c d e f g h");
    }
}
