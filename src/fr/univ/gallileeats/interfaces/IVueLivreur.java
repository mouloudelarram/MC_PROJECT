package fr.univ.gallileeats.interfaces;

import fr.univ.gallileeats.model.Commande;

/**
 * Interface représentant une vue dédiée aux livreurs dans l'architecture MVC.
 * Cette interface étend {@link IVue} et ajoute des fonctionnalités spécifiques
 * pour la gestion et le suivi des livraisons.
 */
public interface IVueLivreur extends IVue {

    /**
     * Affiche la liste des commandes à livrer.
     * Permet au livreur de voir les commandes qui doivent être prises en charge.
     */
    void afficherCommandesALivrer();

    /**
     * Affiche le formulaire permettant au livreur d'enregistrer une livraison.
     * Permet de saisir des informations comme l'heure de livraison et le statut.
     */
    void afficherFormulaireLivraison();

    /**
     * Affiche une confirmation de la livraison d'une commande spécifique.
     *
     * @param commande l'objet représentant la commande qui a été livrée.
     */
    void afficherConfirmationLivraison(Commande commande);

    /**
     * Affiche les statistiques liées aux livraisons effectuées par le livreur.
     * Peut inclure le nombre de livraisons, le temps moyen de livraison, etc.
     */
    void afficherStatistiques();
}