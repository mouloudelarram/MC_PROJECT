package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Sujet;
import fr.univ.gallileeats.interfaces.Observateur;
import fr.univ.gallileeats.interfaces.StrategyPaiement;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Commande implements Sujet {
    // Compteur statique pour générer les numéros de commande

    private List<String> reductionsAppliquees;
    private double totalAvantReductions;
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
        this.etat = null;
        this.observateurs = new ArrayList<>();
        this.dateCommande = new Date();
        this.historique = new ArrayList<>();
        this.estPaye = false;
        this.reductionsAppliquees = new ArrayList<>();
        this.totalAvantReductions = 0.0;

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

            // Changement d'état et notification des cuisiniers
            if (this.etat == EtatCommande.NOUVELLE) {
                changerEtat(EtatCommande.EN_PREPARATION);
                // Les cuisiniers seront notifiés via le changement d'état
                notifierObservateurs();
            }

            ajouterEvenementHistorique("Paiement effectué avec succès");
        } catch (Exception e) {
            ajouterEvenementHistorique("Échec du paiement: " + e.getMessage());
            throw new IllegalStateException("Échec du paiement: " + e.getMessage());
        }
    }

    // Calcul du total
    public void calculerTotal() {
        // Prix de base selon le menu et le nombre de personnes
        this.total = 0.0;

        // Ajouter le prix de chaque élément du menu
        for (MenuComponent element : menu.getElements()) {
            this.total += element.getPrix();
        }

        // Multiplier par le nombre de personnes
        this.total *= nombrePersonnes;

        // Réductions selon le type de client et la taille de la commande
        appliquerReductions();

        // Frais supplémentaires selon le mode de livraison
        appliquerFraisLivraison();

        notifierObservateurs();
    }

    private void appliquerReductions() {
        this.totalAvantReductions = this.total;

        // Réduction pour les grandes commandes
        if (nombrePersonnes >= 20) {
            total *= 0.9; // 10% de réduction
            reductionsAppliquees.add("Réduction groupe (-10%)");
        }

        // Réduction étudiant
        if (client instanceof Client && ((Client) client).estEtudiant()) {
            total *= 0.85; // 15% de réduction
            reductionsAppliquees.add("Réduction étudiant (-15%)");
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
    @Override
    public void changerEtat(EtatCommande nouvelEtat) {
        if (nouvelEtat == null) {
            throw new IllegalArgumentException("Le nouvel état ne peut pas être null");
        }

        if (this.etat == null) {
            // Premier changement d'état
            if (nouvelEtat != EtatCommande.NOUVELLE) {
                throw new IllegalStateException("La première état doit être NOUVELLE");
            }
            this.etat = nouvelEtat;
            ajouterEvenementHistorique("État initial: " + nouvelEtat.getLibelle());
            notifierCuisiniers();
            return;
        }

        if (!this.etat.peutPasserA(nouvelEtat)) {
            throw new IllegalStateException(
                    String.format("Transition invalide: %s vers %s",
                            this.etat.getLibelle(), nouvelEtat.getLibelle())
            );
        }

        validerTransition(nouvelEtat);
        this.etat = nouvelEtat;
        ajouterEvenementHistorique("État changé: " + nouvelEtat.getLibelle());
        notifierSelonEtat(nouvelEtat);
    }

    private void validerTransition(EtatCommande nouvelEtat) {
        if (nouvelEtat == EtatCommande.EN_PREPARATION && !estPaye) {
            throw new IllegalStateException("La commande doit être payée");
        }
        if (nouvelEtat == EtatCommande.EN_LIVRAISON &&
                modeLivraison == ModeLivraison.LIVRAISON &&
                livreur == null) {
            throw new IllegalStateException("Un livreur doit être assigné");
        }
    }
    private void notifierSelonEtat(EtatCommande nouvelEtat) {
        switch (nouvelEtat) {
            case NOUVELLE:
                notifierCuisiniers();
                break;
            case PRETE:
                if (modeLivraison == ModeLivraison.LIVRAISON) {
                    notifierLivreursDisponibles();
                }
                break;
            default:
                notifierObservateurs();
        }
    }

    private void notifierCuisiniers() {
        for (Observateur obs : observateurs) {
            if (obs instanceof Cuisinier) {
                obs.actualiser(this);
            }
        }
    }

    private void notifierLivreursDisponibles() {
        for (Observateur obs : observateurs) {
            if (obs instanceof Livreur && ((Livreur) obs).isDisponible()) {
                obs.actualiser(this);
            }
        }
    }


    private void gererChangementEtat(EtatCommande ancienEtat, EtatCommande nouvelEtat) {
        switch (nouvelEtat) {
            case EN_PREPARATION:
                verifierPaiement();
                notifierObservateurs(); // Notifier les cuisiniers
                break;
            case PRETE:
                if (modeLivraison == ModeLivraison.LIVRAISON) {
                    notifierObservateurs(); // Notifier les livreurs disponibles
                }
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
                return nouvelEtat == EtatCommande.EN_PREPARATION;
            case EN_PREPARATION:
                return nouvelEtat == EtatCommande.PRETE;
            case PRETE:
                return nouvelEtat == EtatCommande.EN_LIVRAISON || nouvelEtat == EtatCommande.SERVIE;
            case EN_LIVRAISON:
                return nouvelEtat == EtatCommande.LIVREE;
            default:
                return nouvelEtat == EtatCommande.ANNULEE;
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
    public double getTotalAvantReductions() {
        return totalAvantReductions;
    }

    public List<String> getReductionsAppliquees() {
        return new ArrayList<>(reductionsAppliquees);
    }
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
    private double getReductionPourcentage() {
        if (this.client instanceof Client && ((Client) this.client).estEtudiant()) {
            return 0.15; // 15% de réduction étudiant
        }
        return 0.0;
    }

    // Setters avec validation
    public void setTotal(double total) {
        this.total = total;
        this.totalAvantReductions = total / (1 - getReductionPourcentage());
    }
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