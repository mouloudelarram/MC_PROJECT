package fr.univ.gallileeats.model;

import fr.univ.gallileeats.interfaces.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.stream.Collectors;

public class Cuisinier extends Utilisateur implements Observateur {
    private List<Commande> commandesEnCours;
    private List<Commande> historiqueCommandes;
    private String specialite;
    private boolean disponible;
    private int tempsPreparationMoyen;
    private double tauxSatisfaction;
    private int commandesPrepareesDuJour;

    public Cuisinier(String id, String nom, String email, String motDePasse, String specialite) {
        super(id, nom, email, motDePasse);
        this.commandesEnCours = new ArrayList<>();
        this.historiqueCommandes = new ArrayList<>();
        this.specialite = specialite;
        this.disponible = true;
        this.tempsPreparationMoyen = 0;
        this.tauxSatisfaction = 100.0;
        this.commandesPrepareesDuJour = 0;
    }

    @Override
    public void actualiser(Object source) {
        if (source instanceof Commande) {
            Commande commande = (Commande) source;

            // Le cuisinier observe les nouvelles commandes et celles en préparation
            if (commande.getEtat() == EtatCommande.NOUVELLE ||
                    commande.getEtat() == EtatCommande.EN_PREPARATION) {
                if (!commandesEnCours.contains(commande)) {
                    commandesEnCours.add(commande);
                    String message = String.format(
                            "Nouvelle commande à préparer : %s\nClient : %s",
                            commande.getNumeroCommande(),
                            commande.getClient().getNom()
                    );
                    this.ajouterNotification(message);
                }
            } else if (commande.getEtat() == EtatCommande.PRETE) {
                commandesEnCours.remove(commande);
                historiqueCommandes.add(commande);
                commandesPrepareesDuJour++;
            }
        }
    }

    public List<Commande> getCommandesEnAttente() {
        return commandesEnCours.stream()
                .filter(c -> c.getEtat() == EtatCommande.NOUVELLE ||
                        c.getEtat() == EtatCommande.EN_PREPARATION)
                .collect(Collectors.toList());
    }

    public List<Commande> getCommandesEnPreparation() {
        return commandesEnCours.stream()
                .filter(c -> c.getEtat() == EtatCommande.EN_PREPARATION)
                .collect(Collectors.toList());
    }

    private void calculerTempsPreparationMoyen(Commande commande) {
        if (historiqueCommandes.isEmpty()) {
            tempsPreparationMoyen = 0;
            return;
        }

        long tempsTotal = 0;
        int nombreCommandes = 0;

        for (Commande cmd : historiqueCommandes) {
            if (cmd.getDateCommande() != null) {
                long duree = new Date().getTime() - cmd.getDateCommande().getTime();
                tempsTotal += duree / (1000 * 60); // Conversion en minutes
                nombreCommandes++;
            }
        }

        if (nombreCommandes > 0) {
            tempsPreparationMoyen = (int) (tempsTotal / nombreCommandes);
        }
    }

    public void ajouterEvaluation(int note) {
        if (note < 1 || note > 5) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        // Mise à jour du taux de satisfaction (moyenne pondérée)
        tauxSatisfaction = (tauxSatisfaction * historiqueCommandes.size() + note * 20) /
                (historiqueCommandes.size() + 1);
    }

    // Getters
    public String getSpecialite() {
        return specialite;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public int getTempsPreparationMoyen() {
        return tempsPreparationMoyen;
    }

    public double getTauxSatisfaction() {
        return tauxSatisfaction;
    }

    public int getCommandesPrepareesDuJour() {
        return commandesPrepareesDuJour;
    }

    public List<Commande> getHistoriqueCommandes() {
        return new ArrayList<>(historiqueCommandes);
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String getRole() {
        return "CUISINIER";
    }

    @Override
    public String toString() {
        return String.format("Cuisinier{id='%s', nom='%s', specialite='%s', disponible=%s}",
                getId(), getNom(), specialite, disponible);
    }
}