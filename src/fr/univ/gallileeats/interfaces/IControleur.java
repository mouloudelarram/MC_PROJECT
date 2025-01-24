package fr.univ.gallileeats.interfaces;

public interface IControleur {
    void traiterAction(String action);
    void setVue(IVue vue);
    IVue getVue();
    void afficherVuePrincipale();
    void gererCommandes();
    void afficherStatistiques();
    void afficherEtatProfil();
    void afficherFormulairePaiement();
    void retourMenuPrincipal();
}