//Menu.java
package fr.univ.gallileeats.model;

import java.util.ArrayList;
import java.util.List;

public class Menu extends MenuComponent {
    private List<MenuComponent> elements;
    private String type;

    public Menu(String nom, String description, String type) {
        super(nom, description, 0);
        this.elements = new ArrayList<>();
        this.type = type;
    }

    public void ajouter(MenuComponent element) {
        elements.add(element);
    }

    public void supprimer(MenuComponent element) {
        elements.remove(element);
    }

    public MenuComponent getElement(int index) {
        return elements.get(index);
    }

    @Override
    public double getPrix() {
        return elements.stream()
                .mapToDouble(MenuComponent::getPrix)
                .sum();
    }

    @Override
    public void afficher() {
        System.out.println("\n=== " + nom + " (" + type + ") ===");
        System.out.println(description);
        System.out.println("Prix total: " + getPrix() + "€\n");
        elements.forEach(MenuComponent::afficher);
    }

    @Override
    public List<MenuComponent> getElements() {
        return new ArrayList<>(elements);
    }

    public String getType() {
        return type;
    }
}