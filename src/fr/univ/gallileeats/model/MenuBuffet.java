//MenuBuffet.java
package fr.univ.gallileeats.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBuffet extends Menu {
    private int nombrePersonnes;
    private double prixParPersonne;
    private Map<String, Integer> minParCategorie; // Minimum de plats par catégorie
    private boolean estComplet;

    public MenuBuffet(String nom, String description, int nombrePersonnes) {
        super(nom, description, "BUFFET");
        this.nombrePersonnes = nombrePersonnes;
        this.prixParPersonne = 0.0;
        this.minParCategorie = new HashMap<>();
        this.estComplet = false;
        initialiserReglesBuffet();
    }

    private void initialiserReglesBuffet() {
        // Définition des minimums requis par catégorie pour un buffet
        minParCategorie.put("ENTREE", 2);
        minParCategorie.put("PLAT", 3);
        minParCategorie.put("DESSERT", 2);
        minParCategorie.put("BOISSON", 2);
    }

    @Override
    public void ajouter(MenuComponent plat) {
        super.ajouter(plat);
        verifierCompletude();
        calculerPrixParPersonne();
    }

    @Override
    public void supprimer(MenuComponent plat) {
        super.supprimer(plat);
        verifierCompletude();
        calculerPrixParPersonne();
    }

    private void verifierCompletude() {
        Map<String, Integer> compteurCategories = new HashMap<>();

        // Compter les plats par catégorie
        for (MenuComponent component : getElements()) {
            if (component instanceof Plat) {
                Plat plat = (Plat) component;
                String categorie = plat.getCategorie().toUpperCase();
                compteurCategories.merge(categorie, 1, Integer::sum);
            }
        }

        // Vérifier si toutes les catégories ont le minimum requis
        estComplet = minParCategorie.entrySet().stream()
                .allMatch(entry -> {
                    Integer count = compteurCategories.getOrDefault(entry.getKey(), 0);
                    return count >= entry.getValue();
                });
    }

    private void calculerPrixParPersonne() {
        double totalPrix = getElements().stream()
                .mapToDouble(MenuComponent::getPrix)
                .sum();

        // Ajout d'une réduction si le buffet est complet
        if (estComplet && nombrePersonnes >= 20) {
            totalPrix *= 0.9; // 10% de réduction pour les grands groupes
        }

        this.prixParPersonne = totalPrix / nombrePersonnes;
    }

    @Override
    public double getPrix() {
        return prixParPersonne * nombrePersonnes;
    }

    @Override
    public void afficher() {
        System.out.println("\n=== " + getNom() + " ===");
        System.out.println("Description: " + getDescription());
        System.out.println("Nombre de personnes: " + nombrePersonnes);
        System.out.println("Prix par personne: " + String.format("%.2f€", prixParPersonne));
        System.out.println("Prix total: " + String.format("%.2f€", getPrix()));
        System.out.println("Statut: " + (estComplet ? "Complet" : "Incomplet"));

        System.out.println("\nPlats proposés par catégorie:");
        Map<String, List<Plat>> platsParCategorie = new HashMap<>();

        // Grouper les plats par catégorie
        for (MenuComponent component : getElements()) {
            if (component instanceof Plat) {
                Plat plat = (Plat) component;
                platsParCategorie
                        .computeIfAbsent(plat.getCategorie(), k -> new ArrayList<>())
                        .add(plat);
            }
        }

        // Afficher les plats par catégorie
        platsParCategorie.forEach((categorie, plats) -> {
            System.out.println("\n" + categorie + ":");
            plats.forEach(plat -> {
                System.out.println("  - " + plat.getNom() +
                        " (" + String.format("%.2f€", plat.getPrix()) + ")");
                if (plat.getDescription() != null && !plat.getDescription().isEmpty()) {
                    System.out.println("    " + plat.getDescription());
                }
            });
        });

        // Afficher les minimums requis non atteints
        System.out.println("\nMinimums requis par catégorie:");
        minParCategorie.forEach((categorie, minimum) -> {
            int actuel = platsParCategorie.getOrDefault(categorie, new ArrayList<>()).size();
            System.out.println(categorie + ": " + actuel + "/" + minimum +
                    (actuel < minimum ? " (Minimum non atteint)" : ""));
        });
    }

    // Getters et setters spécifiques
    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        if (nombrePersonnes <= 0) {
            throw new IllegalArgumentException("Le nombre de personnes doit être positif");
        }
        this.nombrePersonnes = nombrePersonnes;
        calculerPrixParPersonne();
    }

    public double getPrixParPersonne() {
        return prixParPersonne;
    }

    public boolean estComplet() {
        return estComplet;
    }

    public Map<String, Integer> getMinParCategorie() {
        return new HashMap<>(minParCategorie);
    }

    // Méthodes utilitaires supplémentaires
    public boolean ajouterMinimumCategorie(String categorie, int minimum) {
        if (minimum <= 0) {
            return false;
        }
        minParCategorie.put(categorie.toUpperCase(), minimum);
        verifierCompletude();
        return true;
    }

    public void appliquerReductionGroupe(double pourcentage) {
        if (pourcentage < 0 || pourcentage > 100) {
            throw new IllegalArgumentException("Le pourcentage doit être entre 0 et 100");
        }
        this.prixParPersonne *= (1 - pourcentage / 100);
    }
}