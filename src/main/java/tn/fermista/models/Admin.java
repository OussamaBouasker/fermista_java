package tn.fermista.models;

import java.util.List;

public class Admin extends User{

    public Admin(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations) {
        super(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations);
    }

    public Admin() {
    }

    public Admin(String email, String password, String firstName, String lastName, String number, String image) {
        super(email, password, firstName, lastName, number, image);
    }

    @Override
    public String toString() {
        return "Admin{} " + super.toString();
    }

}
