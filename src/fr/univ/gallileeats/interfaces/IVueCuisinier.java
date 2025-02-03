package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une vue dédiée aux cuisiniers dans l'architecture MVC.
 * Cette interface étend {@link IVue} et ajoute des fonctionnalités spécifiques
 * pour la gestion et le suivi des commandes en cuisine.
 */
public interface IVueCuisinier extends IVue {

    /**
     * Affiche la liste des commandes en attente.
     * Permet au cuisinier de voir les commandes qui doivent être préparées.
     */
    void afficherCommandesEnAttente();

    /**
     * Affiche la liste des commandes en cours de préparation.
     * Permet au cuisinier de suivre les commandes en cours de traitement.
     */
    void afficherCommandesEnPreparation();

    /**
     * Affiche l'historique des commandes terminées.
     * Permet au cuisinier de consulter les commandes déjà préparées et livrées.
     */
    void afficherHistoriqueCommandes();

    /**
     * Affiche les statistiques liées aux commandes et à la cuisine.
     * Peut inclure des informations sur le nombre de commandes traitées, le temps moyen de préparation, etc.
     */
    void afficherStatistiques();
}
