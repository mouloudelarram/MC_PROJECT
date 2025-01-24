package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Sujet;
import fr.univ.gallileeats.interfaces.Observateur;
import fr.univ.gallileeats.interfaces.StrategyPaiement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Commande implements Sujet {
    // Compteur statique pour générer les numéros de commande
    private static int compteur = 0;

    // Attributs de base
    private String numeroCommande;
    private Utilisateur client;
    private MenuComponent menu;
    private EtatCommande etat;
    private List<Observateur> observateurs;
    private Date dateCommande;
    private Date dateLivraison;
    private String adresseLivraison;
    private ModeLivraison modeLivraison;
    private StrategyPaiement strategyPaiement;
    private double total;
    private String evenement;
    private Livreur livreur;
    private int nombrePersonnes;
    private String commentaires;
    private boolean estPaye;
    private List<String> historique;

    public enum ModeLivraison {
        LIVRAISON("Livraison à domicile"),
        SUR_PLACE("Sur place"),
        A_EMPORTER("À emporter");

        private String libelle;

        ModeLivraison(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    // Constructeur complet
    public Commande(Utilisateur client, MenuComponent menu, int nombrePersonnes, ModeLivraison modeLivraison) {
        if (client == null || menu == null) {
            throw new IllegalArgumentException("Le client et le menu sont requis");
        }
        if (nombrePersonnes <= 0) {
            throw new IllegalArgumentException("Le nombre de personnes doit être positif");
        }

        this.numeroCommande = genererNumeroCommande();
        this.client = client;
        this.menu = menu;
        this.nombrePersonnes = nombrePersonnes;
        this.modeLivraison = modeLivraison;
        this.etat = EtatCommande.NOUVELLE;
        this.observateurs = new ArrayList<>();
        this.dateCommande = new Date();
        this.historique = new ArrayList<>();
        this.estPaye = false;

        // Gestion de l'adresse selon le mode de livraison
        if (modeLivraison == ModeLivraison.LIVRAISON && client instanceof Client) {
            this.adresseLivraison = ((Client) client).getAdresseLivraison();
        }

        ajouterEvenementHistorique("Commande créée");
        calculerTotal();
    }

    // Constructeur simplifié pour commande standard
    public Commande(Utilisateur client, MenuComponent menu) {
        this(client, menu, 1, ModeLivraison.LIVRAISON);
    }

    private String genererNumeroCommande() {
        return "CMD" + String.format("%04d", ++compteur);
    }

    // Gestion du paiement
    public void setStrategyPaiement(StrategyPaiement strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La stratégie de paiement ne peut pas être nulle");
        }
        this.strategyPaiement = strategy;
        ajouterEvenementHistorique("Méthode de paiement définie: " + strategy.getClass().getSimpleName());
    }

    public void payer() {
        if (strategyPaiement == null) {
            throw new IllegalStateException("Aucune méthode de paiement définie");
        }
        if (estPaye) {
            throw new IllegalStateException("La commande est déjà payée");
        }

        try {
            strategyPaiement.payer(total);
            this.estPaye = true;
            changerEtat(EtatCommande.EN_PREPARATION);
            ajouterEvenementHistorique("Paiement effectué avec succès");
        } catch (Exception e) {
            ajouterEvenementHistorique("Échec du paiement: " + e.getMessage());
            throw new IllegalStateException("Échec du paiement: " + e.getMessage());
        }
    }

    // Calcul du total
    public void calculerTotal() {
        // Prix de base selon le menu
        this.total = menu.getPrix() * nombrePersonnes;

        // Réductions selon le type de client et la taille de la commande
        appliquerReductions();

        // Frais supplémentaires selon le mode de livraison
        appliquerFraisLivraison();

        notifierObservateurs();
    }

    private void appliquerReductions() {
        // Réduction pour les grandes commandes
        if (nombrePersonnes >= 20) {
            total *= 0.9; // 10% de réduction
            ajouterEvenementHistorique("Réduction de 10% appliquée (commande groupe)");
        }

        // Réduction étudiant
        if (client instanceof Client && ((Client) client).estEtudiant()) {
            total *= 0.85; // 15% de réduction
            ajouterEvenementHistorique("Réduction étudiant de 15% appliquée");
        }
    }

    private void appliquerFraisLivraison() {
        if (modeLivraison == ModeLivraison.LIVRAISON) {
            if (total < 20) {
                total += 2.5; // Frais de livraison pour petites commandes
                ajouterEvenementHistorique("Frais de livraison ajoutés: 2.50€");
            }
        }
    }

    // Gestion des états
    public void changerEtat(EtatCommande nouvelEtat) {
        if (!estTransitionValide(nouvelEtat)) {
            throw new IllegalStateException("Transition d'état invalide de " +
                    this.etat + " vers " + nouvelEtat);
        }

        EtatCommande ancienEtat = this.etat;
        this.etat = nouvelEtat;

        gererChangementEtat(ancienEtat, nouvelEtat);
        ajouterEvenementHistorique("État changé : " + nouvelEtat.getLibelle());
        notifierObservateurs();
    }

    private void gererChangementEtat(EtatCommande ancienEtat, EtatCommande nouvelEtat) {
        switch (nouvelEtat) {
            case EN_PREPARATION:
                verifierPaiement();
                break;
            case EN_LIVRAISON:
                verifierLivreur();
                break;
            case LIVREE:
            case SERVIE:
                this.dateLivraison = new Date();
                break;
            case ANNULEE:
                gererAnnulation();
                break;
        }
    }

    private void verifierPaiement() {
        if (!estPaye) {
            throw new IllegalStateException("La commande doit être payée avant la préparation");
        }
    }

    private void verifierLivreur() {
        if (modeLivraison == ModeLivraison.LIVRAISON && livreur == null) {
            throw new IllegalStateException("Un livreur doit être assigné pour la livraison");
        }
    }

    private void gererAnnulation() {
        if (estPaye) {
            ajouterEvenementHistorique("Remboursement nécessaire");
        }
    }

    private boolean estTransitionValide(EtatCommande nouvelEtat) {
        switch (this.etat) {
            case NOUVELLE:
                return nouvelEtat == EtatCommande.EN_PREPARATION ||
                        nouvelEtat == EtatCommande.ANNULEE;
            case EN_PREPARATION:
                return nouvelEtat == EtatCommande.PRETE ||
                        nouvelEtat == EtatCommande.ANNULEE;
            case PRETE:
                return nouvelEtat == EtatCommande.EN_LIVRAISON ||
                        nouvelEtat == EtatCommande.SERVIE ||
                        nouvelEtat == EtatCommande.ANNULEE;
            case EN_LIVRAISON:
                return nouvelEtat == EtatCommande.LIVREE ||
                        nouvelEtat == EtatCommande.ANNULEE;
            case LIVREE:
            case SERVIE:
            case ANNULEE:
                return false;
            default:
                return false;
        }
    }

    private void ajouterEvenementHistorique(String evenement) {
        String timestamp = new Date().toString();
        historique.add(timestamp + " - " + evenement);
    }

    // Implémentation du pattern Observer
    @Override
    public void ajouterObservateur(Observateur o) {
        if (!observateurs.contains(o)) {
            observateurs.add(o);
        }
    }

    @Override
    public void supprimerObservateur(Observateur o) {
        observateurs.remove(o);
    }

    @Override
    public void notifierObservateurs() {
        for (Observateur o : observateurs) {
            o.actualiser(this);
        }
    }

    // Getters
    public String getNumeroCommande() { return numeroCommande; }
    public Utilisateur getClient() { return client; }
    public MenuComponent getMenu() { return menu; }
    public EtatCommande getEtat() { return etat; }
    public double getTotal() { return total; }
    public Date getDateCommande() { return dateCommande; }
    public Date getDateLivraison() { return dateLivraison; }
    public String getAdresseLivraison() { return adresseLivraison; }
    public ModeLivraison getModeLivraison() { return modeLivraison; }
    public Livreur getLivreur() { return livreur; }
    public int getNombrePersonnes() { return nombrePersonnes; }
    public String getCommentaires() { return commentaires; }
    public boolean estPayee() { return estPaye; }
    public String getEvenement() { return evenement; }
    public List<String> getHistorique() { return new ArrayList<>(historique); }

    // Setters avec validation
    public void setAdresseLivraison(String adresseLivraison) {
        if (modeLivraison == ModeLivraison.LIVRAISON &&
                (adresseLivraison == null || adresseLivraison.trim().isEmpty())) {
            throw new IllegalArgumentException("L'adresse de livraison est requise pour une livraison");
        }
        this.adresseLivraison = adresseLivraison;
        ajouterEvenementHistorique("Adresse de livraison modifiée: " + adresseLivraison);
        notifierObservateurs();
    }

    public void setLivreur(Livreur livreur) {
        if (modeLivraison == ModeLivraison.LIVRAISON && livreur == null) {
            throw new IllegalArgumentException("Le livreur ne peut pas être null pour une livraison");
        }
        this.livreur = livreur;
        ajouterEvenementHistorique("Livreur assigné: " + livreur.getNom());
        notifierObservateurs();
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
        ajouterEvenementHistorique("Commentaire ajouté: " + commentaires);
    }

    public void setEvenement(String evenement) {
        this.evenement = evenement;
        ajouterEvenementHistorique("Événement défini: " + evenement);
    }

    @Override
    public String toString() {
        return String.format("Commande{numero='%s', client=%s, etat=%s, total=%.2f€, mode=%s}",
                numeroCommande, client.getNom(), etat.getLibelle(), total, modeLivraison.getLibelle());
    }
}