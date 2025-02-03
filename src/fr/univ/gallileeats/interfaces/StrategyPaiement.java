// StrategyPaiement.java
package fr.univ.gallileeats.interfaces;

/**
 * Interface représentant une stratégie de paiement dans le modèle de conception Strategy.
 * Cette interface définit une méthode pour effectuer un paiement avec un montant donné.
 */
public interface StrategyPaiement {

    /**
     * Effectue un paiement du montant spécifié.
     *
     * @param montant le montant à payer.
     */
    void payer(double montant);
}