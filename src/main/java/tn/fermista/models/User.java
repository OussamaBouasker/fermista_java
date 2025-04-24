package tn.fermista.models;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class User {

    private Integer id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String number;
    private boolean state;
    private boolean isVerified;
    private String image;
    private List<Reclamation> reclamations;
    private Roles roles;
    private Set<Reservation> reservations;
    private Set<Workshop> workshops;



    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User() {
    }

    public User( String email, String password, String firstName, String lastName) {

        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String password, String firstName, String number, String lastName, Roles roles) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.number = number;
        this.lastName = lastName;
        this.roles = roles;
    }

    public User(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations, Roles roles) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.state = state;
        this.isVerified = isVerified;
        this.image = image;
        this.reclamations = reclamations;
        this.roles = roles;
    }

    public User(int userId) {
    }

    public User(String email, String password, String firstName, String lastName, String number, String image) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.number = number;
        this.image = image;
    }

    public User(Integer id, String email, String password, String firstName, String lastName, String number, Boolean state, boolean isVerified, String image, List<Reclamation> reclamations) {
    }


    public Integer getId() {
        return id;
    }

    public Set<Workshop> getWorkshops() {
        return workshops;
    }

    public void setWorkshops(Set<Workshop> workshops) {
        this.workshops = workshops;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    public List<Reclamation> getReclamations() {
        return reclamations;
    }

    public void setReclamations(List<Reclamation> reclamations) {
        this.reclamations = reclamations;
    }


    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isVerified == user.isVerified && Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(password, user.password) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(number, user.number) && Objects.equals(state, user.state) && Objects.equals(image, user.image) && Objects.equals(reclamations, user.reclamations) && Objects.equals(reservations, user.reservations) && Objects.equals(workshops, user.workshops) && roles == user.roles;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, firstName, lastName, number, state, isVerified, image, reclamations, reservations, workshops, roles);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", number='" + number + '\'' +
                ", state=" + state +
                ", isVerified=" + isVerified +
                ", image='" + image + '\'' +
                ", reclamations=" + reclamations +
                ", reservations=" + reservations +
                ", workshops=" + workshops +
                ", roles=" + roles +
                '}';
    }
}
