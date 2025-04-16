package tn.fermista.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Vache {
    private int id,age;
    private String name,race,etat_medical;

    private List<Consultation> consultations = new ArrayList<>();

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }
    // Constructors

    public Vache() {}

    public Vache(int id) {
        this.id = id;
    }

    //chaima
    public Vache(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Vache(int age, String race, String etat_medical, String name) {
        this.age = age;
        this.race = race;
        this.etat_medical = etat_medical;
        this.name = name;
    }



    // Getters and Setters


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getEtat_medical() {
        return etat_medical;
    }

    public void setEtat_medical(String etat_medical) {
        this.etat_medical = etat_medical;
    }



    // Equals & HashCode


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vache vache = (Vache) o;
        return id == vache.id && age == vache.age && Objects.equals(name, vache.name) && Objects.equals(race, vache.race) && Objects.equals(etat_medical, vache.etat_medical) && Objects.equals(consultations, vache.consultations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, age, name, race, etat_medical,  consultations);
    }

    @Override
    public String toString() {
        return "Vache{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", race='" + race + '\'' +
                ", etat_medical='" + etat_medical + '\'' +
                ", consultations=" + consultations +
                '}';
    }
}
