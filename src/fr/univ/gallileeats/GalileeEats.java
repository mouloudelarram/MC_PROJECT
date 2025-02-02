//GalileeEats.java
package fr.univ.gallileeats;

import fr.univ.gallileeats.controleur.ControleurPrincipal;
import fr.univ.gallileeats.model.*;
import java.util.ArrayList;
import java.util.List;

public class GalileeEats {
    private static ControleurPrincipal controleurPrincipal;
    private static List<Menu> menusDisponibles;
    private static List<MenuBuffet> menusBuffet;

    public static void main(String[] args) {
        System.out.println("=== Démarrage de GALILEE EATS ===");
        System.out.println("Initialisation du système...");

        try {
            initialiserDonnees();
            demarrerApplication();
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage de l'application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void initialiserDonnees() {
        System.out.println("Chargement des données initiales...");

        // Initialisation des listes
        menusDisponibles = new ArrayList<>();
        menusBuffet = new ArrayList<>();

        // Création des menus standards
        creerMenusStandards();

        // Création des menus buffet
        creerMenusBuffet();

        System.out.println("Données initiales chargées avec succès");
    }

    private static void creerMenusStandards() {
        // Menu Étudiant
        Menu menuEtudiant = new Menu("Menu Étudiant", "Menu économique pour les étudiants", "ETUDIANT");
        menuEtudiant.ajouter(new Plat("Salade Verte", "Salade fraîche de saison", 3.0, "ENTREE"));
        menuEtudiant.ajouter(new Plat("Pâtes Bolognaise", "Pâtes avec sauce bolognaise maison", 6.0, "PLAT"));
        menuEtudiant.ajouter(new Plat("Yaourt", "Yaourt nature ou aux fruits", 1.0, "DESSERT"));
        menusDisponibles.add(menuEtudiant);

        // Menu du Jour
        Menu menuJour = new Menu("Menu du Jour", "Menu complet équilibré", "STANDARD");
        menuJour.ajouter(new Plat("Soupe du Jour", "Soupe fraîche selon le marché", 4.0, "ENTREE"));
        menuJour.ajouter(new Plat("Plat du Chef", "Suggestion du chef", 9.0, "PLAT"));
        menuJour.ajouter(new Plat("Dessert du Jour", "Dessert maison", 3.0, "DESSERT"));
        menusDisponibles.add(menuJour);

        // Menu Végétarien
        Menu menuVege = new Menu("Menu Végétarien", "Menu 100% végétarien", "VEGETARIEN");
        menuVege.ajouter(new Plat("Salade Composée", "Mélange de crudités de saison", 5.0, "ENTREE"));
        menuVege.ajouter(new Plat("Curry de Légumes", "Curry de légumes avec riz", 8.0, "PLAT"));
        menuVege.ajouter(new Plat("Salade de Fruits", "Fruits frais de saison", 3.0, "DESSERT"));
        menusDisponibles.add(menuVege);
    }

    private static void creerMenusBuffet() {
        // Buffet Standard
        MenuBuffet buffetStandard = new MenuBuffet("Buffet Standard", "Buffet varié pour événements", 10);
        buffetStandard.ajouter(new Plat("Assortiment d'Entrées", "Sélection d'entrées variées", 8.0, "BUFFET"));
        buffetStandard.ajouter(new Plat("Plats Chauds", "Sélection de plats chauds", 15.0, "BUFFET"));
        buffetStandard.ajouter(new Plat("Desserts Variés", "Assortiment de desserts", 6.0, "BUFFET"));
        menusBuffet.add(buffetStandard);

        // Buffet Gala
        MenuBuffet buffetGala = new MenuBuffet("Buffet Gala", "Buffet premium pour événements spéciaux", 20);
        buffetGala.ajouter(new Plat("Plateau Prestige", "Sélection d'entrées premium", 12.0, "BUFFET"));
        buffetGala.ajouter(new Plat("Plats Signatures", "Spécialités du chef", 20.0, "BUFFET"));
        buffetGala.ajouter(new Plat("Desserts de Luxe", "Pâtisseries fines", 10.0, "BUFFET"));
        menusBuffet.add(buffetGala);
    }

    private static void demarrerApplication() {
        System.out.println("Démarrage de l'interface utilisateur...");

        // Création du contrôleur principal
        controleurPrincipal = new ControleurPrincipal();

                // ajouter des utilisateurs de démonstration
        // Création d'un client de démonstration
        Client client = new Client(
                "CLI1",
                "Jean Dupont",
                "jean@galilee.fr",
                "password123",
                "123 rue de Paris"
        );
        client.activerStatutEtudiant("20240001");
        controleurPrincipal.setUtilisateurConnecte("CLIENT", client);

        // Création d'un livreur de démonstration
        Livreur livreur = new Livreur(
                "LIV1",
                "Pierre Martin",
                "pierre@galilee.fr",
                "password123",
                "Vélo",
                "Zone Nord"
        );
        controleurPrincipal.setUtilisateurConnecte("LIVREUR", livreur);

        // Création d'un responsable de campus de démonstration
        ResponsableCampus responsable = new ResponsableCampus(
                "RES1",
                "Marie Durand",
                "marie@galilee.fr",
                "password123",
                "Informatique",
                5000.0
        );
        controleurPrincipal.setUtilisateurConnecte("RESPONSABLE", responsable);



        controleurPrincipal.initialiserSousControleurs();

        // Affichage de la vue principale
        controleurPrincipal.afficherVuePrincipale();
    }

    // Getters pour accéder aux données depuis d'autres classes
    public static List<Menu> getMenusDisponibles() {
        return new ArrayList<>(menusDisponibles);
    }

    public static List<MenuBuffet> getMenusBuffet() {
        return new ArrayList<>(menusBuffet);
    }
}