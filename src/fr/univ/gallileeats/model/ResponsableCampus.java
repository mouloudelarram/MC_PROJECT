package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponsableCampus extends Utilisateur implements Observateur {
    private String departement;
    private double budgetInitial;
    private double budgetDisponible;
    private List<Commande> commandesGroupees;
    private Map<String, Double> budgetParEvenement;
    private List<String> evenementsRecurrents;
    private Map<String, MenuBuffet> menusPredefinis;
    private int nombrePersonnesMax;
    private List<String> historiqueBudget;

    public ResponsableCampus(String id, String nom, String email, String motDePasse,
                             String departement, double budgetInitial) {
        super(id, nom, email, motDePasse);
        if (budgetInitial < 0) {
            throw new IllegalArgumentException("Le budget initial ne peut pas être négatif");
        }
        if (departement == null || departement.trim().isEmpty()) {
            throw new IllegalArgumentException("Le département est requis");
        }

        this.departement = departement;
        this.budgetInitial = budgetInitial;
        this.budgetDisponible = budgetInitial;
        this.commandesGroupees = new ArrayList<>();
        this.budgetParEvenement = new HashMap<>();
        this.evenementsRecurrents = new ArrayList<>();
        this.menusPredefinis = new HashMap<>();
        this.nombrePersonnesMax = 100;
        this.historiqueBudget = new ArrayList<>();
        ajouterAHistoriqueBudget("Budget initial: " + budgetInitial + "€");
    }

    public void creerCommandeGroupee(MenuBuffet menu, int nombrePersonnes, String evenement) {
        verifierParametresCommande(nombrePersonnes, evenement);
        double coutTotal = calculerCoutTotal(menu, nombrePersonnes);
        verifierBudgetDisponible(coutTotal);

        Commande commande = new Commande(this, menu, nombrePersonnes, Commande.ModeLivraison.SUR_PLACE);
        commande.setEvenement(evenement);

        enregistrerCommande(commande, coutTotal, evenement);
    }

    private void verifierParametresCommande(int nombrePersonnes, String evenement) {
        if (nombrePersonnes <= 0 || nombrePersonnes > nombrePersonnesMax) {
            throw new IllegalArgumentException(
                    "Nombre de personnes invalide (doit être entre 1 et " + nombrePersonnesMax + ")"
            );
        }
        if (evenement == null || evenement.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'événement est requis");
        }
    }

    private double calculerCoutTotal(MenuBuffet menu, int nombrePersonnes) {
        return menu.getPrix() * nombrePersonnes;
    }

    private void verifierBudgetDisponible(double coutTotal) {
        if (coutTotal > budgetDisponible) {
            throw new IllegalStateException(
                    String.format("Budget insuffisant (Disponible: %.2f€, Requis: %.2f€)",
                            budgetDisponible, coutTotal)
            );
        }
    }

    private void enregistrerCommande(Commande commande, double coutTotal, String evenement) {
        budgetDisponible -= coutTotal;
        budgetParEvenement.put(evenement, coutTotal);
        commandesGroupees.add(commande);
        commande.ajouterObservateur(this);
        ajouterAHistoriqueBudget(String.format(
                "Commande créée pour '%s' - Montant: %.2f€", evenement, coutTotal));
    }

    public void annulerCommandeGroupee(Commande commande) {
        verifierAnnulationPossible(commande);

        double montantRembourse = commande.getTotal();
        budgetDisponible += montantRembourse;
        budgetParEvenement.remove(commande.getEvenement());

        commande.changerEtat(EtatCommande.ANNULEE);
        commandesGroupees.remove(commande);

        ajouterAHistoriqueBudget(String.format(
                "Annulation de la commande pour '%s' - Remboursement: %.2f€",
                commande.getEvenement(), montantRembourse));
    }

    private void verifierAnnulationPossible(Commande commande) {
        if (!commandesGroupees.contains(commande)) {
            throw new IllegalArgumentException("Cette commande n'appartient pas à ce responsable");
        }
        if (commande.getEtat() == EtatCommande.LIVREE ||
                commande.getEtat() == EtatCommande.SERVIE) {
            throw new IllegalStateException("Impossible d'annuler une commande déjà livrée/servie");
        }
    }

    public void ajouterMenuPredefini(String nomMenu, MenuBuffet menu) {
        if (nomMenu == null || nomMenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du menu est requis");
        }
        menusPredefinis.put(nomMenu, menu);
    }

    public void ajouterEvenementRecurrent(String evenement) {
        if (evenement != null && !evenement.trim().isEmpty() &&
                !evenementsRecurrents.contains(evenement)) {
            evenementsRecurrents.add(evenement);
        }
    }

    public void demanderAugmentationBudget(double montant, String justification) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        if (justification == null || justification.trim().isEmpty()) {
            throw new IllegalArgumentException("Une justification est requise");
        }

        ajouterAHistoriqueBudget(String.format(
                "Demande d'augmentation de budget: %.2f€ - %s", montant, justification));
    }

    private void ajouterAHistoriqueBudget(String evenement) {
        historiqueBudget.add(new Date() + " - " + evenement);
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;
            if (commandesGroupees.contains(commande)) {
                String message = String.format(
                        "La commande pour l'événement '%s' est passée à l'état: %s",
                        commande.getEvenement(),
                        commande.getEtat().getLibelle()
                );
                ajouterNotification(message);
            }
        }
    }

    // Getters (avec copies défensives pour les collections)
    public String getDepartement() {
        return departement;
    }

    public double getBudgetInitial() {
        return budgetInitial;
    }

    public double getBudgetDisponible() {
        return budgetDisponible;
    }

    public List<Commande> getCommandesGroupees() {
        return new ArrayList<>(commandesGroupees);
    }

    public Map<String, Double> getBudgetParEvenement() {
        return new HashMap<>(budgetParEvenement);
    }

    public List<String> getEvenementsRecurrents() {
        return new ArrayList<>(evenementsRecurrents);
    }

    public Map<String, MenuBuffet> getMenusPredefinis() {
        return new HashMap<>(menusPredefinis);
    }

    public List<String> getHistoriqueBudget() {
        return new ArrayList<>(historiqueBudget);
    }

    public int getNombrePersonnesMax() {
        return nombrePersonnesMax;
    }

    // Méthodes de calcul
    public double getBudgetUtilise() {
        return budgetInitial - budgetDisponible;
    }

    public double getPourcentageBudgetUtilise() {
        return (getBudgetUtilise() / budgetInitial) * 100;
    }

    @Override
    public String getRole() {
        return "RESPONSABLE";
    }

    @Override
    public String toString() {
        return String.format(
                "ResponsableCampus{id='%s', nom='%s', departement='%s', budgetDisponible=%.2f€, commandes=%d}",
                getId(), getNom(), departement, budgetDisponible, commandesGroupees.size()
        );
    }
}