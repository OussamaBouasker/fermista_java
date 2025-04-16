package tn.fermista.services;

import tn.fermista.models.Commande;
import tn.fermista.models.Produit;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduit implements CRUD<Produit> {

    private Connection cnx;

    public ServiceProduit() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(Produit produit) throws SQLException {
        String req = "INSERT INTO produit (id, nom, description, image, prix, categorie, etat, commande_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, produit.getId());
        ps.setString(2, produit.getNom());
        ps.setString(3, produit.getDescription());
        ps.setString(4, produit.getImage());
        ps.setInt(5, produit.getPrix());
        ps.setString(6, produit.getCategorie());
        ps.setString(7, produit.getEtat());
        if (produit.getCommande_id() != null) {
            ps.setInt(8, produit.getCommande_id().getId());
        } else {
            ps.setNull(8, Types.INTEGER);
        }
        ps.executeUpdate();
        return false;
    }

    @Override
    public boolean update(Produit produit) throws SQLException {
        String req = "UPDATE produit SET nom = ?, description = ?, image = ?, prix = ?, categorie = ?, etat = ?, commande_id = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setString(1, produit.getNom());
        ps.setString(2, produit.getDescription());
        ps.setString(3, produit.getImage());
        ps.setInt(4, produit.getPrix());
        ps.setString(5, produit.getCategorie());
        ps.setString(6, produit.getEtat());
        if (produit.getCommande_id() != null) {
            ps.setInt(7, produit.getCommande_id().getId());
        } else {
            ps.setNull(7, Types.INTEGER);
        }
        ps.setInt(8, produit.getId());
        ps.executeUpdate();
        return false;
    }

    @Override
    public boolean delete(Produit produit) throws SQLException {
        String req = "DELETE FROM produit WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, produit.getId());
        ps.executeUpdate();
        return false;
    }

    @Override
    public List<Produit> showAll() throws SQLException {
        List<Produit> list = new ArrayList<>();
        String req = "SELECT * FROM produit";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String description = rs.getString("description");
            String image = rs.getString("image");
            int prix = rs.getInt("prix");
            String categorie = rs.getString("categorie");
            String etat = rs.getString("etat");
            int commandeId = rs.getInt("commande_id");
            Commande commande = null;

            // Si commande_id n'est pas null
            if (!rs.wasNull()) {
                commande = new Commande();
                commande.setId(commandeId); // Tu peux améliorer ça en récupérant la commande depuis un ServiceCommande
            }

            Produit produit = new Produit(id, nom, description, image, prix, categorie, etat, commande);
            list.add(produit);
        }

        return list;
    }
}
