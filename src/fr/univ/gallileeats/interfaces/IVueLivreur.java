package fr.univ.gallileeats.interfaces;

import fr.univ.gallileeats.model.Commande;

public interface IVueLivreur extends IVue {
    void afficherCommandesALivrer();
    void afficherFormulaireLivraison();
    void afficherConfirmationLivraison(Commande commande);
    void afficherStatistiques();
}