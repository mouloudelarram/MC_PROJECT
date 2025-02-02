package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class ControleurLivreur extends AbstractControleur {
    private ControleurPrincipal controleurPrincipal;
    private Scanner scanner;

    public ControleurLivreur(ControleurPrincipal controleurPrincipal) {
        super();
        this.controleurPrincipal = controleurPrincipal;
        this.scanner = new Scanner(System.in);
    }

    @Override
    protected void initialiserActionHandlers() {
        actionHandlers.put("1", params -> voirCommandesALivrer());
        actionHandlers.put("2", params -> marquerCommandeLivree());
        actionHandlers.put("3", params -> afficherHistorique());
        actionHandlers.put("4", params -> gererDisponibilite());
        actionHandlers.put("5", params -> afficherStatistiques());
        actionHandlers.put("6", params -> retourMenuPrincipal());
    }

    @Override
    public void traiterAction(String action) {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        verifierUtilisateurConnecte(livreur, "LIVREUR");

        if (action.startsWith("LIVREUR_")) {
            action = action.substring(8);
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
    public void afficherVuePrincipale() {
        controleurPrincipal.afficherVuePrincipale();
    }

    @Override
    public void gererCommandes() {
        voirCommandesALivrer();
    }

    @Override
    public void afficherStatistiques() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        ((VueLivreur)vue).afficherStatistiques();
        attendreTouche();
        vue.afficher();
    }

    @Override
    public void afficherEtatProfil() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        System.out.println("\n=== Profil Livreur ===");
        System.out.println("👤 Nom: " + livreur.getNom());
        System.out.println("📧 Email: " + livreur.getEmail());
        System.out.println("🚗 Véhicule: " + livreur.getVehicule());
        System.out.println("🌍 Zone: " + livreur.getZone());
        System.out.println("⭐ Note moyenne: " + livreur.getNoteMoyenne());
        System.out.println("📊 Livraisons effectuées: " + livreur.getNombreLivraisonsEffectuees());
        attendreTouche();
        vue.afficher();
    }

    @Override
    public void afficherFormulairePaiement() {
        // Non utilisé pour le livreur
    }

    @Override
    public void retourMenuPrincipal() {
        controleurPrincipal.afficherVuePrincipale();
    }

    private void voirCommandesALivrer() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        ((VueLivreur)vue).afficherCommandesALivrer();

        attendreTouche();
        vue.afficher();
    }

    private void marquerCommandeLivree() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        List<Commande> commandes = livreur.getCommandesALivrer();

        if (commandes.isEmpty()) {
            System.out.println("\nAucune commande à livrer.");
            attendreTouche();
            vue.afficher();
            return;
        }

        ((VueLivreur)vue).afficherFormulaireLivraison();

        try {
            int choix = Integer.parseInt(scanner.nextLine());
            if (choix == 0) {
                vue.afficher();
                return;
            }

            if (choix > 0 && choix <= commandes.size()) {
                Commande commande = commandes.get(choix - 1);
                ((VueLivreur)vue).afficherConfirmationLivraison(commande);

                System.out.print("Votre choix (1: Livrer, 2: Problème, 3: Annuler) : ");
                String confirmation = scanner.nextLine();
                switch (confirmation) {
                    case "1":
                        livreur.terminerLivraison(commande);
                        System.out.println("\n✅ Commande livrée avec succès !");
                        break;
                    case "2":
                        System.out.print("\nDétails du problème : ");
                        String commentaire = scanner.nextLine();
                        livreur.signalerProblemeLivraison(commande, commentaire);
                        System.out.println("\n⚠️ Problème signalé");
                        break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("\n⚠️ Choix invalide");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\n⚠️ Erreur : " + e.getMessage());
        }

        attendreTouche();
        vue.afficher();
    }

    private void afficherHistorique() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");
        List<Commande> historique = livreur.getHistoriqueLivraisons();

        if (historique.isEmpty()) {
            System.out.println("\nAucune livraison dans l'historique.");
        } else {
            System.out.println("\n=== Historique des livraisons ===");
            historique.forEach(commande -> {
                System.out.println("\n📦 Commande n°" + commande.getNumeroCommande());
                System.out.println("👤 Client : " + commande.getClient().getNom());
                System.out.println("📍 Adresse : " + commande.getAdresseLivraison());
                System.out.println("🔄 État : " + commande.getEtat().getLibelle());
                System.out.printf("💰 Total : %.2f€%n", commande.getTotal());
                System.out.println("----------------------------------------");
            });
        }
        attendreTouche();
        vue.afficher();
    }

    private void gererDisponibilite() {
        Livreur livreur = (Livreur) controleurPrincipal.getUtilisateurConnecte("LIVREUR");

        System.out.println("\n=== Gestion de la disponibilité ===");
        System.out.println("1. Me marquer comme disponible");
        System.out.println("2. Me marquer comme indisponible");
        System.out.println("3. Prendre une pause");

        try {
            String choix = scanner.nextLine();
            switch (choix) {
                case "1": // Disponible
                    if (livreur.isEnPause()) {
                        livreur.terminerPause();
                    }
                    System.out.println("\n✅ Vous êtes maintenant disponible");
                    break;

                case "2": // Non disponible
                    if (!livreur.isEnPause()) {
                        livreur.commencerPause();
                    }
                    System.out.println("\n✅ Vous êtes maintenant indisponible");
                    break;

                case "3": // Pause
                    if (!livreur.getCommandesALivrer().isEmpty()) {
                        System.out.println("\n⚠️ Impossible de prendre une pause avec des commandes en cours");
                    } else {
                        livreur.commencerPause();
                        System.out.println("\n✅ Pause enregistrée");
                    }
                    break;
            }
        } catch (IllegalStateException e) {
            System.out.println("\nErreur : " + e.getMessage());
        }

        attendreTouche();
        vue.afficher();
    }
}