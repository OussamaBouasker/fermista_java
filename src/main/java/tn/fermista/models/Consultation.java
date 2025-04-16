package tn.fermista.models;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

public class Consultation {
    private int id;
    private RapportMedical rapportMedical;  // One-to-One
    private Vache vache;                    // Many-to-One
    private String nom;
    private Date date;
    private Time heure;
    private String lieu;

    public Consultation(int id, RapportMedical rapportMedical, Vache vache, String nom, Date date, Time heure, String lieu) {
        this.id = id;
        this.rapportMedical = rapportMedical;
        this.vache = vache;
        this.nom = nom;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
    }

    public Consultation(RapportMedical rapportMedical, Vache vache, String nom, Date date, Time heure, String lieu) {
        this.rapportMedical = rapportMedical;
        this.vache = vache;
        this.nom = nom;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
    }

    public Consultation() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RapportMedical getRapportMedical() {
        return rapportMedical;
    }

    public void setRapportMedical(RapportMedical rapportMedical) {
        this.rapportMedical = rapportMedical;
    }

    public Vache getVache() {
        return vache;
    }

    public void setVache(Vache vache) {
        this.vache = vache;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getHeure() {
        return heure;
    }

    public void setHeure(Time heure) {
        this.heure = heure;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consultation that = (Consultation) o;
        return id == that.id && Objects.equals(rapportMedical, that.rapportMedical) && Objects.equals(vache, that.vache) && Objects.equals(nom, that.nom) && Objects.equals(date, that.date) && Objects.equals(heure, that.heure) && Objects.equals(lieu, that.lieu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rapportMedical, vache, nom, date, heure, lieu);
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", rapportMedical=" + rapportMedical +
                ", vache=" + vache +
                ", nom='" + nom + '\'' +
                ", date=" + date +
                ", heure=" + heure +
                ", lieu='" + lieu + '\'' +
                '}';
    }
}
