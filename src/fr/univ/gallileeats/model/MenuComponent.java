//MenuComponent.java
package fr.univ.gallileeats.model;

import java.util.List;

public abstract class MenuComponent {
    protected String nom;
    protected String description;
    protected double prix;

    public MenuComponent(String nom, String description, double prix) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
    }

    public abstract double getPrix();
    public abstract void afficher();
    public abstract List<MenuComponent> getElements();

    public String getNom() { return nom; }
    public String getDescription() { return description; }
}