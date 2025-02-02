// Sujet.java
package fr.univ.gallileeats.interfaces;

import fr.univ.gallileeats.model.EtatCommande;

public interface Sujet {
    // Gestion des états
    void changerEtat(EtatCommande nouvelEtat);

    void ajouterObservateur(Observateur o);
    void supprimerObservateur(Observateur o);
    void notifierObservateurs();
}