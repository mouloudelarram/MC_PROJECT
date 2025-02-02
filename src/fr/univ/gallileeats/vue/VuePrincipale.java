package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;

/**
 * Vue principale de l'application, affiche le menu de connexion.
 * Cette vue permet aux utilisateurs de choisir leur mode de connexion.
 */
public class VuePrincipale extends AbstractVue {
    /**
     * Options disponibles dans le menu principal.
     */
    private static final String[] OPTIONS_MENU = {
            "Connexion Client",
            "Connexion Livreur",
            "Connexion Responsable Campus",
            "Connexion Administrateur",
            "Connexion Cuisinier",
            "Quitter"
    };

    /**
     * Constructeur de la VuePrincipale.
     *
     * @param controleur Le contrôleur associé à cette vue.
     */
    public VuePrincipale(IControleur controleur) {
        super(controleur);
    }

    /**
     * Affiche l'écran principal avec l'entête, le menu et gère la sélection de l'utilisateur.
     */
    @Override
    public void afficher() {
        effacerEcran();
        afficherEntete();
        afficherMenu();
        traiterChoix();
    }

    /**
     * Affiche l'entête de l'application avec le nom et la version.
     */
    private void afficherEntete() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║           GALILEE EATS v1.0            ║");
        System.out.println("║      Restaurant Universitaire          ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Affiche le menu principal avec les options disponibles.
     */
    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    /**
     * Gère le choix de l'utilisateur et transmet l'action au contrôleur.
     */
    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("MENU_PRINCIPAL_" + choix);
    }

    /**
     * Cette méthode est implémentée pour respecter l'interface mais n'est pas utilisée,
     * car la vue principale ne nécessite pas d'être actualisée.
     *
     * @param source Objet source de l'événement (non utilisé).
     */
    @Override
    public void actualiser(Object source) {
        // La vue principale n'a pas besoin d'être actualisée
        // car elle sert uniquement de point d'entrée
    }
}
