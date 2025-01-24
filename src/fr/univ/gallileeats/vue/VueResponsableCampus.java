package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueResponsable;
import fr.univ.gallileeats.model.*;
import java.util.List;

public class VueResponsableCampus extends AbstractVue implements IVueResponsable {
    private ResponsableCampus responsable;
    private static final String[] OPTIONS_MENU = {
            "Commander pour un événement",
            "Voir les commandes en cours",
            "Historique des commandes",
            "Gérer le budget",
            "Retour au menu principal"
    };

    public VueResponsableCampus(IControleur controleur, ResponsableCampus responsable) {
        super(controleur);
        if (responsable == null) {
            throw new IllegalArgumentException("Le responsable ne peut pas être null");
        }
        this.responsable = responsable;
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
        System.out.printf("║  Responsable Campus - %s%n", responsable.getNom());
        System.out.println("╚══════════════════════════════════════╝\n");

        System.out.println("🏢 Département : " + responsable.getDepartement());
        System.out.printf("💰 Budget disponible : %.2f€%n", responsable.getBudgetDisponible());
        System.out.printf("📊 Budget utilisé : %.2f€ (%.1f%%)%n",
                responsable.getBudgetUtilise(),
                responsable.getPourcentageBudgetUtilise());
        afficherSeparateur();
    }

    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("RESPONSABLE_" + choix);
    }

    public void afficherFormulaireCommandeEvenement() {
        System.out.println("\n=== Nouvelle Commande pour Événement ===");
        System.out.printf("💰 Budget disponible : %.2f€%n", responsable.getBudgetDisponible());
        afficherSeparateur();

        String evenement = lireEntree("Nom de l'événement");
        if (evenement.trim().isEmpty()) {
            afficherErreur("Le nom de l'événement est obligatoire");
            return;
        }

        int nombrePersonnes;
        try {
            nombrePersonnes = lireEntreeNumerique("Nombre de personnes", 1, 1000);
        } catch (NumberFormatException e) {
            afficherErreur("Nombre de personnes invalide");
            return;
        }

        controleur.traiterAction(String.format("RESPONSABLE_COMMANDE_%s_%d",
                evenement.replace(" ", "_"), nombrePersonnes));
    }

    public void afficherCommandesGroupees() {
        List<Commande> commandes = responsable.getCommandesGroupees();
        if (commandes.isEmpty()) {
            afficherInfo("Aucune commande groupée en cours.");
            return;
        }

        System.out.println("\n=== Commandes Groupées ===");
        for (Commande commande : commandes) {
            afficherCommandeGroupee(commande);
        }
    }

    private void afficherCommandeGroupee(Commande commande) {
        System.out.println("\n🔖 Commande n°" + commande.getNumeroCommande());
        System.out.println("🎉 Événement : " + commande.getEvenement());
        System.out.println("👥 Nombre de personnes : " + commande.getNombrePersonnes());
        System.out.println("🔄 État : " + commande.getEtat().getLibelle());
        System.out.printf("💰 Total : %.2f€%n", commande.getTotal());

        if (commande.getCommentaires() != null && !commande.getCommentaires().isEmpty()) {
            System.out.println("💬 Commentaires : " + commande.getCommentaires());
        }

        if (commande.getEtat() == EtatCommande.PRETE) {
            System.out.println("⚡ COMMANDE PRÊTE !");
        }
        afficherSeparateur();
    }

    public void afficherGestionBudget() {
        System.out.println("\n=== Gestion du Budget ===");
        System.out.printf("💰 Budget initial : %.2f€%n", responsable.getBudgetInitial());
        System.out.printf("📊 Budget utilisé : %.2f€%n", responsable.getBudgetUtilise());
        System.out.printf("💵 Budget restant : %.2f€%n", responsable.getBudgetDisponible());

        System.out.println("\n📈 Dépenses par événement :");
        responsable.getBudgetParEvenement().forEach((evenement, montant) ->
                System.out.printf("- %s : %.2f€%n", evenement, montant));

        afficherSeparateur();
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            String message = String.format(
                    "La commande pour l'événement '%s' est passée à l'état : %s",
                    commande.getEvenement(),
                    commande.getEtat().getLibelle()
            );
            notifications.add(message);
            afficher();
        }
    }
}