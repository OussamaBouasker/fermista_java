package tn.fermista.models;

import java.util.Objects;
// Constructors

public class Collier {
    private int id;
    private String reference;
    private String taille;
    private double valeur_Temperature;
    private double valeur_Agitation;
    private Vache vache;

    public Collier() {
    }


    public Collier(String reference, String taille, double valeur_Temperature, double valeur_Agitation, Vache vache) {
        this.reference = reference;
        this.taille = taille;
        this.valeur_Temperature = valeur_Temperature;
        this.valeur_Agitation = valeur_Agitation;
        this.vache = vache;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getTaille() {
        return taille;
    }

    public void setTaille(String taille) {
        this.taille = taille;
    }

    public double getValeurTemperature() {
        return valeur_Temperature;
    }

    public void setValeurTemperature(double valeur_Temperature) {
        this.valeur_Temperature = valeur_Temperature;
    }

    public double getValeurAgitation() {
        return valeur_Agitation;
    }

    public void setValeurAgitation(double valeur_Agitation) {
        this.valeur_Agitation = valeur_Agitation;
    }

    public Vache getVache() {
        return vache;
    }

    public void setVache(Vache vache) {
        this.vache = vache;
    }

    // Equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collier)) return false;
        Collier collier = (Collier) o;
        return id == collier.id &&
                Double.compare(collier.valeur_Temperature, valeur_Temperature) == 0 &&
                Double.compare(collier.valeur_Agitation, valeur_Agitation) == 0 &&
                Objects.equals(reference, collier.reference) &&
                Objects.equals(taille, collier.taille) &&
                Objects.equals(vache, collier.vache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, taille, valeur_Temperature, valeur_Agitation, vache);
    }

    @Override
    public String toString() {
        return "Collier{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", taille='" + taille + '\'' +
                ", valeurTemperature=" + valeur_Temperature +
                ", valeurAgitation=" + valeur_Agitation +
                ", vache=" + vache +
                '}';
    }
}