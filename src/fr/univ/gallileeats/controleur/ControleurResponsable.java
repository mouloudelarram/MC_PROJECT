package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;
import java.util.*;
import java.util.function.Consumer;

public class ControleurResponsable extends AbstractControleur {
    private ControleurPrincipal controleurPrincipal;
    private Map<String, Double> budgetParEvenement;

    public ControleurResponsable(ControleurPrincipal controleurPrincipal) {
        super();
        this.controleurPrincipal = controleurPrincipal;
        this.budgetParEvenement = new HashMap<>();
    }

    @Override
    protected void initialiserActionHandlers() {
        actionHandlers.put("1", params -> creerCommandeEvenement());
        actionHandlers.put("2", params -> gererCommandes());
        actionHandlers.put("3", params -> afficherHistorique());
        actionHandlers.put("4", params -> gererBudget());
        actionHandlers.put("5", params -> retourMenuPrincipal());
    }

    @Override
    public void traiterAction(String action) {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        verifierUtilisateurConnecte(responsable, "RESPONSABLE");

        if (action.startsWith("RESPONSABLE_")) {
            action = action.substring(12);
        }

        if (action.startsWith("COMMANDE_")) {
            traiterCommandeEvenement(action.substring(9).split("_"));
            // return;
        }

        if (action.startsWith("BUDGET_")) {
            traiterGestionBudget(action.substring(7).split("_"));
            return;
        }

        Consumer<String[]> handler = actionHandlers.get(action);
        if (handler != null) {
            handler.accept(new String[]{});
        } else {
            System.out.println("Action non reconnue : " + action);
            attendreTouche();
            vue.afficher();
        }
    }

    @Override
    public void gererCommandes() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        ((VueResponsableCampus)vue).afficherCommandesGroupees();
        attendreTouche();
        vue.afficher();
    }

    @Override
    public void afficherStatistiques() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        System.out.println("\n=== Statistiques du département ===");
        System.out.println("🏢 Département: " + responsable.getDepartement());
        System.out.printf("💰 Budget total: %.2f€%n", responsable.getBudgetInitial());
        System.out.printf("💸 Budget utilisé: %.2f€ (%.1f%%)%n",
                responsable.getBudgetUtilise(),
                responsable.getPourcentageBudgetUtilise());
        System.out.printf("💵 Budget restant: %.2f€%n", responsable.getBudgetDisponible());
        afficherStatistiquesEvenements();
        attendreTouche();        
        vue.afficher();
    }

    @Override
    public void afficherEtatProfil() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        System.out.println("\n=== Profil Responsable Campus ===");
        System.out.println("👤 Nom: " + responsable.getNom());
        System.out.println("📧 Email: " + responsable.getEmail());
        System.out.println("🏢 Département: " + responsable.getDepartement());
        System.out.printf("💰 Budget géré: %.2f€%n", responsable.getBudgetInitial());
        attendreTouche();
        vue.afficher();
    }

    @Override
    public void afficherFormulairePaiement() {
        // Non utilisé pour le responsable campus
    }

    @Override
    public void retourMenuPrincipal() {
        controleurPrincipal.afficherVuePrincipale();
    }

    private void creerCommandeEvenement() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        ((VueResponsableCampus)vue).afficherFormulaireCommandeEvenement();
        attendreTouche();
        vue.afficher();
    }

    private void traiterCommandeEvenement(String[] params) {
        if (params.length < 2) return;

        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        String evenement = params[0].replace("_", " ");
        int nombrePersonnes;

        try {
            nombrePersonnes = Integer.parseInt(params[1]);
            MenuBuffet menuBuffet = creerMenuBuffet(evenement, nombrePersonnes);

            double coutTotal = menuBuffet.getPrix() * nombrePersonnes;
            if (coutTotal > responsable.getBudgetDisponible()) {
                System.out.printf("⚠️ Budget insuffisant (Requis: %.2f€, Disponible: %.2f€)%n",
                        coutTotal, responsable.getBudgetDisponible());
        
                attendreTouche();
                vue.afficher();
                //return;
            }

            responsable.creerCommandeGroupee(menuBuffet, nombrePersonnes, evenement);
            budgetParEvenement.put(evenement, coutTotal);

            System.out.println("\n✅ Commande créée avec succès!");
            System.out.printf("Total: %.2f€%n", coutTotal);

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("⚠️ Erreur: " + e.getMessage());
        }

        attendreTouche();
        vue.afficher();
    }

    private MenuBuffet creerMenuBuffet(String nomEvenement, int nombrePersonnes) {
        MenuBuffet menu = new MenuBuffet(
                "Buffet " + nomEvenement,
                "Menu buffet pour l'événement " + nomEvenement,
                nombrePersonnes
        );

        menu.ajouter(new Plat(
                "Assortiment d'Entrées Froides",
                "Variété de salades et charcuteries",
                12.0,
                "ENTREE"
        ));

        menu.ajouter(new Plat(
                "Entrées Chaudes",
                "Quiches et feuilletés variés",
                15.0,
                "ENTREE"
        ));

        menu.ajouter(new Plat(
                "Plats Chauds",
                "Assortiment de viandes et poissons",
                25.0,
                "PLAT"
        ));

        menu.ajouter(new Plat(
                "Options Végétariennes",
                "Plats végétariens variés",
                20.0,
                "PLAT"
        ));

        menu.ajouter(new Plat(
                "Buffet de Desserts",
                "Assortiment de pâtisseries",
                10.0,
                "DESSERT"
        ));

        return menu;
    }

    private void afficherHistorique() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        List<Commande> commandes = responsable.getCommandesGroupees();

        if (commandes.isEmpty()) {
            System.out.println("\nAucune commande dans l'historique.");
        } else {
            System.out.println("\n=== Historique des commandes ===");
            commandes.forEach(this::afficherDetailsCommande);
        }
        attendreTouche();
        vue.afficher();
    }

    private void afficherDetailsCommande(Commande commande) {
        System.out.println("\n🔖 Commande n°" + commande.getNumeroCommande());
        System.out.println("🎉 Événement: " + commande.getEvenement());
        System.out.println("👥 Nombre de personnes: " + commande.getNombrePersonnes());
        System.out.println("📅 Date: " + commande.getDateCommande());
        System.out.println("🔄 État: " + commande.getEtat().getLibelle());
        System.out.printf("💰 Total: %.2f€%n", commande.getTotal());

        if (commande.getCommentaires() != null && !commande.getCommentaires().isEmpty()) {
            System.out.println("💬 Commentaires: " + commande.getCommentaires());
        }
        System.out.println("----------------------------------------");
    }

    private void gererBudget() {
        System.out.println("\n=== Gestion du Budget ===");
        System.out.println("1. Voir les dépenses par événement");
        System.out.println("2. Demander une augmentation de budget");
        System.out.println("3. Voir le budget restant");
        System.out.println("4. Retour");

        Scanner scanner = new Scanner(System.in);
        String choix = scanner.nextLine();

        switch(choix) {
            case "1":
                afficherDepensesParEvenement();
                break;
            case "2":
                demanderAugmentationBudget();
                break;
            case "3":
                afficherBudgetRestant();
                break;
            case "4":
                vue.afficher();
                break;
        }
    }

    private void traiterGestionBudget(String[] params) {
        if (params.length < 1) return;

        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");

        if ("AUGMENTATION".equals(params[0]) && params.length >= 3) {
            double montant = Double.parseDouble(params[1]);
            String justification = params[2].replace("_", " ");
            responsable.demanderAugmentationBudget(montant, justification);
            System.out.println("✅ Demande d'augmentation envoyée");
        }

        vue.afficher();
    }

    private void afficherDepensesParEvenement() {
        if (budgetParEvenement.isEmpty()) {
            System.out.println("\nAucune dépense enregistrée.");
            return;
        }

        System.out.println("\n=== Dépenses par Événement ===");
        budgetParEvenement.forEach((evenement, montant) ->
                System.out.printf("%s : %.2f€%n", evenement, montant));
    }

    private void afficherStatistiquesEvenements() {
        if (!budgetParEvenement.isEmpty()) {
            System.out.println("\n📊 Statistiques des événements:");
            double totalDepenses = budgetParEvenement.values().stream().mapToDouble(Double::doubleValue).sum();
            System.out.printf("Nombre d'événements: %d%n", budgetParEvenement.size());
            System.out.printf("Dépense moyenne par événement: %.2f€%n", totalDepenses / budgetParEvenement.size());
        }
    }

    private void demanderAugmentationBudget() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Demande d'Augmentation de Budget ===");

        try {
            System.out.print("Montant souhaité (€) : ");
            double montant = Double.parseDouble(scanner.nextLine());

            System.out.print("Motif de la demande : ");
            String motif = scanner.nextLine();

            responsable.demanderAugmentationBudget(montant, motif);
            System.out.println("\n✅ Demande envoyée à l'administration");

        } catch (NumberFormatException e) {
            System.out.println("⚠️ Montant invalide");
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ " + e.getMessage());
        }
    }

    private void afficherBudgetRestant() {
        ResponsableCampus responsable = (ResponsableCampus) controleurPrincipal.getUtilisateurConnecte("RESPONSABLE");
        System.out.println("\n=== État du Budget ===");
        System.out.printf("Budget initial : %.2f€%n", responsable.getBudgetInitial());
        System.out.printf("Budget utilisé : %.2f€%n", responsable.getBudgetUtilise());
        System.out.printf("Budget restant : %.2f€%n", responsable.getBudgetDisponible());
        System.out.printf("Pourcentage utilisé : %.1f%%%n", responsable.getPourcentageBudgetUtilise());
    }
}