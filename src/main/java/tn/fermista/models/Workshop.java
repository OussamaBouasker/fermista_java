package tn.fermista.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Workshop {

    public static final String TYPE_LIVE_WORKSHOP = "Atelier Live";
    public static final String TYPE_SELF_PACED_WORKSHOP = "Formation Autonome";

    private int id;

    private String titre;

    private String description;

    private LocalDateTime date;

    private String prix;

    private String theme;

    private LocalTime duration;

    private List<Reservation> reservations = new ArrayList<>();

    private Integer nbrPlacesMax;

    private Integer nbrPlacesRestantes;

    private String type;

    private String image;

    private String meetlink;

    private User user;

    private String keywords;

    public Workshop() {
        this.reservations = new ArrayList<>();
        this.nbrPlacesMax = 0;
        this.nbrPlacesRestantes = 0;
    }

    public Workshop(int id, String titre, String description, LocalDateTime date, String prix, String theme, List<Reservation> reservations, LocalTime duration, Integer nbrPlacesMax, Integer nbrPlacesRestantes, String type, String image, String meetlink, User user, String keywords) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.prix = prix;
        this.theme = theme;
        this.reservations = reservations != null ? reservations : new ArrayList<>();
        this.duration = duration;
        this.nbrPlacesMax = nbrPlacesMax != null ? nbrPlacesMax : 0;
        this.nbrPlacesRestantes = nbrPlacesRestantes != null ? nbrPlacesRestantes : 0;
        this.type = type;
        this.image = image;
        this.meetlink = meetlink;
        this.user = user;
        this.keywords = keywords;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getPrix() {
        return prix;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public LocalTime getDuration() {
        return duration;
    }

    public void setDuration(LocalTime duration) {
        this.duration = duration;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Integer getNbrPlacesMax() {
        return nbrPlacesMax;
    }

    public void setNbrPlacesMax(Integer nbrPlacesMax) {
        this.nbrPlacesMax = nbrPlacesMax;
    }

    public Integer getNbrPlacesRestantes() {
        return nbrPlacesRestantes;
    }

    public void setNbrPlacesRestantes(Integer nbrPlacesRestantes) {
        this.nbrPlacesRestantes = nbrPlacesRestantes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMeetlink() {
        return meetlink;
    }

    public void setMeetlink(String meetlink) {
        this.meetlink = meetlink;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workshop workshop = (Workshop) o;
        return Objects.equals(id, workshop.id) && Objects.equals(titre, workshop.titre) && Objects.equals(description, workshop.description) && Objects.equals(date, workshop.date) && Objects.equals(prix, workshop.prix) && Objects.equals(theme, workshop.theme) && Objects.equals(duration, workshop.duration) && Objects.equals(reservations, workshop.reservations) && Objects.equals(nbrPlacesMax, workshop.nbrPlacesMax) && Objects.equals(nbrPlacesRestantes, workshop.nbrPlacesRestantes) && Objects.equals(type, workshop.type) && Objects.equals(image, workshop.image) && Objects.equals(meetlink, workshop.meetlink) && Objects.equals(user, workshop.user) && Objects.equals(keywords, workshop.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titre, description, date, prix, theme, duration, reservations, nbrPlacesMax, nbrPlacesRestantes, type, image, meetlink, user, keywords);
    }

    @Override
    public String toString() {
        return "Workshop{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", prix='" + prix + '\'' +
                ", theme='" + theme + '\'' +
                ", duration=" + duration +
                ", reservations=" + reservations +
                ", nbrPlacesMax=" + nbrPlacesMax +
                ", nbrPlacesRestantes=" + nbrPlacesRestantes +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", meetlink='" + meetlink + '\'' +
                ", user=" + user +
                ", keywords='" + keywords + '\'' +
                '}';
    }
}
