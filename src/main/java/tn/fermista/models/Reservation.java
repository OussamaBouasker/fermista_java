package tn.fermista.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reservation {

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CANCELED = "canceled";


    private int id;

    private LocalDateTime reservationDate;

    private String status = STATUS_PENDING;

    private String prix;

    private String commentaire;

    private Boolean confirmation;


    private Workshop workshop;

    private String email;

    private Integer numTel;

    private String numCarteBancaire;


    private User user;

    public Reservation(User user, Workshop workshop, LocalDateTime reservationDate) {
        this.user = user;
        this.workshop = workshop;
        this.reservationDate = reservationDate;
        this.status = STATUS_PENDING;
    }

    public Reservation(){}
    public Reservation(int id, LocalDateTime reservationDate, String prix, String status, String commentaire, Boolean confirmation, Workshop workshop, String email, Integer numTel, User user, String numCarteBancaire) {
        this.id = id;
        this.reservationDate = reservationDate;
        this.prix = prix;
        this.status = status;
        this.commentaire = commentaire;
        this.confirmation = confirmation;
        this.workshop = workshop;
        this.email = email;
        this.numTel = numTel;
        this.user = user;
        this.numCarteBancaire = numCarteBancaire;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Boolean getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(Boolean confirmation) {
        this.confirmation = confirmation;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getNumTel() {
        return numTel;
    }

    public void setNumTel(Integer numTel) {
        this.numTel = numTel;
    }

    public String getNumCarteBancaire() {
        return numCarteBancaire;
    }

    public void setNumCarteBancaire(String numCarteBancaire) {
        this.numCarteBancaire = numCarteBancaire;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationDate=" + reservationDate +
                ", status='" + status + '\'' +
                ", prix='" + prix + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", confirmation=" + confirmation +
                ", workshop=" + workshop +
                ", email='" + email + '\'' +
                ", numTel=" + numTel +
                ", numCarteBancaire='" + numCarteBancaire + '\'' +
                ", user=" + user +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id) && Objects.equals(reservationDate, that.reservationDate) && Objects.equals(status, that.status) && Objects.equals(prix, that.prix) && Objects.equals(commentaire, that.commentaire) && Objects.equals(confirmation, that.confirmation) && Objects.equals(workshop, that.workshop) && Objects.equals(email, that.email) && Objects.equals(numTel, that.numTel) && Objects.equals(numCarteBancaire, that.numCarteBancaire) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reservationDate, status, prix, commentaire, confirmation, workshop, email, numTel, numCarteBancaire, user);
    }



}
