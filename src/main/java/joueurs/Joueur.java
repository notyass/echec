package joueurs;

import pieces.Piece;
import pieces.Roi;

import java.util.ArrayList;
import java.util.List;

// Représente un joueur dans la partie (nom, couleur, temps, pièces, historique)
public class Joueur {
    private final String nom;
    private final boolean estBlanc;
    private final List<Piece> pieces;           // Liste des pièces encore en jeu pour ce joueur
    private final List<String> historique;      // Historique des coups joués

    private long tempsRestantMillis;
    private long debutChrono;
    private boolean chronoEnCours;

    // Initialise un joueur avec un nom, une couleur, et le temps de jeu imparti
    public Joueur(String nom, boolean estBlanc, long tempsInitialMillis) {
        this.nom = nom;
        this.estBlanc = estBlanc;
        this.pieces = new ArrayList<>();
        this.historique = new ArrayList<>();
        this.tempsRestantMillis = tempsInitialMillis;
        this.chronoEnCours = false;
    }

    // Ajoute une pièce à la liste de ce joueur (ex: au début de partie)
    public void ajouterPiece(Piece piece) {
        pieces.add(piece);
    }

    // Retire une pièce capturée
    public void retirerPiece(Piece piece) {
        pieces.remove(piece);
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public boolean estBlanc() {
        return estBlanc;
    }

    public String getNom() {
        return nom;
    }

    // Démarre le chrono du joueur (appelé quand c'est son tour)
    public void demarrerChrono() {
        if (!chronoEnCours) {
            debutChrono = System.currentTimeMillis();
            chronoEnCours = true;
        }
    }

    // Arrête le chrono et déduit le temps écoulé
    public void arreterChrono() {
        if (chronoEnCours) {
            long tempsEcoule = System.currentTimeMillis() - debutChrono;
            tempsRestantMillis = Math.max(0, tempsRestantMillis - tempsEcoule);
            chronoEnCours = false;
        }
    }

    public long getTempsRestantMillis() {
        return tempsRestantMillis;
    }

    // Renvoie le temps formaté sous forme mm:ss
    public String getTempsFormate() {
        long totalSec = tempsRestantMillis / 1000;
        long min = totalSec / 60;
        long sec = totalSec % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // Vérifie si le joueur a encore son roi (utile en cas de détection de fin)
    public boolean aEncoreSonRoi() {
        return pieces.stream().anyMatch(p -> p instanceof Roi);
    }

    // Renvoie l’objet Roi du joueur (ou null si absent)
    public Roi getRoi() {
        for (Piece p : pieces) {
            if (p instanceof Roi) return (Roi) p;
        }
        return null;
    }

    // Ajoute un coup à l'historique
    public void ajouterHistorique(String coup) {
        historique.add(coup);
    }

    public List<String> getHistorique() {
        return historique;
    }

    // Affiche les coups joués en console (utile en debug)
    public void afficherHistorique() {
        System.out.println("Historique de " + nom + " :");
        for (String coup : historique) {
            System.out.println(" - " + coup);
        }
    }
}
