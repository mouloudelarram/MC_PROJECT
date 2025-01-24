//Utilisateur.java
package fr.univ.gallileeats.model;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public abstract class Utilisateur {
    protected String id;
    protected String nom;
    protected String email;
    protected String motDePasse;
    protected Date dateInscription;
    protected Date dernierConnexion;
    protected boolean estActif;
    protected List<String> notifications;
    protected String telephone;
    protected List<String> preferences;
    protected String langue;

    public Utilisateur(String id, String nom, String email, String motDePasse) {
        if (id == null || nom == null || email == null || motDePasse == null) {
            throw new IllegalArgumentException("Tous les champs sont obligatoires");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }

        this.id = id;
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateInscription = new Date();
        this.dernierConnexion = new Date();
        this.estActif = true;
        this.notifications = new ArrayList<>();
        this.preferences = new ArrayList<>();
        this.langue = "FR";
    }

    // Méthodes abstraites
    public abstract String getRole();

    // Méthodes de gestion des préférences
    public void ajouterPreference(String preference) {
        if (!preferences.contains(preference)) {
            preferences.add(preference);
        }
    }

    public void supprimerPreference(String preference) {
        preferences.remove(preference);
    }

    // Méthodes de gestion des notifications
    public void ajouterNotification(String notification) {
        notifications.add(notification);
    }

    public List<String> getNotificationsNonLues() {
        List<String> nonLues = new ArrayList<>(notifications);
        notifications.clear(); // Marquer comme lues
        return nonLues;
    }

    // Méthodes de connexion/déconnexion
    public void seConnecter() {
        this.dernierConnexion = new Date();
        this.estActif = true;
    }

    public void seDeconnecter() {
        this.estActif = false;
    }

    // Méthodes de validation
    public boolean verifierMotDePasse(String motDePasse) {
        return this.motDePasse.equals(motDePasse);
    }

    public void changerMotDePasse(String ancienMotDePasse, String nouveauMotDePasse) {
        if (!verifierMotDePasse(ancienMotDePasse)) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }
        if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères");
        }
        this.motDePasse = nouveauMotDePasse;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide");
        }
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Format d'email invalide");
        }
        this.email = email;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public Date getDernierConnexion() {
        return dernierConnexion;
    }

    public boolean isEstActif() {
        return estActif;
    }

    public void setEstActif(boolean estActif) {
        this.estActif = estActif;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        if (telephone != null && !telephone.matches("^\\+?[0-9]{10,}$")) {
            throw new IllegalArgumentException("Format de téléphone invalide");
        }
        this.telephone = telephone;
    }

    public List<String> getPreferences() {
        return new ArrayList<>(preferences);
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        if (langue == null || langue.trim().isEmpty()) {
            throw new IllegalArgumentException("La langue ne peut pas être vide");
        }
        this.langue = langue.toUpperCase();
    }

    // Méthodes utilitaires
    public boolean aDesNotifications() {
        return !notifications.isEmpty();
    }

    public boolean estConnecte() {
        return estActif;
    }

    public long getDureeDepuisDerniereConnexion() {
        return new Date().getTime() - dernierConnexion.getTime();
    }

    @Override
    public String toString() {
        return String.format("%s{id='%s', nom='%s', email='%s', role='%s'}",
                getClass().getSimpleName(), id, nom, email, getRole());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}