package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une vue dans l'architecture MVC.
 * Cette interface définit les méthodes essentielles pour l'affichage
 * et l'actualisation des vues de l'application.
 */
public interface IVue {

    /**
     * Affiche la vue à l'utilisateur.
     */
    void afficher();

    /**
     * Actualise la vue en fonction des changements survenus dans le modèle.
     *
     * @param source l'objet source qui a déclenché l'actualisation.
     */
    void actualiser(Object source);
}
