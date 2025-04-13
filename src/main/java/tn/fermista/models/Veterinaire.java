package tn.fermista.models;

import java.util.List;

public class Veterinaire extends User{

    public Veterinaire() {
    }

    public Veterinaire(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations) {
        super(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations);
    }

    public Veterinaire(String email, String password, String firstName, String lastName, String number, String image) {
        super(email, password, firstName, lastName, number, image);
    }

    @Override
    public String toString() {
        return "Veterinaire{} " + super.toString();
    }
}
