package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.interfaces.StrategyPaiement;
import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;
import fr.univ.gallileeats.strategie.*;
import fr.univ.gallileeats.GalileeEats;
import java.util.List;
import java.util.function.Consumer;
import java.util.Scanner;

public class ControleurClient extends AbstractControleur {
    private ControleurPrincipal controleurPrincipal;
    private Scanner scanner;
    private Commande commandeEnCours;

    public ControleurClient(ControleurPrincipal controleurPrincipal) {
        super();
        this.controleurPrincipal = controleurPrincipal;
        this.scanner = new Scanner(System.in);
    }

    @Override
    protected void initialiserActionHandlers() {
        actionHandlers.put("1", params -> creerNouvelleCommande());
        actionHandlers.put("2", params -> gererCommandes());
        actionHandlers.put("3", params -> afficherHistorique());
        actionHandlers.put("4", params -> gererProfil());
        actionHandlers.put("5", params -> retourMenuPrincipal());

        // Sous-menus pour la gestion des commandes
        actionHandlers.put("CMD_1", params -> modifierCommande());
        actionHandlers.put("CMD_2", params -> annulerCommande());
        actionHandlers.put("CMD_3", params -> suivreCommande());

        // Sous-menus pour la gestion du profil
        actionHandlers.put("PROFIL_1", params -> modifierInformationsPersonnelles());
        actionHandlers.put("PROFIL_2", params -> gererPreferences());
        actionHandlers.put("PROFIL_3", params -> gererAllergies());
        actionHandlers.put("PROFIL_4", params -> rechargerSoldeIzly());


    }

    @Override
    public void traiterAction(String action) {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        verifierUtilisateurConnecte(client, "CLIENT");

        if (action.startsWith("CLIENT_")) {
            action = action.substring(7);
        }

        Consumer<String[]> handler = actionHandlers.get(action);
        if (handler != null) {
            try {
                handler.accept(new String[]{});
            } catch (Exception e) {
                System.out.println("\n⚠️ Erreur : " + e.getMessage());
                attendreTouche();
                vue.afficher();
            }
        } else {
            System.out.println("Action non reconnue : " + action);
            vue.afficher();
        }
    }

    private void creerNouvelleCommande() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");

        try {
            // Affichage des menus disponibles
            List<Menu> menus = GalileeEats.getMenusDisponibles();
            System.out.println("\n=== Menus Disponibles ===");
            for (int i = 0; i < menus.size(); i++) {
                Menu menu = menus.get(i);
                System.out.printf("%d. %s (%.2f€)%n", (i + 1), menu.getNom(), menu.getPrix());
                System.out.println("   " + menu.getDescription());
                menu.getElements().forEach(element ->
                        System.out.println("   - " + element.getNom() +
                                String.format(" (%.2f€)", element.getPrix())));
                System.out.println();
            }

            int choixMenu = lireEntier("Choisissez un menu", 1, menus.size());
            Menu menuChoisi = menus.get(choixMenu - 1);

            // Choix du mode de livraison
            System.out.println("\nMode de livraison :");
            System.out.println("1. Livraison à domicile");
            System.out.println("2. Sur place");
            System.out.println("3. À emporter");

            int choixLivraison = lireEntier("Votre choix", 1, 3);
            Commande.ModeLivraison modeLivraison = Commande.ModeLivraison.values()[choixLivraison - 1];

            // Création de la commande
            commandeEnCours = new Commande(client, menuChoisi, 1, modeLivraison);

            if (modeLivraison == Commande.ModeLivraison.LIVRAISON) {
                System.out.println("\nAdresse de livraison actuelle : " + client.getAdresseLivraison());
                if (confirmerAction("Voulez-vous utiliser une autre adresse ?")) {
                    System.out.print("Nouvelle adresse : ");
                    String nouvelleAdresse = scanner.nextLine();
                    commandeEnCours.setAdresseLivraison(nouvelleAdresse);
                }
            }

            // Options supplémentaires
            ajouterOptionsSupplementaires(menuChoisi);

            // Affichage du récapitulatif
            afficherRecapitulatifCommande();

            if (confirmerAction("Confirmer la commande ?")) {
                // Procéder au paiement
                afficherFormulairePaiement();

                if (commandeEnCours.estPayee()) {
                    client.ajouterCommande(commandeEnCours);
                    System.out.println("\n✅ Commande créée avec succès !");
                    System.out.println("Numéro de commande : " + commandeEnCours.getNumeroCommande());
                    System.out.printf("Total : %.2f€%n", commandeEnCours.getTotal());
                }
            }

        } catch (Exception e) {
            System.out.println("\n⚠️ Erreur : " + e.getMessage());
        }

        attendreTouche();
        vue.afficher();
    }

    private void ajouterOptionsSupplementaires(Menu menu) {
        while (confirmerAction("\nVoulez-vous ajouter des options supplémentaires ?")) {
            System.out.println("\nOptions disponibles :");
            System.out.println("1. Ingrédient supplémentaire");
            System.out.println("2. Sauce supplémentaire");
            System.out.println("3. Portion supplémentaire");

            int choix = lireEntier("Votre choix", 1, 3);

            switch (choix) {
                case 1:
                    ajouterIngredientSupplementaire();
                    break;
                    /*
                case 2:
                    ajouterSauceSupplementaire();
                    break;
                case 3:
                    ajouterPortionSupplementaire();
                    break;
                     */
            }
        }
    }

    private void ajouterIngredientSupplementaire() {
        // Exemple d'implémentation
        System.out.println("1. Fromage (+1.00€)");
        System.out.println("2. Bacon (+1.50€)");
        System.out.println("3. Œuf (+1.00€)");

        int choix = lireEntier("Choisissez un ingrédient", 1, 3);
        // Implémentation de l'ajout...
    }

    @Override
    public void gererCommandes() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        List<Commande> commandes = client.getCommandesEnCours();

        System.out.println("\n=== Gestion des Commandes ===");
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande en cours.");
            attendreTouche();
            vue.afficher();
            return;
        }

        for (int i = 0; i < commandes.size(); i++) {
            Commande commande = commandes.get(i);
            System.out.printf("\n%d. Commande #%s%n", (i + 1), commande.getNumeroCommande());
            System.out.println("État : " + commande.getEtat().getLibelle());
            System.out.printf("Total : %.2f€%n", commande.getTotal());
        }

        System.out.println("\nActions disponibles :");
        System.out.println("1. Modifier une commande");
        System.out.println("2. Annuler une commande");
        System.out.println("3. Suivre une commande");
        System.out.println("4. Retour");

        int choix = lireEntier("Votre choix", 1, 4);
        traiterAction("CMD_" + choix);
    }

    @Override
    public void afficherFormulairePaiement() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");

        ((VueClient)vue).afficherFormulairePaiement();

        int choix = lireEntier("Votre choix", 1, 3);

        try {
            switch (choix) {
                case 1:
                    traiterPaiementCarte();
                    break;
                case 2:
                    if (client.estEtudiant()) {
                        traiterPaiementIzly();
                    }
                    break;
                case 3:
                    traiterPaiementEspeces();
                    break;
            }
        } catch (Exception e) {
            System.out.println("\n⚠️ Erreur de paiement : " + e.getMessage());
        }
    }

    private void traiterPaiementCarte() {
        System.out.print("Numéro de carte : ");
        String numeroCarte = scanner.nextLine();

        System.out.print("Date d'expiration (MM/YY) : ");
        String dateExpiration = scanner.nextLine();

        System.out.print("CVV : ");
        String cvv = scanner.nextLine();

        StrategyPaiement strategie = new PaiementCarteBancaire(numeroCarte, dateExpiration, cvv);
        commandeEnCours.setStrategyPaiement(strategie);
        commandeEnCours.payer();
    }

    private void traiterPaiementIzly() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");

        if (client.getSoldeIzly() < commandeEnCours.getTotal()) {
            throw new IllegalStateException("Solde IZLY insuffisant");
        }

        System.out.print("Code PIN IZLY : ");
        String pin = scanner.nextLine();

        StrategyPaiement strategie = new PaiementIzly(client.getNumeroEtudiant(), pin);
        commandeEnCours.setStrategyPaiement(strategie);
        commandeEnCours.payer();
        client.debiterSoldeIzly(commandeEnCours.getTotal());
    }

    private void traiterPaiementEspeces() {
        System.out.printf("Montant à payer : %.2f€%n", commandeEnCours.getTotal());
        double montant = lireDouble("Montant fourni", commandeEnCours.getTotal());

        StrategyPaiement strategie = new PaiementEspeces(montant);
        commandeEnCours.setStrategyPaiement(strategie);
        commandeEnCours.payer();
    }

    private void afficherRecapitulatifCommande() {
        System.out.println("\n=== Récapitulatif de la commande ===");
        System.out.println("Menu : " + commandeEnCours.getMenu().getNom());
        commandeEnCours.getMenu().getElements().forEach(element ->
                System.out.println("- " + element.getNom() +
                        String.format(" (%.2f€)", element.getPrix())));

        if (commandeEnCours.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
            System.out.println("Livraison à : " + commandeEnCours.getAdresseLivraison());
        }

        System.out.printf("Total à payer : %.2f€%n", commandeEnCours.getTotal());
    }

    private int lireEntier(String message, int min, int max) {
        while (true) {
            System.out.printf("%s (%d-%d) : ", message, min, max);
            try {
                int valeur = Integer.parseInt(scanner.nextLine());
                if (valeur >= min && valeur <= max) {
                    return valeur;
                }
                System.out.println("⚠️ Valeur hors limites");
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Veuillez entrer un nombre valide");
            }
        }
    }

    private double lireDouble(String message, double min) {
        while (true) {
            System.out.printf("%s (min. %.2f€) : ", message, min);
            try {
                double valeur = Double.parseDouble(scanner.nextLine());
                if (valeur >= min) {
                    return valeur;
                }
                System.out.println("⚠️ Montant insuffisant");
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Veuillez entrer un montant valide");
            }
        }
    }

    private boolean confirmerAction(String message) {
        System.out.print(message + " (oui/non) : ");
        return scanner.nextLine().trim().equalsIgnoreCase("oui");
    }

    // Méthodes de gestion du profil
    private void gererProfil() {
        System.out.println("\n=== Gestion du Profil ===");
        System.out.println("1. Modifier informations personnelles");
        System.out.println("2. Gérer préférences alimentaires");
        System.out.println("3. Gérer allergies");
        System.out.println("4. Recharger le solde IZLY");
        System.out.println("5. Retour");

        int choix = lireEntier("Votre choix", 1, 5);
        if (choix < 5) {
            traiterAction("PROFIL_" + choix);
        } else {
            vue.afficher();
        }
    }

    private void modifierInformationsPersonnelles() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        System.out.println("\n=== Modifier Informations Personnelles ===");
        System.out.println("Informations actuelles :");
        System.out.println("1. Nom : " + client.getNom());
        System.out.println("2. Email : " + client.getEmail());
        System.out.println("3. Adresse : " + client.getAdresseLivraison());
        System.out.println("4. Téléphone : " + (client.getTelephone() != null ? client.getTelephone() : "Non renseigné"));
        System.out.println("5. Retour");

        int choix = lireEntier("Que souhaitez-vous modifier", 1, 5);
        if (choix == 5) return;

        try {
            switch (choix) {
                case 1:
                    System.out.print("Nouveau nom : ");
                    client.setNom(scanner.nextLine());
                    break;
                case 2:
                    System.out.print("Nouvel email : ");
                    client.setEmail(scanner.nextLine());
                    break;
                case 3:
                    System.out.print("Nouvelle adresse : ");
                    client.setAdresseLivraison(scanner.nextLine());
                    break;
                case 4:
                    System.out.print("Nouveau téléphone : ");
                    client.setTelephone(scanner.nextLine());
                    break;
            }
            System.out.println("✅ Modification effectuée avec succès");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        }
        attendreTouche();
        gererProfil();
    }

    private void gererPreferences() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        System.out.println("\n=== Gérer Préférences Alimentaires ===");

        List<String> preferences = client.getPreferencesAlimentaires();
        if (!preferences.isEmpty()) {
            System.out.println("\nPréférences actuelles :");
            for (int i = 0; i < preferences.size(); i++) {
                System.out.println((i + 1) + ". " + preferences.get(i));
            }
        }

        System.out.println("\n1. Ajouter une préférence");
        System.out.println("2. Supprimer une préférence");
        System.out.println("3. Retour");

        int choix = lireEntier("Votre choix", 1, 3);
        switch (choix) {
            case 1:
                System.out.print("Nouvelle préférence : ");
                client.ajouterPreferenceAlimentaire(scanner.nextLine());
                System.out.println("✅ Préférence ajoutée");
                break;
            case 2:
                if (!preferences.isEmpty()) {
                    int index = lireEntier("Numéro de la préférence à supprimer", 1, preferences.size()) - 1;
                    client.supprimerPreferenceAlimentaire(preferences.get(index));
                    System.out.println("✅ Préférence supprimée");
                }
                break;
        }
        attendreTouche();
        gererProfil();
    }

    private void gererAllergies() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        System.out.println("\n=== Gérer Allergies ===");

        List<String> allergies = client.getAllergies();
        if (!allergies.isEmpty()) {
            System.out.println("\nAllergies déclarées :");
            for (int i = 0; i < allergies.size(); i++) {
                System.out.println((i + 1) + ". " + allergies.get(i));
            }
        }

        System.out.println("\n1. Ajouter une allergie");
        System.out.println("2. Supprimer une allergie");
        System.out.println("3. Retour");

        int choix = lireEntier("Votre choix", 1, 3);
        switch (choix) {
            case 1:
                System.out.print("Nouvelle allergie : ");
                client.ajouterAllergie(scanner.nextLine());
                System.out.println("✅ Allergie ajoutée");
                break;
            case 2:
                if (!allergies.isEmpty()) {
                    int index = lireEntier("Numéro de l'allergie à supprimer", 1, allergies.size()) - 1;
                    client.supprimerAllergie(allergies.get(index));
                    System.out.println("✅ Allergie supprimée");
                }
                break;
        }
        attendreTouche();
        gererProfil();
    }

    private void rechargerSoldeIzly() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        System.out.println("\n=== Recharger Solde IZLY ===");

        System.out.print("Montant à recharger : ");
        double montant = lireDouble("Montant", 0.0);

        try {
            // afficherFormulairePaiement();
            client.rechargerSoldeIzly(montant);
            System.out.println("✅ Solde rechargé avec succès");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        }
        attendreTouche();
        gererProfil();
    }

    private void afficherHistorique() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        List<Commande> commandes = client.getCommandes();

        if (commandes.isEmpty()) {
            System.out.println("\nAucune commande dans l'historique.");
            attendreTouche();
            vue.afficher();
            return;
        }

        System.out.println("\n=== Historique des Commandes ===");
        for (Commande commande : commandes) {
            System.out.println("\n📦 Commande #" + commande.getNumeroCommande());
            System.out.println("📅 Date : " + commande.getDateCommande());
            System.out.println("🔄 État : " + commande.getEtat().getLibelle());
            System.out.printf("💰 Total : %.2f€%n", commande.getTotal());
            System.out.println("----------------------------------------");
        }

        System.out.println("\nStatistiques :");
        System.out.printf("Total dépensé : %.2f€%n", client.getTotalDepense());
        System.out.printf("Points fidélité : %.2f points%n", client.getSoldePoints());

        attendreTouche();
        vue.afficher();
    }

    private void modifierCommande() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        List<Commande> commandes = client.getCommandesEnCours();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande modifiable.");
            return;
        }

        int index = lireEntier("Numéro de la commande à modifier", 1, commandes.size()) - 1;
        Commande commande = commandes.get(index);

        if (commande.getEtat() != EtatCommande.NOUVELLE) {
            System.out.println("⚠️ Cette commande ne peut plus être modifiée");
            return;
        }

        // Options de modification
        System.out.println("\n1. Modifier le mode de livraison");
        System.out.println("2. Modifier l'adresse");
        System.out.println("3. Retour");

        int choix = lireEntier("Votre choix", 1, 3);
        switch (choix) {
            case 1:
                modifierModeLivraison(commande);
                break;
            case 2:
                modifierAdresseLivraison(commande);
                break;
        }
    }

    private void modifierModeLivraison(Commande commande) {
        System.out.println("\nMode de livraison actuel : " + commande.getModeLivraison().getLibelle());
        System.out.println("1. Livraison à domicile");
        System.out.println("2. Sur place");
        System.out.println("3. À emporter");

        // Implementation de la modification
    }

    private void modifierAdresseLivraison(Commande commande) {
        if (commande.getModeLivraison() != Commande.ModeLivraison.LIVRAISON) {
            System.out.println("⚠️ Cette commande n'est pas en livraison");
            return;
        }

        System.out.println("Adresse actuelle : " + commande.getAdresseLivraison());
        System.out.print("Nouvelle adresse : ");
        String nouvelleAdresse = scanner.nextLine();
        commande.setAdresseLivraison(nouvelleAdresse);
        System.out.println("✅ Adresse modifiée");
    }

    private void annulerCommande() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        List<Commande> commandes = client.getCommandesEnCours();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande à annuler.");
            return;
        }

        int index = lireEntier("Numéro de la commande à annuler", 1, commandes.size()) - 1;
        Commande commande = commandes.get(index);

        try {
            if (confirmerAction("Êtes-vous sûr de vouloir annuler cette commande ?")) {
                commande.changerEtat(EtatCommande.ANNULEE);
                System.out.println("✅ Commande annulée");
            }
        } catch (IllegalStateException e) {
            System.out.println("⚠️ " + e.getMessage());
        }
    }

    private void suivreCommande() {
        Client client = (Client) controleurPrincipal.getUtilisateurConnecte("CLIENT");
        List<Commande> commandes = client.getCommandesEnCours();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande en cours.");
            return;
        }

        int index = lireEntier("Numéro de la commande à suivre", 1, commandes.size()) - 1;
        Commande commande = commandes.get(index);

        System.out.println("\n=== Suivi de commande #" + commande.getNumeroCommande() + " ===");
        System.out.println("État actuel : " + commande.getEtat().getLibelle());
        System.out.println("\nHistorique :");
        commande.getHistorique().forEach(System.out::println);

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON &&
                commande.getLivreur() != null) {
            System.out.println("\nInformations livreur :");
            System.out.println("Nom : " + commande.getLivreur().getNom());
            System.out.println("Téléphone : " + commande.getLivreur().getTelephone());
        }

        attendreTouche();
        vue.afficher();
    }

    @Override
    public void retourMenuPrincipal() {
        controleurPrincipal.afficherVuePrincipale();
    }
}