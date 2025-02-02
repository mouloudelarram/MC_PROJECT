package fr.univ.gallileeats.controleur;

import java.util.Scanner;

import fr.univ.gallileeats.interfaces.*;
import fr.univ.gallileeats.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Classe abstraite servant de base aux contrôleurs de l'application.
 * Elle implémente l'interface IControleur et gère les interactions avec la vue.
 */
public abstract class AbstractControleur implements IControleur {
    /**
     * Vue associée au contrôleur
     */
    protected IVue vue;
    /**
     * Map associant des actions à leurs gestionnaires
     */
    protected Map<String, Consumer<String[]>> actionHandlers;
    /**
     * Scanner pour la saisie utilisateur
     */
    protected final Scanner scanner = new Scanner(System.in);

    /**
     * Constructeur initialisant la gestion des actions.
     */
    public AbstractControleur() {
        this.actionHandlers = new HashMap<>();
        initialiserActionHandlers();
    }

    /**
     * Méthode abstraite devant être implémentée pour définir les actions gérées par le contrôleur.
     */
    protected abstract void initialiserActionHandlers();

    /**
     * Associe une vue au contrôleur.
     *
     * @param vue La vue à associer.
     */
    @Override
    public void setVue(IVue vue) {
        this.vue = vue;
    }

    /**
     * Retourne la vue associée au contrôleur.
     *
     * @return La vue actuelle.
     */
    @Override
    public IVue getVue() {
        return this.vue;
    }

    /**
     * Traite une action en recherchant son gestionnaire et en l'exécutant.
     *
     * @param action Nom de l'action à traiter.
     */
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

    /**
     * Affiche la vue principale de l'application.
     */
    @Override
    public void afficherVuePrincipale() {
        // Implémentation par défaut vide
    }

    /**
     * Gère les commandes.
     */
    @Override
    public void gererCommandes() {
        // Implémentation par défaut vide
    }

    /**
     * Affiche les statistiques.
     */
    @Override
    public void afficherStatistiques() {
        // Implémentation par défaut vide
    }

    /**
     * Affiche l'état du profil utilisateur.
     */
    @Override
    public void afficherEtatProfil() {
        // Implémentation par défaut vide
    }

    /**
     * Affiche le formulaire de paiement.
     */
    @Override
    public void afficherFormulairePaiement() {
        // Implémentation par défaut vide
    }

    /**
     * Retourne au menu principal.
     */
    @Override
    public void retourMenuPrincipal() {
        // Implémentation par défaut vide
    }

    /**
     * Ajoute un gestionnaire d'action à la liste des actions gérées.
     *
     * @param action  Nom de l'action.
     * @param handler Fonction à exécuter pour cette action.
     */
    protected void ajouterHandler(String action, Consumer<String[]> handler) {
        actionHandlers.put(action, handler);
    }

    /**
     * Vérifie si un utilisateur est connecté et a le bon rôle.
     *
     * @param utilisateur Utilisateur connecté.
     * @param role        Rôle attendu.
     * @throws IllegalStateException si l'utilisateur n'est pas connecté ou a un rôle invalide.
     */
    protected void verifierUtilisateurConnecte(Utilisateur utilisateur, String role) {
        if (utilisateur == null) {
            throw new IllegalStateException("Aucun utilisateur connecté");
        }
        if (!utilisateur.getRole().equals(role)) {
            throw new IllegalStateException("Rôle utilisateur invalide");
        }
    }

    /**
     * Attend que l'utilisateur appuie sur une touche avant de continuer.
     */
    protected void attendreTouche() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }
}