// Observateur.java
package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant un observateur dans le modèle de conception Observer.
 * Un observateur est notifié des changements survenus dans un objet observé.
 */
public interface Observateur {

    /**
     * Méthode appelée pour mettre à jour l'état de l'observateur lorsque l'objet observé change.
     *
     * @param source l'objet source qui a déclenché l'actualisation.
     */
    void actualiser(Object source);
}
