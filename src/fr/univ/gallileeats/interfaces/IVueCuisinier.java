package fr.univ.gallileeats.interfaces;

public interface IVueCuisinier extends IVue {
    void afficherCommandesEnAttente();
    void afficherCommandesEnPreparation();
    void afficherHistoriqueCommandes();
    void afficherStatistiques();
}