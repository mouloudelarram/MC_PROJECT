package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueLivreur;
import fr.univ.gallileeats.model.*;
import java.util.List;

/**
 * Vue dédiée aux livreurs, leur permettant de gerer des commandes à livrer,
 */
public class VueLivreur extends AbstractVue implements IVueLivreur {
    private Livreur livreur;
    private static final String[] OPTIONS_MENU = {
            "Voir les commandes à livrer",
            "Marquer une commande comme livrée",
            "Voir l'historique des livraisons",
            "Gérer ma disponibilité",
            "Voir mes statistiques",
            "Retour au menu principal"
    };

    /**
     * Constructeur de VueLivreur.
     * @param controleur Le contrôleur associé à cette vue.
     * @param livreur L'instance du livreur utilisant la vue.
     * @throws IllegalArgumentException si le livreur est null.
     */

    public VueLivreur(IControleur controleur, Livreur livreur) {
        super(controleur);
        if (livreur == null) {
            throw new IllegalArgumentException("Le livreur ne peut pas être null");
        }
        this.livreur = livreur;
    }

    /**
     * Affiche la vue du livreur, y compris le menu et les notifications.
     */
    @Override
    public void afficher() {
        effacerEcran();
        afficherNotifications();
        afficherStatusLivreur();
        afficherMenu();
        traiterChoix();
    }

    /**
     * Affiche les informations du livreur, y compris son statut, zone, et véhicule.
     */
    private void afficherStatusLivreur() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.printf("║     Livreur - %s%n", livreur.getNom());
        System.out.println("╚══════════════════════════════════════╝\n");
        System.out.println("🚩 Zone : " + livreur.getZone());
        System.out.println("🚗 Véhicule : " + livreur.getVehicule());
        System.out.println("🔄 Status : " + (livreur.isDisponible() ? "✅ Disponible" : "❌ Occupé"));
        System.out.println("📦 Commandes en cours : " + livreur.getCommandesALivrer().size());

        if (livreur.getNoteMoyenne() > 0) {
            System.out.printf("⭐ Note moyenne : %.1f/5%n", livreur.getNoteMoyenne());
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
     * Gère l'entrée utilisateur pour sélectionner une action dans le menu.
     */
    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("LIVREUR_" + choix);
    }

    /**
     * Affiche la liste des commandes disponibles à livrer.
     */
    public void afficherCommandesALivrer() {
        List<Commande> commandes = livreur.getCommandesALivrer();
        if (commandes.isEmpty()) {
            afficherInfo("Aucune commande à livrer pour le moment.");
            return;
        }

        System.out.println("\n=== Commandes à livrer ===");
        for (int i = 0; i < commandes.size(); i++) {
            System.out.println("\n" + (i + 1) + ".");
            afficherDetailsCommande(commandes.get(i));
        }
    }


    /**
     * Affiche les détails d'une commande spécifique.
     * @param commande La commande à afficher.
     */
    private void afficherDetailsCommande(Commande commande) {
        System.out.println("🔖 N° " + commande.getNumeroCommande());
        System.out.println("👤 Client : " + commande.getClient().getNom());
        System.out.println("📍 Adresse : " + commande.getAdresseLivraison());
        System.out.println("🔄 État : " + commande.getEtat().getLibelle());
        System.out.printf("💰 Total : %.2f€%n", commande.getTotal());

        if (commande.getCommentaires() != null && !commande.getCommentaires().isEmpty()) {
            System.out.println("💬 Commentaires : " + commande.getCommentaires());
        }

        if (commande.getEtat() == EtatCommande.PRETE) {
            System.out.println("⚡ PRÊTE À ÊTRE LIVRÉE !");
        }
        afficherSeparateur();
    }

    /**
     * Affiche le formulaire permettant de sélectionner une commande à livrer.
     */

    public void afficherFormulaireLivraison() {
        List<Commande> commandes = livreur.getCommandesALivrer();
        if (commandes.isEmpty()) {
            System.out.println("Aucune commande à livrer.");
            return;
        }

        System.out.println("\nCommandes disponibles pour livraison :");
        for (int i = 0; i < commandes.size(); i++) {
            System.out.printf("\n%d. Commande #%s%n", (i + 1), commandes.get(i).getNumeroCommande());
            afficherDetailsCommande(commandes.get(i));
        }

        System.out.println("\nEntrez le numéro de l'option (1-" + commandes.size() + "), 0 pour annuler :");
    }

    /**
     * Affiche la confirmation de livraison d'une commande.
     * @param commande La commande dont la livraison est à confirmer.
     */
    public void afficherConfirmationLivraison(Commande commande) {
        System.out.println("\n=== Confirmation de livraison ===");
        afficherDetailsCommande(commande);
        System.out.println("\n1. Livraison réussie");
        System.out.println("2. Problème de livraison");
        System.out.println("3. Annuler");
    }

    /**
     * Affiche les statistiques du livreur, telles que le nombre de livraisons effectuées et la note moyenne.
     */
    public void afficherStatistiques() {
        System.out.println("\n=== Mes Statistiques ===");
        System.out.println("📊 Nombre total de livraisons : " + livreur.getNombreLivraisonsEffectuees());
        System.out.printf("⭐ Note moyenne : %.1f/5%n", livreur.getNoteMoyenne());
        System.out.printf("💰 Total des pourboires : %.2f€%n", livreur.getTotalPourboires());

        System.out.println("\n📍 Statistiques de la zone " + livreur.getZone() + " :");
        System.out.println("⏱️ Temps moyen de livraison : " +
                livreur.getTempsEstimeProchaineLivraison() + " minutes");

        if (livreur.getDerniereLivraison() != null) {
            System.out.println("🕒 Dernière livraison : " + livreur.getDerniereLivraison());
        }
        afficherSeparateur();
    }

    /**
     * Met à jour l'affichage lorsqu'une commande change d'état.
     * @param source L'objet source de l'événement (généralement une commande).
     */
    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            // Vérifier que la commande est prête et en mode livraison
            if (commande.getEtat() == EtatCommande.PRETE &&
                    commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
                String message = String.format(
                        "Nouvelle commande disponible n°%s\nClient : %s\nAdresse : %s",
                        commande.getNumeroCommande(),
                        commande.getClient().getNom(),
                        commande.getAdresseLivraison()
                );
                notifications.add(message);
                afficher();
            }
        }
    }
}