package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;

/**
 * Vue principale de l'application, affiche le menu de connexion
 */
public class VuePrincipale extends AbstractVue {
    private static final String[] OPTIONS_MENU = {
            "Connexion Client",
            "Connexion Livreur",
            "Connexion Responsable Campus",
            "Connexion Administrateur",
            "Connexion Cuisinier",
            "Quitter"
    };

    public VuePrincipale(IControleur controleur) {
        super(controleur);
    }

    @Override
    public void afficher() {
        effacerEcran();
        afficherEntete();
        afficherMenu();
        traiterChoix();
    }

    private void afficherEntete() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║           GALILEE EATS v1.0            ║");
        System.out.println("║      Restaurant Universitaire          ║");
        System.out.println("╚════════════════════════════════════════╝");
    }


    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("MENU_PRINCIPAL_" + choix);
    }

    @Override
    public void actualiser(Object source) {
        // La vue principale n'a pas besoin d'être actualisée
        // car elle sert uniquement de point d'entrée
    }
}