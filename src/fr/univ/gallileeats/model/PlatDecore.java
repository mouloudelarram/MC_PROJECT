//PlatDecore.java
package fr.univ.gallileeats.model;

import java.util.List;

public abstract class PlatDecore extends MenuComponent {
    protected MenuComponent plat;

    public PlatDecore(MenuComponent plat) {
        super(plat.getNom(), plat.getDescription(), plat.getPrix());
        this.plat = plat;
    }

    @Override
    public abstract double getPrix();

    @Override
    public void afficher() {
        plat.afficher();
    }

    @Override
    public List<MenuComponent> getElements() {
        return plat.getElements();
    }

    // Méthodes spécifiques au décorateur
    public MenuComponent getPlatDeBase() {
        return this.plat;
    }

    public void setPlatDeBase(MenuComponent plat) {
        this.plat = plat;
    }

    // Cette méthode permet de vérifier si un plat contient déjà un certain type de décoration
    protected boolean contientDecoration(Class<?> typeDecoration) {
        if (this.getClass().equals(typeDecoration)) {
            return true;
        }
        if (plat instanceof PlatDecore) {
            return ((PlatDecore) plat).contientDecoration(typeDecoration);
        }
        return false;
    }

    // Méthode utilitaire pour obtenir le nom complet avec toutes les décorations
    public String getNomComplet() {
        return plat.getNom() + " + " + this.getDescriptionDecoration();
    }

    // Méthode abstraite que chaque décorateur concret doit implémenter
    protected abstract String getDescriptionDecoration();

    // Méthode pour obtenir le supplément de prix ajouté par ce décorateur
    protected abstract double getPrixSupplementaire();
}