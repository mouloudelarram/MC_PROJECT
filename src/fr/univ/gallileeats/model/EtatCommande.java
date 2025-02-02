//EtatCommande.java
package fr.univ.gallileeats.model;

public enum EtatCommande {
    NOUVELLE("Nouvelle commande", "La commande vient d'être créée") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == EN_PREPARATION;
        }
    },
    EN_PREPARATION("En préparation", "La commande est en cours de préparation") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == PRETE;
        }
    },
    PRETE("Prête", "La commande est prête") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == EN_LIVRAISON || nouvelEtat == SERVIE;
        }
    },
    EN_LIVRAISON("En livraison", "La commande est en cours de livraison") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == LIVREE || nouvelEtat == ANNULEE;
        }
    },
    LIVREE("Livrée", "La commande a été livrée") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == ANNULEE;
        }
    },
    SERVIE("Servie", "La commande a été servie") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return nouvelEtat == ANNULEE;
        }
    },
    ANNULEE("Annulée", "La commande a été annulée") {
        @Override
        public boolean peutPasserA(EtatCommande nouvelEtat) {
            return false;
        }
    };

    private final String libelle;
    private final String description;

    EtatCommande(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public abstract boolean peutPasserA(EtatCommande nouvelEtat);

    public String getLibelle() { return libelle; }
    public String getDescription() { return description; }
}