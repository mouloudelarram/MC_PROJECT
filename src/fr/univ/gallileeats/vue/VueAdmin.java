package fr.univ.gallileeats.vue;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.interfaces.IVueAdmin;
import fr.univ.gallileeats.model.*;
import java.util.List;
import java.util.Map;

public class VueAdmin extends AbstractVue implements IVueAdmin {
    private Administrateur admin;
    private static final String[] OPTIONS_MENU = {
            "Gérer le menu",
            "Voir toutes les commandes",
            "Gérer les utilisateurs",
            "Statistiques",
            "Retour au menu principal"
    };

    private static final String[] OPTIONS_MENU_GESTION = {
            "Ajouter un plat",
            "Modifier un plat",
            "Supprimer un plat",
            "Gérer les catégories",
            "Gérer les menus buffet",
            "Voir tous les plats",
            "Retour"
    };

    private static final String[] OPTIONS_UTILISATEURS = {
            "Liste des clients",
            "Liste des livreurs",
            "Liste des responsables campus",
            "Ajouter un utilisateur",
            "Gérer les droits d'accès",
            "Retour"
    };

    private static final String[] OPTIONS_COMMANDES = {
            "Commandes en cours",
            "Historique des commandes",
            "Commandes annulées",
            "Gérer les remboursements",
            "Retour"
    };

    private static final String[] OPTIONS_STATISTIQUES = {
            "Statistiques générales",
            "Statistiques par type d'utilisateur",
            "Statistiques des menus",
            "Statistiques des paiements",
            "Retour"
    };

    public VueAdmin(IControleur controleur) {
        super(controleur);
        this.admin = Administrateur.getInstance();
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
        System.out.println("╔════════════════════════════════════════════╗");
        System.out.println("║        Administration GALILEE EATS         ║");
        System.out.printf("║  Connecté en tant que : %s    ║%n", admin.getNom());
        System.out.println("╚════════════════════════════════════════════╝\n");
        afficherSeparateur();
    }

    private void afficherMenu() {
        for (int i = 0; i < OPTIONS_MENU.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU[i]);
        }
    }

    private void traiterChoix() {
        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU.length);
        controleur.traiterAction("ADMIN_" + choix);
    }

    @Override
    public void afficherGestionMenu() {
        effacerEcran();
        System.out.println("\n=== Gestion du Menu ===");
        for (int i = 0; i < OPTIONS_MENU_GESTION.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_MENU_GESTION[i]);
        }

        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_MENU_GESTION.length);
        controleur.traiterAction("ADMIN_MENU_" + choix);
    }

    @Override
    public void afficherGestionUtilisateurs() {
        effacerEcran();
        System.out.println("\n=== Gestion des Utilisateurs ===");
        for (int i = 0; i < OPTIONS_UTILISATEURS.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_UTILISATEURS[i]);
        }

        int choix = lireEntreeNumerique("\nVotre choix", 1, OPTIONS_UTILISATEURS.length);
        controleur.traiterAction("ADMIN_USERS_" + choix);
    }

    @Override
    public void afficherFormulaireAjoutPlat() {
        effacerEcran();
        System.out.println("\n=== Ajouter un nouveau plat ===");

        String nom = lireEntree("Nom du plat");
        String description = lireEntree("Description");
        double prix = lireEntreeDouble("Prix (€)", 0.0);

        System.out.println("\nCatégories disponibles :");
        System.out.println("1. ENTREE");
        System.out.println("2. PLAT");
        System.out.println("3. DESSERT");
        System.out.println("4. BUFFET");
        String categorie = lireEntree("Catégorie");

        System.out.println("\nType de menu :");
        System.out.println("1. STANDARD");
        System.out.println("2. ETUDIANT");
        System.out.println("3. BUFFET");
        String typeMenu = lireEntree("Type");

        controleur.traiterAction(String.format("ADMIN_AJOUT_PLAT_%s_%s_%f_%s_%s",
                nom.replace(" ", "_"),
                description.replace(" ", "_"),
                prix,
                categorie,
                typeMenu));
    }

    public void afficherFormulaireModificationPlat(MenuComponent plat) {
        effacerEcran();
        System.out.println("\n=== Modifier un plat ===");
        System.out.println("Plat actuel : " + plat.getNom());
        System.out.println("1. Modifier le prix");
        System.out.println("2. Modifier la description");
        System.out.println("3. Modifier la disponibilité");
        System.out.println("4. Retour");

        int choix = lireEntreeNumerique("\nVotre choix", 1, 4);
        switch(choix) {
            case 1:
                double nouveauPrix = lireEntreeDouble("Nouveau prix (€)", 0.0);
                controleur.traiterAction("ADMIN_MODIF_PLAT_PRIX_" + plat.getNom() + "_" + nouveauPrix);
                break;
            case 2:
                String nouvelleDescription = lireEntree("Nouvelle description");
                controleur.traiterAction("ADMIN_MODIF_PLAT_DESC_" + plat.getNom() + "_" +
                        nouvelleDescription.replace(" ", "_"));
                break;
            case 3:
                boolean disponible = confirmerAction("Le plat est-il disponible ?");
                controleur.traiterAction("ADMIN_MODIF_PLAT_DISPO_" + plat.getNom() + "_" + disponible);
                break;
        }
    }

    public void afficherFormulaireGestionCategories() {
        effacerEcran();
        System.out.println("\n=== Gestion des Catégories ===");
        System.out.println("1. Ajouter une catégorie");
        System.out.println("2. Supprimer une catégorie");
        System.out.println("3. Voir toutes les catégories");
        System.out.println("4. Retour");
    }

    public void afficherFormulaireGestionBuffet() {
        effacerEcran();
        System.out.println("\n=== Gestion des Menus Buffet ===");
        System.out.println("1. Créer un nouveau menu buffet");
        System.out.println("2. Modifier un menu buffet");
        System.out.println("3. Supprimer un menu buffet");
        System.out.println("4. Retour");
    }

    public void afficherFormulaireAjoutUtilisateur() {
        effacerEcran();
        System.out.println("\n=== Ajouter un Utilisateur ===");
        System.out.println("1. Client");
        System.out.println("2. Livreur");
        System.out.println("3. Responsable Campus");
        System.out.println("4. Retour");
    }

    public void afficherListeUtilisateurs(String type, List<Utilisateur> utilisateurs) {
        effacerEcran();
        if (utilisateurs.isEmpty()) {
            afficherInfo("Aucun " + type.toLowerCase() + " enregistré.");
            return;
        }

        System.out.printf("\n=== Liste des %ss ===\n", type);
        for (Utilisateur user : utilisateurs) {
            afficherUtilisateur(user);
        }
    }

    private void afficherUtilisateur(Utilisateur user) {
        effacerEcran();
        System.out.println("\n🆔 ID: " + user.getId());
        System.out.println("👤 Nom: " + user.getNom());
        System.out.println("📧 Email: " + user.getEmail());
        System.out.println("🔄 Statut: " + (user.isEstActif() ? "✅ Actif" : "❌ Inactif"));
        System.out.println("📅 Inscription: " + user.getDateInscription());

        if (user instanceof Client) {
            afficherDetailsClient((Client) user);
        } else if (user instanceof Livreur) {
            afficherDetailsLivreur((Livreur) user);
        } else if (user instanceof ResponsableCampus) {
            afficherDetailsResponsable((ResponsableCampus) user);
        }
        afficherSeparateur();
    }

    private void afficherDetailsClient(Client client) {
        effacerEcran();
        System.out.println("🎓 Type: " + (client.estEtudiant() ? "Étudiant" : "Standard"));
        System.out.println("📍 Adresse: " + client.getAdresseLivraison());
        System.out.printf("💰 Total dépensé: %.2f€%n", client.getTotalDepense());
        System.out.printf("🎯 Points fidélité: %.2f%n", client.getSoldePoints());
    }

    private void afficherDetailsLivreur(Livreur livreur) {
        effacerEcran();
        System.out.println("🚩 Zone: " + livreur.getZone());
        System.out.println("🚗 Véhicule: " + livreur.getVehicule());
        System.out.println("⭐ Note moyenne: " + livreur.getNoteMoyenne());
        System.out.println("📦 Livraisons effectuées: " + livreur.getNombreLivraisonsEffectuees());
    }

    private void afficherDetailsResponsable(ResponsableCampus resp) {
        effacerEcran();
        System.out.println("🏢 Département: " + resp.getDepartement());
        System.out.printf("💰 Budget total: %.2f€%n", resp.getBudgetInitial());
        System.out.printf("💵 Budget disponible: %.2f€%n", resp.getBudgetDisponible());
    }

    public void afficherGestionCommandes() {
        effacerEcran();
        System.out.println("\n=== Gestion des Commandes ===");
        for (int i = 0; i < OPTIONS_COMMANDES.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), OPTIONS_COMMANDES[i]);
        }
    }

    public void afficherStatistiquesGlobales() {
        effacerEcran();
        System.out.println("\n=== Statistiques Globales ===");
        // À compléter avec les statistiques réelles
        System.out.println("\n1. Statistiques des utilisateurs");
        System.out.println("2. Statistiques des commandes");
        System.out.println("3. Statistiques financières");
        System.out.println("4. Retour");
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            notifications.add(String.format(
                    "Commande n°%s - État modifié: %s - Client: %s",
                    commande.getNumeroCommande(),
                    commande.getEtat().getLibelle(),
                    commande.getClient().getNom()
            ));
            afficher();
        } else if (source instanceof Utilisateur) {
            Utilisateur user = (Utilisateur) source;
            notifications.add(String.format(
                    "Utilisateur modifié: %s (%s)",
                    user.getNom(),
                    user.getRole()
            ));
            afficher();
        }
    }
}