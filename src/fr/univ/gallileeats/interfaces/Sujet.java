// Sujet.java
package fr.univ.gallileeats.interfaces;

import fr.univ.gallileeats.model.EtatCommande;

/**
 * Interface représentant un sujet dans le modèle de conception Observer.
 * Un sujet gère une liste d'observateurs et les notifie en cas de changement d'état.
 */
public interface Sujet {

    /**
     * Change l'état du sujet et notifie les observateurs.
     *
     * @param nouvelEtat le nouvel état de la commande.
     */
    void changerEtat(EtatCommande nouvelEtat);

    /**
     * Ajoute un observateur à la liste des observateurs du sujet.
     *
     * @param o l'observateur à ajouter.
     */
    void ajouterObservateur(Observateur o);

    /**
     * Supprime un observateur de la liste des observateurs du sujet.
     *
     * @param o l'observateur à supprimer.
     */
    void supprimerObservateur(Observateur o);

    /**
     * Notifie tous les observateurs des changements d'état du sujet.
     */
    void notifierObservateurs();
}
