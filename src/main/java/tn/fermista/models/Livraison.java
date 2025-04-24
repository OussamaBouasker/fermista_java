package tn.fermista.models;

import java.util.Date;

public class Livraison {
    private int id;
    private Date date;
    private String livreur;
    private String lieu;

    public Livraison(int id, Date date, String livreur, String lieu) {
        this.id = id;
        this.date = date;
        this.livreur = livreur;
        this.lieu = lieu;
    }

    public Livraison() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLivreur() {
        return livreur;
    }

    public void setLivreur(String livreur) {
        this.livreur = livreur;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public String toString() {
        return "Livraison{" +
                "id=" + id +
                ", date=" + date +
                ", livreur='" + livreur + '\'' +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
