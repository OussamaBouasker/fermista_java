package tn.fermista.models;

import java.time.LocalDate;

public class Commande {
    private int id;
    private LocalDate date;
    private double total;
    private String status;
    private int livraisonId;
    
    // Nouveaux champs pour le formulaire de commande
    private String nomClient;
    private String email;
    private String telephone;
    private String adresse;
    private String notes;

    public Commande() {
        // constructeur vide
    }

    public Commande(int id, LocalDate date, double total, String status, int livraisonId) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.status = status;
        this.livraisonId = livraisonId;
    }
    
    // Constructeur complet avec les nouveaux champs
    public Commande(int id, LocalDate date, double total, String status, int livraisonId, 
                   String nomClient, String email, String telephone, String adresse, String notes) {
        this.id = id;
        this.date = date;
        this.total = total;
        this.status = status;
        this.livraisonId = livraisonId;
        this.nomClient = nomClient;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLivraisonId() {
        return livraisonId;
    }

    public void setLivraisonId(int livraisonId) {
        this.livraisonId = livraisonId;
    }
    
    // Getters et setters pour les nouveaux champs
    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", date=" + date +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", livraisonId=" + livraisonId +
                ", nomClient='" + nomClient + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
