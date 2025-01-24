//PaiementEspeces.java
package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

public class PaiementEspeces implements StrategyPaiement {
    private double montantFourni;

    public PaiementEspeces(double montantFourni) {
        this.montantFourni = montantFourni;
    }

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