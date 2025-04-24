package tn.fermista.models;

import java.util.Date;

public class Commande {
    private int id;
    private Date date_commander;
    private String statut;
    private int montant_total;
    private Livraison livcom_id;

    public Commande() {
        // constructeur vide
    }
    public Commande(int id) {
        this.id = id;

    }

    public Commande(int id, Date date_commander, String statut, int montant_total, Livraison livcom_id) {
        this.id = id;
        this.date_commander = date_commander;
        this.statut = statut;
        this.montant_total = montant_total;
        this.livcom_id = livcom_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate_commander() {
        return date_commander;
    }

    public void setDate_commander(Date date_commander) {
        this.date_commander = date_commander;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getMontant_total() {
        return montant_total;
    }

    public void setMontant_total(int montant_total) {
        this.montant_total = montant_total;
    }

    public Livraison getLivcom_id() {
        return livcom_id;
    }

    public void setLivcom_id(Livraison livcom_id) {
        this.livcom_id = livcom_id;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", date_commander=" + date_commander +
                ", statut='" + statut + '\'' +
                ", montant_total=" + montant_total +
                ", livcom_id=" + livcom_id +
                '}';
    }
}
