package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

/**
 * Classe représentant une stratégie de paiement en espèces.
 * Implémente l'interface {@link StrategyPaiement} et gère le paiement en argent liquide.
 */
public class PaiementEspeces implements StrategyPaiement {
    private double montantFourni;

    /**
     * Constructeur permettant d'initialiser le montant fourni par le client.
     *
     * @param montantFourni le montant donné par le client pour effectuer le paiement.
     */
    public PaiementEspeces(double montantFourni) {
        this.montantFourni = montantFourni;
    }

    /**
     * Effectue un paiement en espèces et calcule la monnaie à rendre si nécessaire.
     *
     * @param montant le montant à payer.
     * @throws IllegalStateException si le montant fourni est insuffisant.
     */
    @Override
    public void payer(double montant) {
        if (montantFourni < montant) {
            throw new IllegalStateException("Montant insuffisant");
        }

        double monnaie = montantFourni - montant;
        System.out.println("Paiement en espèces");
        System.out.println("Montant à payer: " + montant + "€");
        System.out.println("Montant fourni: " + montantFourni + "€");
        System.out.println("Monnaie à rendre: " + monnaie + "€");
    }
}