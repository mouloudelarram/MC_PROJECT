//PaiementIzly.java
package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

public class PaiementIzly implements StrategyPaiement {
    private String numeroEtudiant;
    private String codePin;

    public PaiementIzly(String numeroEtudiant, String codePin) {
        this.numeroEtudiant = numeroEtudiant;
        this.codePin = codePin;
    }

    @Override
    public void payer(double montant) {
        // Simulation de paiement Izly
        System.out.println("Paiement par carte Izly en cours...");
        System.out.println("Numéro étudiant: " + numeroEtudiant);
        System.out.println("Montant: " + montant + "€");
        System.out.println("Paiement Izly accepté!");
    }
}