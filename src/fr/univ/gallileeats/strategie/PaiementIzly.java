package fr.univ.gallileeats.strategie;

import fr.univ.gallileeats.interfaces.StrategyPaiement;

/**
 * Classe représentant une stratégie de paiement via Izly.
 * Implémente l'interface {@link StrategyPaiement} et simule un paiement avec un compte Izly.
 */
public class PaiementIzly implements StrategyPaiement {
    private String numeroEtudiant;
    private String codePin;

    /**
     * Constructeur permettant d'initialiser les informations du compte Izly.
     *
     * @param numeroEtudiant le numéro étudiant associé au compte Izly.
     * @param codePin        le code PIN pour valider le paiement.
     */
    public PaiementIzly(String numeroEtudiant, String codePin) {
        this.numeroEtudiant = numeroEtudiant;
        this.codePin = codePin;
    }

    /**
     * Effectue un paiement du montant spécifié en utilisant le compte Izly.
     *
     * @param montant le montant à payer.
     */
    @Override
    public void payer(double montant) {
        // Simulation de paiement Izly
        System.out.println("Paiement par carte Izly en cours...");
        System.out.println("Numéro étudiant: " + numeroEtudiant);
        System.out.println("Montant: " + montant + "€");
        System.out.println("Paiement Izly accepté!");
    }
}