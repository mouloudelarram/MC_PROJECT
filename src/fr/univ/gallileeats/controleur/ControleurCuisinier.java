package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ControleurCuisinier extends AbstractControleur {
    private ControleurPrincipal controleurPrincipal;
    private Scanner scanner;

    public ControleurCuisinier(ControleurPrincipal controleurPrincipal) {
        super();
        this.controleurPrincipal = controleurPrincipal;
        this.scanner = new Scanner(System.in);
    }

    @Override
    protected void initialiserActionHandlers() {
        actionHandlers.put("1", params -> voirCommandesEnAttente());
        actionHandlers.put("2", params -> voirCommandesEnPreparation());
        actionHandlers.put("3", params -> afficherHistorique());
        actionHandlers.put("4", params -> afficherStatistiques());
        actionHandlers.put("5", params -> retourMenuPrincipal());

        // Actions pour le traitement des commandes
        actionHandlers.put("PREP_1", params -> commencerPreparation());
        actionHandlers.put("PREP_2", params -> terminerPreparation());
    }

    @Override
    public void traiterAction(String action) {
        Cuisinier cuisinier = (Cuisinier) controleurPrincipal.getUtilisateurConnecte("CUISINIER");
        verifierUtilisateurConnecte(cuisinier, "CUISINIER");

        if (action.startsWith("CUISINIER_")) {
            action = action.substring(10);
        }

        Consumer<String[]> handler = actionHandlers.get(action);
        if (handler != null) {
            handler.accept(new String[]{});
        } else {
            System.out.println("Action non reconnue : " + action);
            vue.afficher();
        }
    }

    private void voirCommandesEnAttente() {
        ((VueCuisinier)vue).afficherCommandesEnAttente();

        if (confirmerAction("\nVoulez-vous commencer la préparation d'une commande ?")) {
            commencerPreparation();
        }

        attendreTouche();
        vue.afficher();
    }

    private void afficherHistorique() {
        ((VueCuisinier)vue).afficherHistoriqueCommandes();
        attendreTouche();
        vue.afficher();
    }

    private void voirCommandesEnPreparation() {
        ((VueCuisinier)vue).afficherCommandesEnPreparation();

        if (confirmerAction("\nVoulez-vous marquer une commande comme prête ?")) {
            terminerPreparation();
        }

        attendreTouche();
        vue.afficher();
    }

    private void commencerPreparation() {
        System.out.print("Numéro de la commande à préparer : ");
        String numeroCommande = scanner.nextLine();

        List<Commande> commandesEnAttente = trouverCommandesParEtat(EtatCommande.NOUVELLE);  // Changed from EN_PREPARATION
        Commande commande = trouverCommandeParNumero(commandesEnAttente, numeroCommande);

        if (commande != null) {
            try {
                commande.changerEtat(EtatCommande.EN_PREPARATION);
                System.out.println("✅ Préparation commencée");
            } catch (IllegalStateException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Commande non trouvée");
        }
    }

    private void terminerPreparation() {
        System.out.print("Numéro de la commande terminée : ");
        String numeroCommande = scanner.nextLine();

        List<Commande> commandesEnPreparation = trouverCommandesParEtat(EtatCommande.EN_PREPARATION);
        Commande commande = trouverCommandeParNumero(commandesEnPreparation, numeroCommande);

        if (commande != null) {
            try {
                commande.changerEtat(EtatCommande.PRETE);
                System.out.println("✅ Commande prête");
            } catch (IllegalStateException e) {
                System.out.println("⚠️ " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ Commande non trouvée");
        }
    }

    private List<Commande> trouverCommandesParEtat(EtatCommande etat) {
        return getAllCommandes().stream()
                .filter(c -> c.getEtat() == etat)
                .collect(Collectors.toList());
    }

    private List<Commande> getAllCommandes() {
        List<Commande> toutesCommandes = new ArrayList<>();
        List<Utilisateur> clients = controleurPrincipal.getUtilisateurs("CLIENT");

        for (Utilisateur user : clients) {
            if (user instanceof Client) {
                Client client = (Client) user;
                toutesCommandes.addAll(client.getCommandes());
            }
        }

        return toutesCommandes;
    }

    private Commande trouverCommandeParNumero(List<Commande> commandes, String numero) {
        return commandes.stream()
                .filter(c -> c.getNumeroCommande().equalsIgnoreCase(numero.trim())) // 🔥 Vérification améliorée
                .findFirst()
                .orElse(null);
    }


    private boolean confirmerAction(String message) {
        System.out.print(message + " (oui/non) : ");
        return scanner.nextLine().trim().equalsIgnoreCase("oui");
    }

    @Override
    public void retourMenuPrincipal() {
        controleurPrincipal.afficherVuePrincipale();
    }
}