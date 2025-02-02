package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Livreur extends Utilisateur implements Observateur {
    private List<Commande> commandesALivrer;
    private List<Commande> historiqueLivraisons;
    private boolean disponible;
    private String vehicule;
    private String zone;
    private int nombreLivraisonsEffectuees;
    private double noteMoyenne;
    private int nombreEvaluations;
    private Date derniereLivraison;
    private Position positionActuelle;
    private double totalPourboires;
    private boolean enPause;
    private int tempsEstimeProchaineLivraison;
    private StatutLivreur statut;

    public enum StatutLivreur {
        DEBUTANT("Débutant", 0),
        CONFIRME("Confirmé", 50),
        EXPERT("Expert", 100);

        private String libelle;
        private int livraisonsRequises;

        StatutLivreur(String libelle, int livraisonsRequises) {
            this.libelle = libelle;
            this.livraisonsRequises = livraisonsRequises;
        }

        public String getLibelle() { return libelle; }
        public int getLivraisonsRequises() { return livraisonsRequises; }
    }

    public static class Position {
        private double latitude;
        private double longitude;

        public Position(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }

        public double calculateDistance(Position other) {
            // Calcul simple de distance euclidienne
            double dx = this.latitude - other.latitude;
            double dy = this.longitude - other.longitude;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }

    public Livreur(String id, String nom, String email, String motDePasse, String vehicule, String zone) {
        super(id, nom, email, motDePasse);
        if (vehicule == null || vehicule.trim().isEmpty()) {
            throw new IllegalArgumentException("Le véhicule est requis");
        }
        if (zone == null || zone.trim().isEmpty()) {
            throw new IllegalArgumentException("La zone est requise");
        }

        this.commandesALivrer = new ArrayList<>();
        this.historiqueLivraisons = new ArrayList<>();
        this.disponible = true;
        this.vehicule = vehicule;
        this.zone = zone;
        this.nombreLivraisonsEffectuees = 0;
        this.noteMoyenne = 0.0;
        this.nombreEvaluations = 0;
        this.positionActuelle = new Position(0.0, 0.0);
        this.totalPourboires = 0.0;
        this.enPause = false;
        this.tempsEstimeProchaineLivraison = 0;
        this.statut = StatutLivreur.DEBUTANT;
    }

    public void ajouterLivraison(Commande commande) {
        if (!estDisponiblePourNouvelleLivraison()) {
            throw new IllegalStateException("Le livreur n'est pas disponible pour de nouvelles livraisons");
        }
        if (commande == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }

        commandesALivrer.add(commande);
        commande.ajouterObservateur(this);
        commande.setLivreur(this);

        updateDisponibilite();
        updateTempsEstimation();
    }

    public void terminerLivraison(Commande commande) {
        if (!commandesALivrer.contains(commande)) {
            throw new IllegalArgumentException("Cette commande n'est pas assignée à ce livreur");
        }
        if (commande.getEtat() != EtatCommande.PRETE) {
            throw new IllegalStateException("La commande doit être prête avant d'être livrée");
        }

        commandesALivrer.remove(commande);
        historiqueLivraisons.add(commande);
        commande.changerEtat(EtatCommande.LIVREE);

        nombreLivraisonsEffectuees++;
        derniereLivraison = new Date();

        updateStatut();
        updateDisponibilite();
    }

    public void signalerProblemeLivraison(Commande commande, String raison) {
        if (!commandesALivrer.contains(commande)) {
            throw new IllegalArgumentException("Cette commande n'est pas assignée à ce livreur");
        }
        if (raison == null || raison.trim().isEmpty()) {
            throw new IllegalArgumentException("Une raison doit être fournie");
        }

        commande.setCommentaires("Problème de livraison: " + raison);
        commandesALivrer.remove(commande);
        commande.changerEtat(EtatCommande.ANNULEE);

        updateDisponibilite();
        updateTempsEstimation();
    }

    private void updateStatut() {
        if (nombreLivraisonsEffectuees >= StatutLivreur.EXPERT.getLivraisonsRequises()) {
            statut = StatutLivreur.EXPERT;
        } else if (nombreLivraisonsEffectuees >= StatutLivreur.CONFIRME.getLivraisonsRequises()) {
            statut = StatutLivreur.CONFIRME;
        }
    }

    private void updateDisponibilite() {
        this.disponible = !enPause && commandesALivrer.size() < getCapaciteMaxLivraisons();
    }

    private int getCapaciteMaxLivraisons() {
        switch (statut) {
            case EXPERT: return 4;
            case CONFIRME: return 3;
            default: return 2;
        }
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            // Vérifier si la commande est prête et en mode livraison
            if (commande.getEtat() == EtatCommande.PRETE &&
                    commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON &&
                    this.isDisponible()) {

                // Vérifier que la commande n'est pas déjà prise en charge
                if (!commandesALivrer.contains(commande)) {
                    commandesALivrer.add(commande);
                    commande.setLivreur(this);

                    String message = String.format(
                            "Nouvelle commande disponible pour livraison : %s\nClient : %s\nAdresse : %s",
                            commande.getNumeroCommande(),
                            commande.getClient().getNom(),
                            commande.getAdresseLivraison()
                    );
                    this.ajouterNotification(message);

                    this.updateDisponibilite();
                }
            }
        }
    }

    public void updatePosition(double latitude, double longitude) {
        this.positionActuelle = new Position(latitude, longitude);
        updateTempsEstimation();
    }

    private void updateTempsEstimation() {
        if (commandesALivrer.isEmpty()) {
            tempsEstimeProchaineLivraison = 0;
            return;
        }

        int tempsBase = 15; // 15 minutes de base

        // Ajustement selon le type de véhicule
        switch (vehicule.toLowerCase()) {
            case "velo": tempsBase *= 1.5; break;
            case "scooter": tempsBase *= 0.8; break;
            case "voiture": tempsBase *= 1.2; break;
        }

        // Ajustement selon le nombre de commandes
        tempsBase *= (1 + (commandesALivrer.size() - 1) * 0.3);

        tempsEstimeProchaineLivraison = tempsBase;
    }

    public void recevoirEvaluation(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        double totalPrecedent = noteMoyenne * nombreEvaluations;
        nombreEvaluations++;
        noteMoyenne = (totalPrecedent + note) / nombreEvaluations;
    }

    public void recevoirPourboire(double montant) {
        if (montant < 0) {
            throw new IllegalArgumentException("Le pourboire ne peut pas être négatif");
        }
        this.totalPourboires += montant;
    }

    public void commencerPause() {
        if (!commandesALivrer.isEmpty()) {
            throw new IllegalStateException("Impossible de prendre une pause avec des commandes en cours");
        }
        this.enPause = true;
        updateDisponibilite();
    }

    public void terminerPause() {
        this.enPause = false;
        updateDisponibilite();
    }

    private boolean estDisponiblePourNouvelleLivraison() {
        return disponible &&
                !enPause &&
                commandesALivrer.size() < getCapaciteMaxLivraisons();
    }

    // Getters (retournant des copies défensives pour les collections)
    public List<Commande> getCommandesALivrer() {
        return new ArrayList<>(commandesALivrer);
    }

    public List<Commande> getHistoriqueLivraisons() {
        return new ArrayList<>(historiqueLivraisons);
    }

    public boolean isDisponible() {
        return disponible;
    }

    public String getVehicule() {
        return vehicule;
    }

    public String getZone() {
        return zone;
    }

    public int getNombreLivraisonsEffectuees() {
        return nombreLivraisonsEffectuees;
    }

    public double getNoteMoyenne() {
        return noteMoyenne;
    }

    public Date getDerniereLivraison() {
        return derniereLivraison;
    }

    public Position getPositionActuelle() {
        return positionActuelle;
    }

    public double getTotalPourboires() {
        return totalPourboires;
    }

    public StatutLivreur getStatut() {
        return statut;
    }

    public boolean isEnPause() {
        return enPause;
    }

    public int getTempsEstimeProchaineLivraison() {
        return tempsEstimeProchaineLivraison;
    }

    @Override
    public String getRole() {
        return "LIVREUR";
    }

    @Override
    public String toString() {
        return "Livreur{" +
                "id='" + getId() + '\'' +
                ", nom='" + getNom() + '\'' +
                ", zone='" + zone + '\'' +
                ", statut=" + statut.getLibelle() +
                ", disponible=" + disponible +
                ", commandesEnCours=" + commandesALivrer.size() +
                '}';
    }
}