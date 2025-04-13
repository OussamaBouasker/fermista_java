package tn.fermista.models;

import java.util.Objects;

public class RapportMedical {
    private int id;
    private int num;
    private String race;
    private String historiqueDeMaladie;
    private String casMedical;
    private String solution;
    public RapportMedical() {}

    public RapportMedical(int id, int num, String race, String historiqueDeMaladie, String casMedical, String solution) {
        this.id = id;
        this.num = num;
        this.race = race;
        this.historiqueDeMaladie = historiqueDeMaladie;
        this.casMedical = casMedical;
        this.solution = solution;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getHistoriqueDeMaladie() {
        return historiqueDeMaladie;
    }

    public void setHistoriqueDeMaladie(String historiqueDeMaladie) {
        this.historiqueDeMaladie = historiqueDeMaladie;
    }

    public String getCasMedical() {
        return casMedical;
    }

    public void setCasMedical(String casMedical) {
        this.casMedical = casMedical;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RapportMedical that = (RapportMedical) o;
        return id == that.id && num == that.num && Objects.equals(race, that.race) && Objects.equals(historiqueDeMaladie, that.historiqueDeMaladie) && Objects.equals(casMedical, that.casMedical) && Objects.equals(solution, that.solution);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, num, race, historiqueDeMaladie, casMedical, solution);
    }

    @Override
    public String toString() {
        return "RapportMedical{" +
                "id=" + id +
                ", num=" + num +
                ", race='" + race + '\'' +
                ", historiqueDeMaladie='" + historiqueDeMaladie + '\'' +
                ", casMedical='" + casMedical + '\'' +
                ", solution='" + solution + '\'' +
                '}';
    }


}
