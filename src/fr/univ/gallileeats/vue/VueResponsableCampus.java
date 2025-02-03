package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueResponsable;
import fr.univ.gallileeats.model.*;

import java.util.List;

/**
 * Vue dédiée au responsable de campus permettant de gérer les commandes groupées pour des événements,
 * d'afficher les commandes en cours, de consulter l'historique et de gérer le budget disponible.
 */
public class VueResponsableCampus extends AbstractVue implements IVueResponsable {
    private ResponsableCampus responsable;
    private static final String[] OPTIONS_MENU = {
            "Commander pour un événement",
            "Voir les commandes en cours",
            "Historique des commandes",
            "Gérer le budget",
            "Retour au menu principal"
    };

    /**
     * Constructeur de VueResponsableCampus.
     *
     * @param controleur  Le contrôleur associé à cette vue.
     * @param responsable L'instance du responsable de campus utilisant la vue.
     * @throws IllegalArgumentException si le responsable est null.
     */
    public VueResponsableCampus(IControleur controleur, ResponsableCampus responsable) {
        super(controleur);
        if (responsable == null) {
            throw new IllegalArgumentException("Le responsable ne peut pas être null");
        }
        this.responsable = responsable;
    }

    /**
     * Affiche la vue du responsable de campus, y compris le menu et les notifications.
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
     * Affiche l'en-tête avec les informations du responsable de campus.
     */
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
        controleur.traiterAction("RESPONSABLE_" + choix);
    }

    /**
     * Affiche un formulaire pour commander un repas pour un événement.
     */
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

    /**
     * Affiche la liste des commandes groupées en cours.
     */
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

    /**
     * Affiche les détails d'une commande groupée spécifique.
     * @param commande La commande à afficher.
     */
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

    /**
     * Affiche les informations sur la gestion du budget du responsable de campus.
     */
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

    /**
     * Met à jour l'affichage lorsqu'une commande change d'état.
     * @param source L'objet source de l'événement (généralement une commande).
     */
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