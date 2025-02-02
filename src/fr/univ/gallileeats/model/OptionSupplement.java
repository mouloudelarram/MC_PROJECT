//OptionSupplement.java
package fr.univ.gallileeats.model;

import java.util.ArrayList;
import java.util.List;

public class OptionSupplement extends PlatDecore {
    private String nomSupplement;
    private double prixSupplement;
    private boolean disponible;
    private int quantite;
    private String description;
    private TypeSupplement typeSupplement;

    public enum TypeSupplement {
        INGREDIENT("Ingrédient supplémentaire"),
        SAUCE("Sauce supplémentaire"),
        PORTION("Portion supplémentaire"),
        ACCOMPAGNEMENT("Accompagnement supplémentaire"),
        BOISSON("Boisson supplémentaire");

        private String libelle;

        TypeSupplement(String libelle) {
            this.libelle = libelle;
        }

        public String getLibelle() {
            return libelle;
        }
    }

    public OptionSupplement(MenuComponent plat, String nomSupplement,
                            double prixSupplement, TypeSupplement typeSupplement,
                            String description) {
        super(plat);
        this.nomSupplement = nomSupplement;
        this.prixSupplement = prixSupplement;
        this.typeSupplement = typeSupplement;
        this.description = description;
        this.disponible = true;
        this.quantite = 1;

        // Vérification de la validité du prix
        if (prixSupplement < 0) {
            throw new IllegalArgumentException("Le prix du supplément ne peut pas être négatif");
        }
    }

    @Override
    public double getPrix() {
        return plat.getPrix() + (prixSupplement * quantite);
    }

    @Override
    public void afficher() {
        plat.afficher();
        if (disponible) {
            System.out.printf("  + %s (%s) : %.2f€ x%d%n",
                    nomSupplement, typeSupplement.getLibelle(),
                    prixSupplement, quantite);
            if (description != null && !description.isEmpty()) {
                System.out.println("    " + description);
            }
        }
    }

    @Override
    public String getDescriptionDecoration() {
        return String.format("%s (%s)", nomSupplement, typeSupplement.getLibelle());
    }

    @Override
    public double getPrixSupplementaire() {
        return prixSupplement * quantite;
    }

    // Méthodes spécifiques aux suppléments
    public void augmenterQuantite() {
        if (!disponible) {
            throw new IllegalStateException("Ce supplément n'est pas disponible");
        }
        quantite++;
    }

    public void diminuerQuantite() {
        if (quantite > 1) {
            quantite--;
        }
    }

    public void setQuantite(int quantite) {
        if (quantite < 1) {
            throw new IllegalArgumentException("La quantité doit être au moins de 1");
        }
        this.quantite = quantite;
    }

    // Vérification de la compatibilité des suppléments
    public boolean estCompatibleAvec(MenuComponent autre) {
        if (autre instanceof OptionSupplement) {
            OptionSupplement autreOption = (OptionSupplement) autre;
            // Éviter les doublons du même type de supplément
            return !this.nomSupplement.equals(autreOption.getNomSupplement());
        }
        return true;
    }

    // Getters et Setters
    public String getNomSupplement() {
        return nomSupplement;
    }

    public double getPrixSupplement() {
        return prixSupplement;
    }

    public void setPrixSupplement(double prixSupplement) {
        if (prixSupplement < 0) {
            throw new IllegalArgumentException("Le prix du supplément ne peut pas être négatif");
        }
        this.prixSupplement = prixSupplement;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getQuantite() {
        return quantite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeSupplement getTypeSupplement() {
        return typeSupplement;
    }

    @Override
    public String toString() {
        return String.format("%s + %s (%s) - %.2f€ x%d %s",
                plat.getNom(), nomSupplement, typeSupplement.getLibelle(),
                prixSupplement, quantite, disponible ? "" : "[Non disponible]");
    }

    // Méthode utilitaire pour vérifier si le plat contient déjà un certain type de supplément
    public boolean contientTypeSupplement(TypeSupplement type) {
        if (this.typeSupplement == type) {
            return true;
        }
        if (plat instanceof OptionSupplement) {
            return ((OptionSupplement) plat).contientTypeSupplement(type);
        }
        return false;
    }

    // Méthode pour calculer le prix total des suppléments
    public double getPrixTotalSupplements() {
        double total = this.getPrixSupplementaire();
        if (plat instanceof OptionSupplement) {
            total += ((OptionSupplement) plat).getPrixTotalSupplements();
        }
        return total;
    }

    // Méthode pour obtenir la liste de tous les suppléments
    public List<OptionSupplement> getTousSupplements() {
        List<OptionSupplement> supplements = new ArrayList<>();
        supplements.add(this);
        if (plat instanceof OptionSupplement) {
            supplements.addAll(((OptionSupplement) plat).getTousSupplements());
        }
        return supplements;
    }
}