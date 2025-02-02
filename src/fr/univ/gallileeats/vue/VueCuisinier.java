package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueCuisinier;
import fr.univ.gallileeats.model.*;

import java.util.List;


/**
 * Vue dédiée au cuisinier permettant d'afficher et de gérer les commandes en cuisine.
 * Cette vue permet au cuisinier de voir les commandes en attente, en préparation,
 * ainsi que l'historique et les statistiques de son activité.
 */
public class VueCuisinier extends AbstractVue implements IVueCuisinier {
    private Cuisinier cuisinier;
    private static final String[] OPTIONS_MENU = {
            "Voir les commandes en attente",
            "Voir les commandes en préparation",
            "Historique des commandes",
            "Statistiques",
            "Retour au menu principal"
    };

    /**
     * Constructeur de VueCuisinier.
     *
     * @param controleur Le contrôleur associé à cette vue.
     * @param cuisinier  L'instance du cuisinier utilisant la vue.
     */
    public VueCuisinier(IControleur controleur, Cuisinier cuisinier) {
        super(controleur);
        this.cuisinier = cuisinier;
    }

    /**
     * Affiche la vue du cuisinier, y compris le menu et les notifications.
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
     * Affiche l'en-tête avec les informations du cuisinier.
     */
    private void afficherEntete() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║            CUISINE                    ║");
        System.out.printf("║      Cuisinier : %s          ║%n", cuisinier.getNom());
        System.out.println("╚═══════════════════════════════════════╝\n");
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
        controleur.traiterAction("CUISINIER_" + choix);
    }

    /**
     * Affiche la liste des commandes en attente de préparation.
     */
    @Override
    public void afficherCommandesEnAttente() {
        System.out.println("\n=== Commandes en Attente ===");
        List<Commande> commandes = cuisinier.getCommandesEnAttente();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande en attente");
            return;
        }

        for (Commande commande : commandes) {
            afficherDetailsCommande(commande);
        }
    }

    @Override
    public void afficherCommandesEnPreparation() {
        System.out.println("\n=== Commandes en Préparation ===");
        List<Commande> commandes = cuisinier.getCommandesEnPreparation();

        if (commandes.isEmpty()) {
            System.out.println("Aucune commande en préparation");
            return;
        }

        for (Commande commande : commandes) {
            afficherDetailsCommande(commande);
        }
    }

    /**
     * Affiche les détails d'une commande spécifique.
     *
     * @param commande La commande à afficher.
     */
    private void afficherDetailsCommande(Commande commande) {
        System.out.println("\n🔖 Commande n°" + commande.getNumeroCommande());
        System.out.println("📅 Date : " + commande.getDateCommande());
        System.out.println("🔄 État : " + commande.getEtat().getLibelle());

        // Détails du menu
        MenuComponent menu = commande.getMenu();
        System.out.println("\nDétails de la commande :");
        for (MenuComponent item : menu.getElements()) {
            System.out.printf("- %s%n", item.getNom());
        }

        // Afficher les options spéciales si présentes
        if (menu instanceof PlatDecore) {
            System.out.println("\nOptions :");
            PlatDecore platDecore = (PlatDecore) menu;
            afficherOptions(platDecore);
        }

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON) {
            System.out.println("🚚 Mode : Livraison");
        }

        System.out.println("----------------------------------------");
    }

    /**
     * Affiche les options spéciales associées à une commande.
     *
     * @param platDecore L'objet représentant un plat décoré contenant des options.
     */
    private void afficherOptions(PlatDecore platDecore) {
        if (platDecore instanceof OptionSupplement) {
            OptionSupplement option = (OptionSupplement) platDecore;
            System.out.printf("- %s (%s)%n",
                    option.getNomSupplement(),
                    option.getTypeSupplement().getLibelle());
        }

        if (platDecore.getPlatDeBase() instanceof PlatDecore) {
            afficherOptions((PlatDecore) platDecore.getPlatDeBase());
        }
    }

    /**
     * Met à jour l'affichage lorsqu'une commande change d'état.
     *
     * @param source L'objet source de l'événement (généralement une commande).
     */
    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            if (commande.getEtat() == EtatCommande.NOUVELLE) {
                String message = String.format(
                        "Nouvelle commande n°%s reçue !\nClient : %s\nMenu : %s",
                        commande.getNumeroCommande(),
                        commande.getClient().getNom(),
                        commande.getMenu().getNom()
                );
                notifications.add(message);
                afficher();
            }
        }
    }

    /**
     * Affiche l'historique des commandes préparées par le cuisinier.
     */
    @Override
    public void afficherHistoriqueCommandes() {
        System.out.println("\n=== Historique des Commandes ===");
        List<Commande> historique = cuisinier.getHistoriqueCommandes();

        if (historique.isEmpty()) {
            System.out.println("Aucune commande dans l'historique");
            return;
        }

        for (Commande commande : historique) {
            afficherDetailsCommande(commande);
        }
    }

    /**
     * Affiche les statistiques de performance du cuisinier.
     */

    @Override
    public void afficherStatistiques() {
        System.out.println("\n=== Statistiques ===");
        System.out.printf("Commandes préparées aujourd'hui : %d%n",
                cuisinier.getCommandesPrepareesDuJour());
        System.out.printf("Temps moyen de préparation : %d minutes%n",
                cuisinier.getTempsPreparationMoyen());
        System.out.printf("Taux de satisfaction : %.1f%%%n",
                cuisinier.getTauxSatisfaction());
    }
}