package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant un contrôleur dans l'architecture MVC.
 * Cette interface définit les méthodes nécessaires pour gérer les interactions
 * entre la vue et le modèle.
 */
public interface IControleur {

    /**
     * Traite une action spécifique en fonction de son type.
     *
     * @param action la chaîne représentant l'action à traiter.
     */
    void traiterAction(String action);

    /**
     * Définit la vue associée au contrôleur.
     *
     * @param vue l'instance de la vue à associer.
     */
    void setVue(IVue vue);

    /**
     * Retourne la vue actuellement associée au contrôleur.
     *
     * @return l'instance de la vue.
     */
    IVue getVue();

    /**
     * Affiche la vue principale de l'application.
     */
    void afficherVuePrincipale();

    /**
     * Gère les commandes effectuées par les utilisateurs.
     */
    void gererCommandes();

    /**
     * Affiche les statistiques pertinentes de l'application.
     */
    void afficherStatistiques();

    /**
     * Affiche l'état du profil utilisateur.
     */
    void afficherEtatProfil();

    /**
     * Affiche le formulaire de paiement pour effectuer une transaction.
     */
    void afficherFormulairePaiement();

    /**
     * Retourne à l'écran du menu principal.
     */
    void retourMenuPrincipal();
}
