package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une vue dédiée aux clients dans l'architecture MVC.
 * Cette interface étend {@link IVue} et ajoute des fonctionnalités spécifiques
 * pour la gestion des commandes et l'affichage du profil utilisateur.
 */
public interface IVueClient extends IVue {

    /**
     * Affiche la liste des commandes passées par le client.
     * Permet au client de consulter l'historique et le statut de ses commandes.
     */
    void afficherCommandes();

    /**
     * Affiche l'état du profil du client.
     * Permet au client de voir et modifier ses informations personnelles.
     */
    void afficherEtatProfil();
}
