package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

/**
 * Classe représentant une stratégie de paiement par carte bancaire.
 * Implémente l'interface {@link StrategyPaiement} en simulant un paiement par carte.
 */
public class PaiementCarteBancaire implements StrategyPaiement {
    private String numeroCarte;
    private String dateExpiration;
    private String cvv;

    /**
     * Constructeur permettant d'initialiser les informations de la carte bancaire.
     *
     * @param numeroCarte    le numéro de la carte bancaire.
     * @param dateExpiration la date d'expiration de la carte.
     * @param cvv            le code de sécurité de la carte.
     */
    public PaiementCarteBancaire(String numeroCarte, String dateExpiration, String cvv) {
        this.numeroCarte = numeroCarte;
        this.dateExpiration = dateExpiration;
        this.cvv = cvv;
    }

    /**
     * Effectue un paiement du montant spécifié en utilisant les informations de la carte bancaire.
     *
     * @param montant le montant à payer.
     */
    @Override
    public void payer(double montant) {
        // Simulation de paiement par carte bancaire
        System.out.println("Paiement par carte bancaire en cours...");
        System.out.println("Montant: " + montant + "€");
        System.out.println("Carte: XXXX XXXX XXXX " + numeroCarte.substring(numeroCarte.length() - 4));
        System.out.println("Paiement accepté!");
    }
}