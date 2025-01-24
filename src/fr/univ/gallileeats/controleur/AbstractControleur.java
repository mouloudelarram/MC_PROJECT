package fr.univ.gallileeats.controleur;

import java.util.Scanner;
import fr.univ.gallileeats.interfaces.*;
import fr.univ.gallileeats.model.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractControleur implements IControleur {
    protected IVue vue;
    protected Map<String, Consumer<String[]>> actionHandlers;
    protected final Scanner scanner = new Scanner(System.in);

    public AbstractControleur() {
        this.actionHandlers = new HashMap<>();
        initialiserActionHandlers();
    }

    protected abstract void initialiserActionHandlers();

    @Override
    public void setVue(IVue vue) {
        this.vue = vue;
    }

    @Override
    public IVue getVue() {
        return this.vue;
    }

    @Override
    public void traiterAction(String action) {
        if (action == null || action.trim().isEmpty()) return;

        Consumer<String[]> handler = actionHandlers.get(action);
        if (handler != null) {
            try {
                handler.accept(new String[]{});
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement de l'action: " + e.getMessage());
                vue.afficher();
            }
        } else {
            System.out.println("Action non reconnue : " + action);
            vue.afficher();
        }
    }

    @Override
    public void afficherVuePrincipale() {
        // Implémentation par défaut vide
    }

    @Override
    public void gererCommandes() {
        // Implémentation par défaut vide
    }

    @Override
    public void afficherStatistiques() {
        // Implémentation par défaut vide
    }

    @Override
    public void afficherEtatProfil() {
        // Implémentation par défaut vide
    }

    @Override
    public void afficherFormulairePaiement() {
        // Implémentation par défaut vide
    }

    @Override
    public void retourMenuPrincipal() {
        // Implémentation par défaut vide
    }

    protected void ajouterHandler(String action, Consumer<String[]> handler) {
        actionHandlers.put(action, handler);
    }

    protected void verifierUtilisateurConnecte(Utilisateur utilisateur, String role) {
        if (utilisateur == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        if (!utilisateur.getRole().equals(role)) {
            throw new IllegalStateException("Rôle utilisateur invalide");
        }
    }

    protected void attendreTouche() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}