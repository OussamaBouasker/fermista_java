package tn.fermista.models;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private String image;
    private int Prix;
    private String Categorie;
    private String etat;
    private Commande commande_id;

    public Produit() {
        // constructeur vide
    }

    public Produit(int id, String nom, String description, String image, double prix, String categorie, String etat) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.Prix = (int) prix;
        this.Categorie = categorie;
        this.etat = etat;
        this.commande_id = null;
    }

    public Produit(int id, String nom, String description, String image, int prix, String categorie, String etat, Commande commande_id) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.image = image;
        this.Prix = prix;
        this.Categorie = categorie;
        this.etat = etat;
        this.commande_id = commande_id;
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

    public int getPrix() {
        return Prix;
    }

    public void setPrix(int prix) {
        Prix = prix;
    }

    public String getCategorie() {
        return Categorie;
    }

    public void setCategorie(String categorie) {
        Categorie = categorie;
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

    @Override
    public String toString() {
        return "Produit{" +
                "commande_id=" + commande_id +
                ", etat='" + etat + '\'' +
                ", Categorie='" + Categorie + '\'' +
                ", Prix=" + Prix +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", nom='" + nom + '\'' +
                ", id=" + id +
                '}';
    }

}