//PaiementCarteBancaire.java
package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

public class PaiementCarteBancaire implements StrategyPaiement {
    private String numeroCarte;
    private String dateExpiration;
    private String cvv;

    public PaiementCarteBancaire(String numeroCarte, String dateExpiration, String cvv) {
        this.numeroCarte = numeroCarte;
        this.dateExpiration = dateExpiration;
        this.cvv = cvv;
    }

    @Override
    public void payer(double montant) {
        // Simulation de paiement par carte bancaire
        System.out.println("Paiement par carte bancaire en cours...");
        System.out.println("Montant: " + montant + "€");
        System.out.println("Carte: XXXX XXXX XXXX " + numeroCarte.substring(numeroCarte.length() - 4));
        System.out.println("Paiement accepté!");
    }
}