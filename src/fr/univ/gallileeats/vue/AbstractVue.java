package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.*;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstraite de base pour toutes les vues de l'application.
 * Elle implémente les comportements communs et la gestion des notifications.
 */
public abstract class AbstractVue implements IVue, Observateur {
    /**
     * Contrôleur associé à la vue
     */
    protected IControleur controleur;
    /**
     * Scanner pour la saisie utilisateur
     */
    protected Scanner scanner;
    /**
     * Liste des notifications reçues
     */
    protected List<String> notifications;

    /**
     * Constructeur de la classe AbstractVue.
     *
     * @param controleur Le contrôleur associé à la vue.
     * @throws IllegalArgumentException si le contrôleur est null.
     */
    public AbstractVue(IControleur controleur) {
        if (controleur == null) {
            throw new IllegalArgumentException("Le contrôleur ne peut pas être null");
        }
        this.controleur = controleur;
        this.scanner = new Scanner(System.in);
        this.notifications = new ArrayList<>();
    }

    /**
     * Méthode abstraite pour afficher la vue.
     */
    @Override
    public abstract void afficher();

    /**
     * Met à jour la vue lorsqu'une notification est reçue.
     *
     * @param source Objet source de la mise à jour.
     */
    @Override
    public void actualiser(Object source) {
        if (source != null) {
            notifications.add("Mise à jour reçue : " + source.toString());
        }
        this.afficher();
    }

    /**
     * Lit une entrée utilisateur sous forme de chaîne de caractères.
     *
     * @param message Message affiché avant la saisie.
     * @return Entrée utilisateur sans espaces inutiles.
     */
    protected String lireEntree(String message) {
        System.out.print(message + " : ");
        return scanner.nextLine().trim();
    }

    /**
     * Lit une entrée utilisateur sous forme de nombre entier dans une plage donnée.
     *
     * @param message Message affiché avant la saisie.
     * @param min     Valeur minimale autorisée.
     * @param max     Valeur maximale autorisée.
     * @return Nombre entier validé.
     */
    protected int lireEntreeNumerique(String message, int min, int max) {
        while (true) {
            try {
                System.out.print(message + " [" + min + "-" + max + "] : ");
                int valeur = Integer.parseInt(scanner.nextLine().trim());
                if (valeur >= min && valeur <= max) {
                    return valeur;
                }
                System.out.println("⚠️ Veuillez entrer un nombre entre " + min + " et " + max);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Veuillez entrer un nombre valide");
            }
        }
    }

    /**
     * Lit une entrée utilisateur sous forme de nombre décimal supérieur à une valeur minimale.
     *
     * @param message Message affiché avant la saisie.
     * @param min     Valeur minimale autorisée.
     * @return Nombre décimal validé.
     */
    protected double lireEntreeDouble(String message, double min) {
        while (true) {
            try {
                System.out.print(message + " (min. " + min + ") : ");
                double valeur = Double.parseDouble(scanner.nextLine().trim());
                if (valeur >= min) {
                    return valeur;
                }
                System.out.println("⚠️ La valeur doit être supérieure ou égale à " + min);
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Veuillez entrer un nombre valide");
            }
        }
    }

    /**
     * Affiche les notifications reçues et les efface après affichage.
     */
    protected void afficherNotifications() {
        if (!notifications.isEmpty()) {
            System.out.println("\n=== Notifications ===");
            for (String notification : notifications) {
                System.out.println("🔔 " + notification);
            }
            notifications.clear();
            System.out.println("===================\n");
        }
    }

    /**
     * Affiche un séparateur visuel dans la console.
     */
    protected void afficherSeparateur() {
        System.out.println("\n----------------------------------------");
    }

    /**
     * Affiche un message d'erreur formaté.
     *
     * @param message Message d'erreur.
     */
    protected void afficherErreur(String message) {
        System.out.println("\n⚠️ ERREUR : " + message);
    }

    /**
     * Affiche un message de succès formaté.
     *
     * @param message Message de succès.
     */
    protected void afficherSucces(String message) {
        System.out.println("\n✅ " + message);
    }

    /**
     * Affiche un message d'information formaté.
     *
     * @param message Message d'information.
     */
    protected void afficherInfo(String message) {
        System.out.println("\nℹ️ " + message);
    }

    /**
     * Demande une confirmation utilisateur sous forme de oui/non.
     *
     * @param message Message de confirmation.
     * @return true si l'utilisateur répond "oui", false sinon.
     */
    protected boolean confirmerAction(String message) {
        while (true) {
            System.out.print("\n" + message + " (oui/non) : ");
            String reponse = scanner.nextLine().trim().toLowerCase();
            if (reponse.equals("oui")) {
                return true;
            } else if (reponse.equals("non")) {
                return false;
            }
            System.out.println("⚠️ Veuillez répondre par 'oui' ou 'non'");
        }
    }

    /**
     * Attend que l'utilisateur appuie sur Entrée avant de continuer.
     */
    protected void attendreTouche() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Efface l'écran de la console.
     */
    protected void effacerEcran() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}