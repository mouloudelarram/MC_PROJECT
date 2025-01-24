//Administrateur.java
package fr.univ.gallileeats.model;

public class Administrateur extends Utilisateur {
    private static Administrateur instance;
    private static final Object verrou = new Object();

    private Administrateur(String id, String nom, String email, String motDePasse) {
        super(id, nom, email, motDePasse);
    }

    public static Administrateur getInstance() {
        if (instance == null) {
            synchronized (verrou) {
                if (instance == null) {
                    instance = new Administrateur(
                            "ADMIN1",
                            "Admin Principal",
                            "admin@galilee.fr",
                            "admin123"
                    );
                }
            }
        }
        return instance;
    }

    @Override
    public String getRole() {
        return "ADMINISTRATEUR";
    }
}