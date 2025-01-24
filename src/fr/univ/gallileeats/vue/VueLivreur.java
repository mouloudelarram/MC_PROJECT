package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueLivreur;
import fr.univ.gallileeats.model.*;
import java.util.List;

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

    public VueLivreur(IControleur controleur, Livreur livreur) {
        super(controleur);
        if (livreur == null) {
            throw new IllegalArgumentException("Le livreur ne peut pas être null");
        }
        this.livreur = livreur;
    }

    @Override
    public void afficher() {
        effacerEcran();
        afficherNotifications();
        afficherStatusLivreur();
        afficherMenu();
        traiterChoix();
    }

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

    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("LIVREUR_" + choix);
    }

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

    public void afficherFormulaireLivraison() {
        List<Commande> commandes = livreur.getCommandesALivrer();
        afficherCommandesALivrer();
        if (!commandes.isEmpty()) {
            System.out.println("\nEntrez le numéro de la commande à marquer comme livrée (0 pour annuler)");
        }
    }

    public void afficherConfirmationLivraison(Commande commande) {
        System.out.println("\n=== Confirmation de livraison ===");
        afficherDetailsCommande(commande);
        System.out.println("\n1. Livraison réussie");
        System.out.println("2. Problème de livraison");
        System.out.println("3. Annuler");
    }

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

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            String message = "La commande n°" + commande.getNumeroCommande() +
                    " est passée à l'état: " + commande.getEtat().getLibelle();

            if (commande.getEtat() == EtatCommande.PRETE) {
                message += "\n⚡ À récupérer au restaurant !";
                message += "\n👤 Client : " + commande.getClient().getNom();
                message += "\n📍 Adresse : " + commande.getAdresseLivraison();
            }

            notifications.add(message);
            afficher();
        }
    }
}