//Plat.java
package fr.univ.gallileeats.model;

import java.util.Collections;
import java.util.List;

public class Plat extends MenuComponent {
    private String categorie;
    private boolean disponible;

    public Plat(String nom, String description, double prix, String categorie) {
        super(nom, description, prix);
        this.categorie = categorie;
        this.disponible = true;
    }

    @Override
    public double getPrix() {
        return prix;
    }

    @Override
    public void afficher() {
        System.out.println("- " + nom + " (" + categorie + ") : " + prix + "€");
        System.out.println("  " + description);
        if (!disponible) {
            System.out.println("  [Non disponible]");
        }
    }

    @Override
    public List<MenuComponent> getElements() {
        return Collections.singletonList(this);
    }

    public String getCategorie() {
        return categorie;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}