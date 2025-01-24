// Sujet.java
package fr.univ.gallileeats.interfaces;

public interface Sujet {
    void ajouterObservateur(Observateur o);
    void supprimerObservateur(Observateur o);
    void notifierObservateurs();
}