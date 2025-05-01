package tn.fermista.models;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private String image;
    private double prix;
    private String categorie;
    private String etat;
    private Commande commande_id;
    private int quantite;

    public Produit() {
        this.quantite = 1;
    }

    public Produit(int id, String nom, String description, String image, double prix, String categorie, String etat) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.prix = prix;
        this.categorie = categorie;
        this.etat = etat;
        this.commande_id = null;
        this.quantite = 1;
    }

    public Produit(int id, String nom, String description, String image, double prix, String categorie, String etat, Commande commande_id) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.prix = prix;
        this.categorie = categorie;
        this.etat = etat;
        this.commande_id = commande_id;
        this.quantite = 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public Commande getCommande_id() {
        return commande_id;
    }

    public void setCommande_id(Commande commande_id) {
        this.commande_id = commande_id;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    @Override
    public String toString() {
        return "Produit{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", prix=" + prix +
                ", categorie='" + categorie + '\'' +
                ", etat='" + etat + '\'' +
                ", commande_id=" + commande_id +
                ", quantite=" + quantite +
                '}';
    }
}
