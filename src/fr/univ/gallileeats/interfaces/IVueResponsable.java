package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une vue dédiée aux responsables dans l'architecture MVC.
 * Cette interface étend {@link IVue} et ajoute des fonctionnalités spécifiques
 * pour la gestion des commandes d'événements et du budget.
 */
public interface IVueResponsable extends IVue {

    /**
     * Affiche le formulaire permettant de passer une commande pour un événement.
     * Permet au responsable de commander des repas pour des événements spéciaux.
     */
    void afficherFormulaireCommandeEvenement();

    /**
     * Affiche la liste des commandes groupées.
     * Permet au responsable de suivre et gérer les commandes effectuées en groupe.
     */
    void afficherCommandesGroupees();

    /**
     * Affiche l'interface de gestion du budget.
     * Permet au responsable de gérer les finances et de suivre les dépenses liées aux commandes.
     */
    void afficherGestionBudget();
}
