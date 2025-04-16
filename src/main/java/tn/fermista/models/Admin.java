package tn.fermista.models;
import java.util.List;

public class Admin extends User {
    public Admin() {
    }

    public Admin(String email, String password, String firstName, String number, String lastName, Roles roles) {
        super(email, password, firstName, number, lastName, roles);
    }

    public Admin(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations, Roles roles) {
        super(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations, roles);
    }

    public Admin(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    public Admin(String email, String password, String firstName, String lastName, String number) {
    }
}

