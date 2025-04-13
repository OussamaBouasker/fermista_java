package tn.fermista.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reclamation {


    private Integer id;
    private String titre;
    private String description;
    private String status;
    private LocalDateTime dateSoumission;
    private User user;

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_CANCELED = "canceled";


    public Reclamation() {
    }

    public Reclamation(Integer id, String titre, String description, String status, LocalDateTime dateSoumission, User user) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.status = status;
        this.dateSoumission = dateSoumission;
        this.user = user;
    }

    public Reclamation(String titre, String description) {
        this.titre = titre;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }

    public void setDateSoumission(LocalDateTime dateSoumission) {
        this.dateSoumission = dateSoumission;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reclamation that = (Reclamation) o;
        return Objects.equals(id, that.id) && Objects.equals(titre, that.titre) && Objects.equals(description, that.description) && Objects.equals(status, that.status) && Objects.equals(dateSoumission, that.dateSoumission) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, description, status, dateSoumission, user);
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", dateSoumission=" + dateSoumission +
                ", user=" + user +
                '}';
    }
}
