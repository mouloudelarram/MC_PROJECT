package fr.univ.gallileeats.controleur;

import fr.univ.gallileeats.interfaces.IControleur;
import fr.univ.gallileeats.model.*;
import fr.univ.gallileeats.vue.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ControleurPrincipal extends AbstractControleur {
    private Map<String, IControleur> sousControleurs;
    private Map<String, Utilisateur> utilisateursConnectes;
    private static final String DEMO_PASSWORD = "password123";

    public ControleurPrincipal() {
        super();
        this.sousControleurs = new HashMap<>();
        this.utilisateursConnectes = new HashMap<>();
        initialiserSousControleurs();
    }

    @Override
    protected void initialiserActionHandlers() {
        ajouterHandler("MENU_PRINCIPAL_1", params -> connecterClient());
        ajouterHandler("MENU_PRINCIPAL_2", params -> connecterLivreur());
        ajouterHandler("MENU_PRINCIPAL_3", params -> connecterResponsable());
        ajouterHandler("MENU_PRINCIPAL_4", params -> connecterAdmin());
        ajouterHandler("MENU_PRINCIPAL_5", params -> quitterApplication());
    }

    private void initialiserSousControleurs() {
        sousControleurs.put("CLIENT", new ControleurClient(this));
        sousControleurs.put("LIVREUR", new ControleurLivreur(this));
        sousControleurs.put("ADMIN", new ControleurAdmin(this));
        sousControleurs.put("RESPONSABLE", new ControleurResponsable(this));
    }

    @Override
    public void traiterAction(String action) {
        if (action.startsWith("MENU_PRINCIPAL_")) {
            Consumer<String[]> handler = actionHandlers.get(action);
            if (handler != null) {
                handler.accept(new String[]{});
            } else {
                System.out.println("Action non reconnue : " + action);
                afficherVuePrincipale();
            }
        } else {
            String[] parts = action.split("_", 2);
            if (parts.length >= 2) {
                IControleur sousControleur = sousControleurs.get(parts[0]);
                if (sousControleur != null) {
                    sousControleur.traiterAction(parts[1]);
                }
            }
        }
    }

    @Override
    public void afficherVuePrincipale() {
        VuePrincipale vuePrincipale = new VuePrincipale(this);
        setVue(vuePrincipale);
        vue.afficher();
    }

    @Override
    public void gererCommandes() {
        Utilisateur utilisateur = getUtilisateurConnecteActuel();
        if (utilisateur != null) {
            IControleur controleur = sousControleurs.get(utilisateur.getRole());
            if (controleur != null) {
                controleur.gererCommandes();
            }
        }
    }

    @Override
    public void afficherStatistiques() {
        Utilisateur utilisateur = getUtilisateurConnecteActuel();
        if (utilisateur != null) {
            IControleur controleur = sousControleurs.get(utilisateur.getRole());
            if (controleur != null) {
                controleur.afficherStatistiques();
            }
        }
    }

    @Override
    public void afficherEtatProfil() {
        Utilisateur utilisateur = getUtilisateurConnecteActuel();
        if (utilisateur != null) {
            IControleur controleur = sousControleurs.get(utilisateur.getRole());
            if (controleur != null) {
                controleur.afficherEtatProfil();
            }
        }
    }

    @Override
    public void afficherFormulairePaiement() {
        Client client = (Client) getUtilisateurConnecte("CLIENT");
        if (client != null) {
            sousControleurs.get("CLIENT").afficherFormulairePaiement();
        }
    }

    @Override
    public void retourMenuPrincipal() {
        afficherVuePrincipale();
    }

    // Méthodes de connexion
    private void connecterClient() {
        // Création d'un client de démonstration
        Client client = new Client(
                "CLI1",
                "Jean Dupont",
                "jean@galilee.fr",
                DEMO_PASSWORD,
                "123 rue de Paris"
        );
        client.activerStatutEtudiant("20240001");
        utilisateursConnectes.put("CLIENT", client);

        // Création et affichage de la vue
        VueClient vueClient = new VueClient(sousControleurs.get("CLIENT"), client);
        sousControleurs.get("CLIENT").setVue(vueClient);
        vueClient.afficher();
    }

    private void connecterLivreur() {
        Livreur livreur = new Livreur(
                "LIV1",
                "Pierre Martin",
                "pierre@galilee.fr",
                DEMO_PASSWORD,
                "Vélo",
                "Zone Nord"
        );
        utilisateursConnectes.put("LIVREUR", livreur);

        VueLivreur vueLivreur = new VueLivreur(sousControleurs.get("LIVREUR"), livreur);
        sousControleurs.get("LIVREUR").setVue(vueLivreur);
        vueLivreur.afficher();
    }

    private void connecterResponsable() {
        ResponsableCampus responsable = new ResponsableCampus(
                "RES1",
                "Marie Durand",
                "marie@galilee.fr",
                DEMO_PASSWORD,
                "Informatique",
                5000.0
        );
        utilisateursConnectes.put("RESPONSABLE", responsable);

        VueResponsableCampus vueResponsable = new VueResponsableCampus(sousControleurs.get("RESPONSABLE"), responsable);
        sousControleurs.get("RESPONSABLE").setVue(vueResponsable);
        vueResponsable.afficher();
    }

    private void connecterAdmin() {
        Administrateur admin = Administrateur.getInstance();
        utilisateursConnectes.put("ADMIN", admin);

        VueAdmin vueAdmin = new VueAdmin(sousControleurs.get("ADMIN"));
        sousControleurs.get("ADMIN").setVue(vueAdmin);
        vueAdmin.afficher();
    }

    // Méthodes de gestion de la session
    public void deconnecter() {
        utilisateursConnectes.clear();
        afficherVuePrincipale();
    }

    private void quitterApplication() {
        System.out.println("\nMerci d'avoir utilisé GALILEE EATS. À bientôt !");
        System.exit(0);
    }

    // Méthodes utilitaires
    public Utilisateur getUtilisateurConnecte(String type) {
        return utilisateursConnectes.get(type);
    }

    private Utilisateur getUtilisateurConnecteActuel() {
        for (Utilisateur utilisateur : utilisateursConnectes.values()) {
            if (utilisateur != null) {
                return utilisateur;
            }
        }
        return null;
    }

    // Méthodes de validation
    private void verifierConnexion(String email, String motDePasse) {
        if (email == null || email.trim().isEmpty() ||
                motDePasse == null || motDePasse.trim().isEmpty()) {
            throw new IllegalArgumentException("Email et mot de passe requis");
        }
    }
}