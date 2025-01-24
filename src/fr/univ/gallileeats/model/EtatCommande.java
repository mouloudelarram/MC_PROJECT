//EtatCommande.java
package fr.univ.gallileeats.model;

public enum EtatCommande {
    NOUVELLE("Nouvelle commande", "La commande vient d'être créée"),
    EN_PREPARATION("En préparation", "La commande est en cours de préparation en cuisine"),
    PRETE("Prête", "La commande est prête à être livrée ou servie"),
    EN_LIVRAISON("En livraison", "La commande est en cours de livraison"),
    LIVREE("Livrée", "La commande a été livrée au client"),
    SERVIE("Servie", "La commande a été servie au client sur place"),
    ANNULEE("Annulée", "La commande a été annulée");

    private String libelle;
    private String description;

    EtatCommande(String libelle, String description) {
        this.libelle = libelle;
        this.description = description;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }
}