package tn.fermista.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Agriculteur extends User{

    private List<RendezVous> rendezVousList = new ArrayList<>(); // One-to-Many

    public Agriculteur() {
    }

    public Agriculteur(Integer id) {
        super(id);
    }

    public Agriculteur(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations, List<RendezVous> rendezVousList) {
        super(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations);
        this.rendezVousList = rendezVousList;
    }

    public Agriculteur(String email, String password, String firstName, String lastName, String number, String image) {
        super(email, password, firstName, lastName, number, image);
    }

    public List<RendezVous> getRendezVousList() {
        return rendezVousList;
    }

    public void setRendezVousList(List<RendezVous> rendezVousList) {
        this.rendezVousList = rendezVousList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Agriculteur that = (Agriculteur) o;
        return Objects.equals(rendezVousList, that.rendezVousList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), rendezVousList);
    }

    @Override
    public String toString() {
        return "Agriculteur{" +
                "rendezVousList=" + rendezVousList +
                "} " + super.toString();
    }
}
