package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstraite de base pour toutes les vues de l'application.
 * Implémente les comportements communs et la gestion des notifications.
 */
public abstract class AbstractVue implements IVue, Observateur {
    protected IControleur controleur;
    protected Scanner scanner;
    protected List<String> notifications;

    public AbstractVue(IControleur controleur) {
        if (controleur == null) {
            throw new IllegalArgumentException("Le contrôleur ne peut pas être null");
        }
        this.controleur = controleur;
        this.scanner = new Scanner(System.in);
        this.notifications = new ArrayList<>();
    }

    @Override
    public abstract void afficher();

    @Override
    public void actualiser(Object source) {
        if (source != null) {
            notifications.add("Mise à jour reçue : " + source.toString());
        }
        this.afficher();
    }

    protected String lireEntree(String message) {
        System.out.print(message + " : ");
        return scanner.nextLine().trim();
    }

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

    protected void afficherSeparateur() {
        System.out.println("\n----------------------------------------");
    }

    protected void afficherErreur(String message) {
        System.out.println("\n⚠️ ERREUR : " + message);
    }

    protected void afficherSucces(String message) {
        System.out.println("\n✅ " + message);
    }

    protected void afficherInfo(String message) {
        System.out.println("\nℹ️ " + message);
    }

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

    protected void attendreTouche() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    protected void effacerEcran() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    protected void afficherMenuGeneral(String[] options) {
        System.out.println("\n=== Menu ===");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), options[i]);
        }
        System.out.println("=============");
    }

}