package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueClient;
import fr.univ.gallileeats.model.*;
import java.util.List;

public class VueClient extends AbstractVue implements IVueClient {
    private Client client;
    private static final String[] OPTIONS_MENU = {
            "Commander un repas",
            "Voir mes commandes en cours",
            "Historique des commandes",
            "Gérer mon profil",
            "Retour au menu principal"
    };

    private static final String[] OPTIONS_PROFIL = {
            "Modifier informations personnelles",
            "Gérer préférences alimentaires",
            "Gérer allergies",
            "Retour"
    };

    private static final String[] OPTIONS_COMMANDES = {
            "Modifier une commande",
            "Annuler une commande",
            "Suivre une commande",
            "Retour"
    };

    public VueClient(IControleur controleur, Client client) {
        super(controleur);
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }
        this.client = client;
    }

    @Override
    public void afficher() {
        effacerEcran();
        afficherNotifications();
        afficherEntete();
        afficherMenu();
        traiterChoix();
    }

    private void afficherEntete() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.printf("  Menu Client - %s%n", client.getNom());
        System.out.println("╚══════════════════════════════════════╝\n");

        if (client.estEtudiant()) {
            System.out.println("🎓 Statut : Étudiant");
            System.out.printf("💳 Solde IZLY : %.2f€%n", client.getSoldeIzly());
        }
        System.out.printf("🎯 Points fidélité : %.2f points%n", client.getSoldePoints());

        // Afficher les commandes en cours s'il y en a
        List<Commande> commandesEnCours = client.getCommandesEnCours();
        if (!commandesEnCours.isEmpty()) {
            System.out.printf("\n📦 Commandes en cours : %d%n", commandesEnCours.size());
        }

        afficherSeparateur();
    }

    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("CLIENT_" + choix);
    }

    @Override
    public void afficherCommandes() {
        List<Commande> commandes = client.getCommandesEnCours();
        if (commandes.isEmpty()) {
            afficherInfo("Vous n'avez aucune commande en cours.");
            return;
        }

        System.out.println("\n=== Vos commandes en cours ===");
        for (int i = 0; i < commandes.size(); i++) {
            System.out.println("\n" + (i + 1) + ".");
            afficherCommande(commandes.get(i));
        }

        System.out.println("\nActions disponibles :");
        for (int i = 0; i < OPTIONS_COMMANDES.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_COMMANDES[i]);
        }
    }

    public void afficherFormulaireModificationCommande(Commande commande) {
        System.out.println("\n=== Modifier la commande n°" + commande.getNumeroCommande() + " ===");
        System.out.println("1. Modifier le mode de livraison");
        System.out.println("2. Modifier l'adresse de livraison");
        System.out.println("3. Ajouter des options supplémentaires");
        System.out.println("4. Retour");
    }

    public void afficherFormulairePaiement() {
        System.out.println("\n=== Choix du mode de paiement ===");
        System.out.println("1. Carte bancaire");
        if (client.estEtudiant()) {
            System.out.println("2. IZLY");
            System.out.printf("   Solde disponible : %.2f€%n", client.getSoldeIzly());
        }
        System.out.println("3. Espèces");
    }

    private void afficherCommande(Commande commande) {
        System.out.println("📦 Commande n°" + commande.getNumeroCommande());
        System.out.println("📅 Date : " + commande.getDateCommande());
        System.out.println("🔄 État : " + commande.getEtat().getLibelle());
        System.out.printf("💰 Total : %.2f€%n", commande.getTotal());
        System.out.println("🚚 Mode : " + commande.getModeLivraison().getLibelle());

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
            System.out.println("📍 Adresse : " + commande.getAdresseLivraison());

            if (commande.getLivreur() != null) {
                Livreur livreur = commande.getLivreur();
                System.out.println("👤 Livreur : " + livreur.getNom());
                if (livreur.getTelephone() != null) {
                    System.out.println("📱 Téléphone : " + livreur.getTelephone());
                }
            }
        }

        if (commande.getCommentaires() != null && !commande.getCommentaires().isEmpty()) {
            System.out.println("💬 Commentaires : " + commande.getCommentaires());
        }

        // Afficher le menu commandé
        MenuComponent menu = commande.getMenu();
        System.out.println("\nDétail de la commande :");
        menu.getElements().forEach(element ->
                System.out.printf("- %s (%.2f€)%n", element.getNom(), element.getPrix())
        );

        afficherSeparateur();
    }

    @Override
    public void afficherEtatProfil() {
        System.out.println("\n=== Gérer mon profil ===");
        for (int i = 0; i < OPTIONS_PROFIL.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_PROFIL[i]);
        }

        System.out.println("\nInformations actuelles :");
        System.out.println("👤 Nom : " + client.getNom());
        System.out.println("📧 Email : " + client.getEmail());
        System.out.println("📍 Adresse de livraison : " + client.getAdresseLivraison());
        System.out.println("📱 Téléphone : " + (client.getTelephone() != null ? client.getTelephone() : "Non renseigné"));
        System.out.println("🎓 Statut étudiant : " + (client.estEtudiant() ? "Oui" : "Non"));

        if (!client.getAllergies().isEmpty()) {
            System.out.println("\n⚠️ Allergies déclarées :");
            client.getAllergies().forEach(allergie -> System.out.println("- " + allergie));
        }

        if (!client.getPreferencesAlimentaires().isEmpty()) {
            System.out.println("\n🍽️ Préférences alimentaires :");
            client.getPreferencesAlimentaires().forEach(pref -> System.out.println("- " + pref));
        }

        afficherSeparateur();
    }

    public void afficherHistoriqueCommandes() {
        List<Commande> commandes = client.getCommandes();
        if (commandes.isEmpty()) {
            afficherInfo("Vous n'avez aucune commande dans l'historique.");
            return;
        }

        System.out.println("\n=== Historique des commandes ===");
        commandes.forEach(this::afficherCommande);

        System.out.println("\nStatistiques :");
        System.out.printf("💰 Total dépensé : %.2f€%n", client.getTotalDepense());
        System.out.printf("🎯 Points fidélité : %.2f points%n", client.getSoldePoints());
        afficherSeparateur();
    }

    public void afficherRecapitulatifCommande(Commande commande) {
        System.out.println("\n=== Récapitulatif de la commande ===");
        System.out.println("Menu : " + commande.getMenu().getNom());
        commande.getMenu().getElements().forEach(element ->
                System.out.printf("- %s (%.2f€)%n", element.getNom(), element.getPrix())
        );

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
            System.out.println("\n📍 Livraison à : " + commande.getAdresseLivraison());
        }

        // Afficher les réductions si applicable
        if (client.estEtudiant()) {
            System.out.println("🎓 Réduction étudiant appliquée (-15%)");
        }

        System.out.printf("\n💰 Total à payer : %.2f€%n", commande.getTotal());
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            String message = String.format(
                    "Votre commande n°%s est passée à l'état : %s",
                    commande.getNumeroCommande(),
                    commande.getEtat().getLibelle()
            );

            if (commande.getEtat() == EtatCommande.EN_LIVRAISON && commande.getLivreur() != null) {
                message += String.format("\n🚚 Livreur assigné : %s", commande.getLivreur().getNom());
            }

            notifications.add(message);
            afficher();
        }
    }
}