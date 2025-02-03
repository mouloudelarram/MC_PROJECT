package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une vue dédiée aux administrateurs dans l'architecture MVC.
 * Cette interface étend {@link IVue} et ajoute des fonctionnalités spécifiques
 * pour la gestion des menus et des utilisateurs.
 */
public interface IVueAdmin extends IVue {

    /**
     * Affiche l'interface de gestion du menu.
     * Permet à l'administrateur de gérer les plats et les catégories.
     */
    void afficherGestionMenu();

    /**
     * Affiche l'interface de gestion des utilisateurs.
     * Permet à l'administrateur de gérer les comptes et leurs privilèges.
     */
    void afficherGestionUtilisateurs();

    /**
     * Affiche le formulaire d'ajout d'un nouveau plat.
     * Permet à l'administrateur d'ajouter un plat au menu.
     */
    void afficherFormulaireAjoutPlat();
}
