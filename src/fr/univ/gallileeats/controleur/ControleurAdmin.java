package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;
import java.util.*;
import java.util.function.Consumer;

public class ControleurAdmin extends AbstractControleur {
    private Map<String, List<MenuComponent>> menus;
    private Map<String, List<Utilisateur>> utilisateurs;
    private ControleurPrincipal controleurPrincipal;

    public ControleurAdmin(ControleurPrincipal controleurPrincipal) {
        super();
        this.controleurPrincipal = controleurPrincipal;
        this.menus = new HashMap<>();
        this.utilisateurs = new HashMap<>();
        initialiserDonnees();
    }

    @Override
    protected void initialiserActionHandlers() {
        // Menu principal
        actionHandlers.put("1", params -> ((VueAdmin)vue).afficherGestionMenu());
        actionHandlers.put("2", params -> gererCommandes());
        actionHandlers.put("3", params -> ((VueAdmin)vue).afficherGestionUtilisateurs());
        actionHandlers.put("4", params -> afficherStatistiques());
        actionHandlers.put("5", params -> retourMenuPrincipal());

        // Gestion menu
        actionHandlers.put("MENU_1", params -> ajouterPlat());
        actionHandlers.put("MENU_2", params -> modifierPlat());
        actionHandlers.put("MENU_3", params -> supprimerPlat());
        actionHandlers.put("MENU_4", params -> gererCategories());
        actionHandlers.put("MENU_5", params -> ((VueAdmin)vue).afficher());

        // Gestion utilisateurs
        actionHandlers.put("USERS_1", params -> afficherListeUtilisateurs("CLIENT"));
        actionHandlers.put("USERS_2", params -> afficherListeUtilisateurs("LIVREUR"));
        actionHandlers.put("USERS_3", params -> afficherListeUtilisateurs("RESPONSABLE"));
        actionHandlers.put("USERS_4", params -> ajouterUtilisateur());
        actionHandlers.put("USERS_5", params -> ((VueAdmin)vue).afficher());
    }

    @Override
    public void traiterAction(String action) {
        Administrateur admin = (Administrateur) controleurPrincipal.getUtilisateurConnecte("ADMIN");
        verifierUtilisateurConnecte(admin, "ADMIN");

        if (action.startsWith("ADMIN_")) {
            action = action.substring(6);
        }

        Consumer<String[]> handler = actionHandlers.get(action);
        if (handler != null) {
            handler.accept(new String[]{});
        } else {
            System.out.println("Action non reconnue : " + action);
            vue.afficher();
        }
    }

    @Override
    public void afficherVuePrincipale() {
        controleurPrincipal.afficherVuePrincipale();
    }

    @Override
    public void gererCommandes() {
        System.out.println("\n=== Gestion des commandes ===");
        System.out.println("1. Commandes en cours");
        System.out.println("2. Historique des commandes");
        System.out.println("3. Commandes annulées");
        System.out.println("4. Retour");

        Scanner scanner = new Scanner(System.in);
        String choix = scanner.nextLine();
        switch (choix) {
            case "1": afficherCommandesEnCours(); break;
            case "2": afficherHistoriqueCommandes(); break;
            case "3": afficherCommandesAnnulees(); break;
            case "4": vue.afficher(); break;
        }
    }

    @Override
    public void afficherStatistiques() {
        System.out.println("\n=== Statistiques Globales ===");

        // Statistiques des utilisateurs
        System.out.println("\nUtilisateurs :");
        utilisateurs.forEach((type, liste) ->
                System.out.println("- " + type + " : " + liste.size())
        );

        // Statistiques des menus
        System.out.println("\nMenus :");
        menus.forEach((type, liste) ->
                System.out.println("- " + type + " : " + liste.size() + " plats")
        );

        vue.afficher();
    }

    @Override
    public void afficherEtatProfil() {
        Administrateur admin = Administrateur.getInstance();
        System.out.println("\n=== Profil Administrateur ===");
        System.out.println("Nom : " + admin.getNom());
        System.out.println("Email : " + admin.getEmail());
        System.out.println("Rôle : " + admin.getRole());
    }

    @Override
    public void afficherFormulairePaiement() {
        // Non utilisé pour l'administrateur
    }

    @Override
    public void retourMenuPrincipal() {
        controleurPrincipal.afficherVuePrincipale();
    }

    private void initialiserDonnees() {
        // Initialisation des menus
        menus.put("STANDARD", new ArrayList<>());
        menus.put("BUFFET", new ArrayList<>());
        menus.put("ETUDIANT", new ArrayList<>());

        // Initialisation des utilisateurs
        utilisateurs.put("CLIENT", new ArrayList<>());
        utilisateurs.put("LIVREUR", new ArrayList<>());
        utilisateurs.put("RESPONSABLE", new ArrayList<>());

        ajouterDonneesDemo();
    }

    private void ajouterDonneesDemo() {
        // Ajout de menus de démonstration
        Menu menuJour = new Menu("Menu du Jour", "Menu complet équilibré", "STANDARD");
        menuJour.ajouter(new Plat("Salade César", "Salade fraîche", 5.0, "ENTREE"));
        menuJour.ajouter(new Plat("Poulet rôti", "Avec légumes", 12.0, "PLAT"));
        menuJour.ajouter(new Plat("Tarte aux pommes", "Maison", 4.0, "DESSERT"));
        menus.get("STANDARD").add(menuJour);

        // Ajout d'utilisateurs de démonstration
        utilisateurs.get("CLIENT").add(
                new Client("CLI1", "Jean Dupont", "jean@galilee.fr", "password", "123 rue de Paris")
        );
        utilisateurs.get("LIVREUR").add(
                new Livreur("LIV1", "Pierre Martin", "pierre@galilee.fr", "password", "Vélo", "Zone Nord")
        );
        utilisateurs.get("RESPONSABLE").add(
                new ResponsableCampus("RES1", "Marie Durand", "marie@galilee.fr", "password", "Informatique", 5000.0)
        );
    }

    private void ajouterPlat() {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.print("Nom du plat : ");
            String nom = scanner.nextLine();
            System.out.print("Description : ");
            String description = scanner.nextLine();
            System.out.print("Prix : ");
            double prix = Double.parseDouble(scanner.nextLine());

            System.out.println("\nCatégories disponibles :");
            System.out.println("1. ENTREE");
            System.out.println("2. PLAT");
            System.out.println("3. DESSERT");
            System.out.print("Catégorie : ");
            String categorie = scanner.nextLine();

            System.out.println("\nTypes de menu :");
            System.out.println("1. STANDARD");
            System.out.println("2. BUFFET");
            System.out.println("3. ETUDIANT");
            System.out.print("Type de menu : ");
            String typeMenu = scanner.nextLine();

            Plat nouveauPlat = new Plat(nom, description, prix, categorie);
            menus.get(typeMenu).add(nouveauPlat);

            System.out.println("\n✅ Plat ajouté avec succès !");
        } catch (Exception e) {
            System.out.println("\n⚠️ Erreur lors de l'ajout du plat : " + e.getMessage());
        }

        ((VueAdmin)vue).afficherGestionMenu();
    }

    private void modifierPlat() {
        afficherTousLesPlats();
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nNom du plat à modifier : ");
        String nomPlat = scanner.nextLine();

        MenuComponent platAModifier = trouverPlatParNom(nomPlat);
        if (platAModifier instanceof Plat) {
            Plat plat = (Plat) platAModifier;

            System.out.println("\n1. Modifier le prix");
            System.out.println("2. Modifier la description");
            System.out.println("3. Modifier la disponibilité");

            String choix = scanner.nextLine();
            switch(choix) {
                case "1":
                    System.out.print("Nouveau prix : ");
                    double nouveauPrix = Double.parseDouble(scanner.nextLine());
                    // Mise à jour du prix
                    System.out.println("✅ Prix mis à jour !");
                    break;
                case "2":
                    System.out.print("Nouvelle description : ");
                    String nouvelleDescription = scanner.nextLine();
                    plat = new Plat(plat.getNom(), nouvelleDescription, plat.getPrix(), plat.getCategorie());
                    System.out.println("✅ Description mise à jour !");
                    break;
                case "3":
                    System.out.print("Disponible (oui/non) : ");
                    boolean disponible = scanner.nextLine().equalsIgnoreCase("oui");
                    plat.setDisponible(disponible);
                    System.out.println("✅ Disponibilité mise à jour !");
                    break;
            }
        } else {
            System.out.println("\n⚠️ Plat non trouvé");
        }

        ((VueAdmin)vue).afficherGestionMenu();
    }

    private void supprimerPlat() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nNom du plat à supprimer : ");
        String nomPlat = scanner.nextLine();

        if (confirmerAction("Êtes-vous sûr de vouloir supprimer ce plat ?")) {
            boolean supprime = false;
            for (List<MenuComponent> platsList : menus.values()) {
                supprime |= platsList.removeIf(p -> p.getNom().equals(nomPlat));
            }

            if (supprime) {
                System.out.println("✅ Plat supprimé avec succès !");
            } else {
                System.out.println("⚠️ Plat non trouvé");
            }
        }

        ((VueAdmin)vue).afficherGestionMenu();
    }

    private void gererCategories() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Gestion des catégories ===");
        System.out.println("1. Ajouter une catégorie");
        System.out.println("2. Supprimer une catégorie");
        System.out.println("3. Voir toutes les catégories");

        String choix = scanner.nextLine();
        switch(choix) {
            case "1":
                System.out.print("Nom de la nouvelle catégorie : ");
                String nouvelleCat = scanner.nextLine();
                menus.put(nouvelleCat.toUpperCase(), new ArrayList<>());
                System.out.println("✅ Catégorie ajoutée !");
                break;
            case "2":
                System.out.print("Catégorie à supprimer : ");
                String catASupprimer = scanner.nextLine();
                if (menus.remove(catASupprimer.toUpperCase()) != null) {
                    System.out.println("✅ Catégorie supprimée !");
                } else {
                    System.out.println("⚠️ Catégorie non trouvée");
                }
                break;
            case "3":
                System.out.println("\nCatégories disponibles :");
                menus.keySet().forEach(cat -> System.out.println("- " + cat));
                break;
        }

        ((VueAdmin)vue).afficherGestionMenu();
    }

    private void afficherListeUtilisateurs(String type) {
        List<Utilisateur> liste = utilisateurs.get(type);
        ((VueAdmin)vue).afficherListeUtilisateurs(type, liste);
    }

    private void ajouterUtilisateur() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Ajouter un utilisateur ===");
        System.out.println("1. Client");
        System.out.println("2. Livreur");
        System.out.println("3. Responsable Campus");

        try {
            String type = scanner.nextLine();
            System.out.print("Nom : ");
            String nom = scanner.nextLine();
            System.out.print("Email : ");
            String email = scanner.nextLine();
            System.out.print("Mot de passe : ");
            String motDePasse = scanner.nextLine();

            Utilisateur nouvelUtilisateur = null;
            switch(type) {
                case "1":
                    System.out.print("Adresse : ");
                    String adresse = scanner.nextLine();
                    nouvelUtilisateur = new Client("CLI" + generateId(), nom, email, motDePasse, adresse);
                    utilisateurs.get("CLIENT").add(nouvelUtilisateur);
                    break;
                case "2":
                    System.out.print("Véhicule : ");
                    String vehicule = scanner.nextLine();
                    System.out.print("Zone : ");
                    String zone = scanner.nextLine();
                    nouvelUtilisateur = new Livreur("LIV" + generateId(), nom, email, motDePasse, vehicule, zone);
                    utilisateurs.get("LIVREUR").add(nouvelUtilisateur);
                    break;
                case "3":
                    System.out.print("Département : ");
                    String departement = scanner.nextLine();
                    System.out.print("Budget initial : ");
                    double budget = Double.parseDouble(scanner.nextLine());
                    nouvelUtilisateur = new ResponsableCampus("RES" + generateId(), nom, email, motDePasse, departement, budget);
                    utilisateurs.get("RESPONSABLE").add(nouvelUtilisateur);
                    break;
            }

            if (nouvelUtilisateur != null) {
                System.out.println("\n✅ Utilisateur ajouté avec succès !");
            }
        } catch (Exception e) {
            System.out.println("\n⚠️ Erreur lors de l'ajout de l'utilisateur : " +
                    e.getMessage());
        }

        ((VueAdmin)vue).afficherGestionUtilisateurs();
    }

    private void afficherCommandesEnCours() {
        for (List<Utilisateur> userList : utilisateurs.values()) {
            for (Utilisateur user : userList) {
                if (user instanceof Client) {
                    Client client = (Client) user;
                    List<Commande> commandesEnCours = client.getCommandesEnCours();
                    if (!commandesEnCours.isEmpty()) {
                        System.out.println("\nCommandes en cours pour " + client.getNom() + ":");
                        commandesEnCours.forEach(this::afficherDetailsCommande);
                    }
                }
            }
        }
        vue.afficher();
    }

    private void afficherHistoriqueCommandes() {
        System.out.println("\n=== Historique complet des commandes ===");
        for (List<Utilisateur> userList : utilisateurs.values()) {
            for (Utilisateur user : userList) {
                if (user instanceof Client) {
                    Client client = (Client) user;
                    List<Commande> commandes = client.getCommandes();
                    if (!commandes.isEmpty()) {
                        System.out.println("\nCommandes de " + client.getNom() + ":");
                        commandes.forEach(this::afficherDetailsCommande);
                    }
                }
            }
        }
        vue.afficher();
    }

    private void afficherCommandesAnnulees() {
        System.out.println("\n=== Commandes Annulées ===");
        for (List<Utilisateur> userList : utilisateurs.values()) {
            for (Utilisateur user : userList) {
                if (user instanceof Client) {
                    Client client = (Client) user;
                    client.getCommandes().stream()
                            .filter(c -> c.getEtat() == EtatCommande.ANNULEE)
                            .forEach(this::afficherDetailsCommande);
                }
            }
        }
        vue.afficher();
    }

    private void afficherDetailsCommande(Commande commande) {
        System.out.println("\n🔖 Commande n°" + commande.getNumeroCommande());
        System.out.println("👤 Client : " + commande.getClient().getNom());
        System.out.println("📅 Date : " + commande.getDateCommande());
        System.out.println("🔄 État : " + commande.getEtat().getLibelle());
        System.out.printf("💰 Total : %.2f€%n", commande.getTotal());

        if (commande.getCommentaires() != null && !commande.getCommentaires().isEmpty()) {
            System.out.println("💬 Commentaires : " + commande.getCommentaires());
        }

        if (commande.getModeLivraison() == Commande.ModeLivraison.LIVRAISON &&
                commande.getLivreur() != null) {
            System.out.println("🚚 Livreur : " + commande.getLivreur().getNom());
        }
        System.out.println("----------------------------------------");
    }

    private void afficherTousLesPlats() {
        System.out.println("\nPlats disponibles :");
        menus.forEach((type, plats) -> {
            System.out.println("\n" + type + " :");
            plats.forEach(plat -> System.out.println("- " + plat.getNom() + " (" + plat.getPrix() + "€)"));
        });
    }

    private MenuComponent trouverPlatParNom(String nom) {
        for (List<MenuComponent> platsList : menus.values()) {
            for (MenuComponent plat : platsList) {
                if (plat.getNom().equals(nom)) {
                    return plat;
                }
            }
        }
        return null;
    }

    private boolean confirmerAction(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message + " (oui/non) : ");
        return scanner.nextLine().equalsIgnoreCase("oui");
    }

    private String generateId() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }
}