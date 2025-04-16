package tn.fermista.models;



import java.util.List;

public class Veterinaire extends User{
    public Veterinaire() {
    }

    public Veterinaire(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations, Roles roles) {
        super(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations, roles);
    }

    public Veterinaire(String email, String password, String firstName, String number, String lastName, Roles roles) {
        super(email, password, firstName, number, lastName, roles);
    }

    public Veterinaire(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    public Veterinaire(String email, String password, String firstName, String lastName, String number) {
    }
}
