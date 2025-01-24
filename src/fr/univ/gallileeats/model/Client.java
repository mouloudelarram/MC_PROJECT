package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Client extends Utilisateur implements Observateur {
    private List<Commande> commandes;
    private String adresseLivraison;
    private boolean estEtudiant;
    private String numeroEtudiant;
    private double soldeIzly;
    private double soldePoints;
    private List<String> allergies;
    private List<String> preferencesAlimentaires;
    private boolean notificationsActivees;
    private ModeLivraison modeLivraisonPrefere;

    public enum ModeLivraison {
        LIVRAISON_DOMICILE("Livraison à domicile"),
        SUR_PLACE("Sur place"),
        A_EMPORTER("À emporter");

        private String libelle;

        ModeLivraison(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public Client(String id, String nom, String email, String motDePasse, String adresseLivraison) {
        super(id, nom, email, motDePasse);

        if (adresseLivraison == null || adresseLivraison.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de livraison est requise");
        }

        this.commandes = new ArrayList<>();
        this.adresseLivraison = adresseLivraison;
        this.soldeIzly = 0.0;
        this.soldePoints = 0.0;
        this.allergies = new ArrayList<>();
        this.preferencesAlimentaires = new ArrayList<>();
        this.notificationsActivees = true;
        this.modeLivraisonPrefere = ModeLivraison.LIVRAISON_DOMICILE;
    }

    // Gestion des commandes
    public void ajouterCommande(Commande commande) {
        if (commande == null) {
            throw new IllegalArgumentException("La commande ne peut pas être nulle");
        }
        commandes.add(commande);
        commande.ajouterObservateur(this);
        ajouterPoints(calculerPointsCommande(commande));
    }

    public void supprimerCommande(Commande commande) {
        commandes.remove(commande);
        commande.supprimerObservateur(this);
    }

    public List<Commande> getCommandes() {
        return new ArrayList<>(commandes);
    }

    public List<Commande> getCommandesEnCours() {
        List<Commande> commandesEnCours = new ArrayList<>();
        for (Commande commande : commandes) {
            if (!commande.getEtat().equals(EtatCommande.LIVREE) &&
                    !commande.getEtat().equals(EtatCommande.SERVIE) &&
                    !commande.getEtat().equals(EtatCommande.ANNULEE)) {
                commandesEnCours.add(commande);
            }
        }
        return commandesEnCours;
    }

    // Gestion du statut étudiant
    public void activerStatutEtudiant(String numeroEtudiant) {
        if (numeroEtudiant == null || numeroEtudiant.trim().isEmpty()) {
            throw new IllegalArgumentException("Numéro étudiant invalide");
        }
        this.estEtudiant = true;
        this.numeroEtudiant = numeroEtudiant;
    }

    public void desactiverStatutEtudiant() {
        this.estEtudiant = false;
        this.numeroEtudiant = null;
    }

    // Gestion du compte Izly
    public void rechargerSoldeIzly(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        this.soldeIzly += montant;
    }

    public boolean debiterSoldeIzly(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (soldeIzly >= montant) {
            soldeIzly -= montant;
            return true;
        }
        return false;
    }

    // Gestion des points de fidélité
    private double calculerPointsCommande(Commande commande) {
        return commande.getTotal() * 0.1; // 10% du montant en points
    }

    public void ajouterPoints(double points) {
        if (points > 0) {
            this.soldePoints += points;
        }
    }

    public boolean utiliserPoints(double points) {
        if (points > 0 && soldePoints >= points) {
            soldePoints -= points;
            return true;
        }
        return false;
    }

    // Gestion des allergies et préférences
    public void ajouterAllergie(String allergie) {
        if (allergie != null && !allergie.trim().isEmpty() && !allergies.contains(allergie)) {
            allergies.add(allergie);
        }
    }

    public void supprimerAllergie(String allergie) {
        allergies.remove(allergie);
    }

    public void ajouterPreferenceAlimentaire(String preference) {
        if (preference != null && !preference.trim().isEmpty() && !preferencesAlimentaires.contains(preference)) {
            preferencesAlimentaires.add(preference);
        }
    }

    public void supprimerPreferenceAlimentaire(String preference) {
        preferencesAlimentaires.remove(preference);
    }

    // Implémentation de l'Observer
    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande && notificationsActivees) {
            Commande commande = (Commande) source;
            if (commandes.contains(commande)) {
                String message = String.format(
                        "Votre commande %s est maintenant %s",
                        commande.getNumeroCommande(),
                        commande.getEtat().getLibelle()
                );
                ajouterNotification(message);
            }
        }
    }

    // Getters et Setters
    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public void setAdresseLivraison(String adresseLivraison) {
        if (adresseLivraison == null || adresseLivraison.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide");
        }
        this.adresseLivraison = adresseLivraison;
    }

    public boolean estEtudiant() {
        return estEtudiant;
    }

    public String getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public double getSoldeIzly() {
        return soldeIzly;
    }

    public double getSoldePoints() {
        return soldePoints;
    }

    public List<String> getAllergies() {
        return new ArrayList<>(allergies);
    }

    public List<String> getPreferencesAlimentaires() {
        return new ArrayList<>(preferencesAlimentaires);
    }

    public boolean isNotificationsActivees() {
        return notificationsActivees;
    }

    public void setNotificationsActivees(boolean notificationsActivees) {
        this.notificationsActivees = notificationsActivees;
    }

    public ModeLivraison getModeLivraisonPrefere() {
        return modeLivraisonPrefere;
    }

    public void setModeLivraisonPrefere(ModeLivraison modeLivraisonPrefere) {
        this.modeLivraisonPrefere = modeLivraisonPrefere;
    }

    @Override
    public String getRole() {
        return "CLIENT";
    }

    // Méthodes utilitaires
    public double getTotalDepense() {
        return commandes.stream()
                .filter(c -> !c.getEtat().equals(EtatCommande.ANNULEE))
                .mapToDouble(Commande::getTotal)
                .sum();
    }

    public boolean peutBeneficierReduction() {
        return soldePoints >= 100.0;
    }

    public boolean aCommandeEnCours() {
        return !getCommandesEnCours().isEmpty();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + getId() + '\'' +
                ", nom='" + getNom() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", adresse='" + adresseLivraison + '\'' +
                ", estEtudiant=" + estEtudiant +
                ", soldePoints=" + soldePoints +
                '}';
    }
}