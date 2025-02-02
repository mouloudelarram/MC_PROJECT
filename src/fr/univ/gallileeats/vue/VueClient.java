package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueClient;
import fr.univ.gallileeats.model.*;

import java.util.ArrayList;
import java.util.List;


/**
 * Vue dédiée aux clients, leur permettant de passer des commandes,
 * gérer leurs profils et suivre l'historique de leurs commandes.
 */
public class VueClient extends AbstractVue implements IVueClient {
    /**
     * Le client associé à cette vue.
     */
    private Client client;

    /**
     * Options disponibles dans le menu principal.
     */
    private static final String[] OPTIONS_MENU = {
            "Commander un repas",
            "Voir mes commandes en cours",
            "Historique des commandes",
            "Gérer mon profil",
            "Retour au menu principal"
    };

    /**
     * Options disponibles dans la gestion du profil.
     */
    private static final String[] OPTIONS_PROFIL = {
            "Modifier informations personnelles",
            "Gérer préférences alimentaires",
            "Gérer allergies",
            "Retour"
    };

    /**
     * Options disponibles dans la gestion des commandes.
     */
    private static final String[] OPTIONS_COMMANDES = {
            "Modifier une commande",
            "Annuler une commande",
            "Suivre une commande",
            "Retour"
    };

    /**
     * Constructeur de la VueClient.
     * @param controleur Le contrôleur associé à cette vue.
     * @param client L'instance du client utilisant la vue.
     * @throws IllegalArgumentException si le client est null.
     */

    public VueClient(IControleur controleur, Client client) {
        super(controleur);
        if (client == null) {
            throw new IllegalArgumentException("Le client ne peut pas être null");
        }
        this.client = client;
    }

    /**
     * Affiche la vue du client, y compris le menu et les notifications.
     */
    @Override
    public void afficher() {
        effacerEcran();
        afficherNotifications();
        afficherEntete();
        afficherMenu();
        traiterChoix();
    }

    /**
     * Affiche l'entête contenant les informations du client.
     */
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


    /**
     * Affiche le menu principal avec les options disponibles.
     */
    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    /**
     * Gère l'entrée de l'utilisateur et exécute l'action correspondante.
     */
    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("CLIENT_" + choix);
    }

    /**
     * Affiche les commandes en cours du client.
     */
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

    /**
     * Affiche un formulaire de modification pour une commande donnée.
     * @param commande La commande à modifier.
     */
    public void afficherFormulaireModificationCommande(Commande commande) {
        System.out.println("\n=== Modifier la commande n°" + commande.getNumeroCommande() + " ===");
        System.out.println("1. Modifier le mode de livraison");
        System.out.println("2. Modifier l'adresse de livraison");
        System.out.println("3. Ajouter des options supplémentaires");
        System.out.println("4. Retour");
    }

    /**
     * Affiche le formulaire de sélection du mode de paiement.
     */
    public void afficherFormulairePaiement() {
        System.out.println("\n=== Choix du mode de paiement ===");
        System.out.println("1. Carte bancaire");
        if (client.estEtudiant()) {
            System.out.println("2. IZLY");
            System.out.printf("   Solde disponible : %.2f€%n", client.getSoldeIzly());
        }
        System.out.println("3. Espèces");
    }

    /**
     * Affiche les détails d'une commande spécifique.
     * @param commande La commande à afficher.
     */
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

    /**
     * Affiche et gère les options pour modifier le profil du client.
     */
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

    /**
     * Affiche l'historique des commandes du client.
     */
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

    /**
     * Affiche un récapitulatif détaillé de la commande avant validation.
     * @param commande La commande concernée.
     */
    public void afficherRecapitulatifCommande(Commande commande) {
        System.out.println("\n=== Récapitulatif de la commande ===");
        System.out.println("Menu : " + commande.getMenu().getNom());

        // Affichage des éléments de base du menu
        System.out.println("\nÉléments du menu :");
        commande.getMenu().getElements().forEach(element ->
                System.out.printf("- %s (%.2f€)%n", element.getNom(), element.getPrix())
        );

        // Affichage des suppléments si présents
        afficherSupplementsRecap(commande.getMenu());

        // Affichage des réductions
        if (!commande.getReductionsAppliquees().isEmpty()) {
            System.out.println("\nRéductions appliquées :");
            System.out.printf("Total avant réductions : %.2f€%n", commande.getTotalAvantReductions());
            commande.getReductionsAppliquees().forEach(reduction ->
                    System.out.println("- " + reduction)
            );
        }

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
            System.out.println("\n📍 Livraison à : " + commande.getAdresseLivraison());
        }

        System.out.printf("\n💰 Total à payer : %.2f€%n", commande.getTotal());
    }

    /**
     * Affiche les suppléments ajoutés à un menu dans une commande.
     * @param menu Le menu concerné.
     */
    private void afficherSupplementsRecap(MenuComponent menu) {
        if (menu instanceof PlatDecore) {
            PlatDecore platDecore = (PlatDecore) menu;
            List<OptionSupplement> supplements = new ArrayList<>();
            MenuComponent current = menu;

            while (current instanceof PlatDecore) {
                if (current instanceof OptionSupplement) {
                    supplements.add((OptionSupplement) current);
                }
                current = ((PlatDecore) current).getPlatDeBase();
            }

            if (!supplements.isEmpty()) {
                System.out.println("\nSuppléments ajoutés :");
                supplements.forEach(supplement ->
                        System.out.printf("- %s (+%.2f€)%n",
                                supplement.getNomSupplement(),
                                supplement.getPrixSupplementaire())
                );
            }
        }
    }


    /**
     * Gère la mise à jour de l'affichage lorsqu'une commande change d'état.
     * @param source L'objet source de l'événement (généralement une commande).
     */
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