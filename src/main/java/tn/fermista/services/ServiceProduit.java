package tn.fermista.services;

import tn.fermista.models.Commande;
import tn.fermista.models.Produit;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduit implements CRUD2<Produit> {

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
        String req = "UPDATE produit SET nom = ?, description = ?, image = ?, prix = ?, categorie = ?, etat = ? WHERE id = ?";
        PreparedStatement ps = null;
        try {
            ps = cnx.prepareStatement(req);
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getImage());
            ps.setInt(4, produit.getPrix());
            ps.setString(5, produit.getCategorie());
            ps.setString(6, produit.getEtat());
            ps.setInt(7, produit.getId());

            System.out.println("Executing update for product ID: " + produit.getId());
            System.out.println("SQL Query: " + req);

            int rowsAffected = ps.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);

            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    @Override
    public boolean delete(Produit produit) throws SQLException {
        String req = "DELETE FROM produit WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, produit.getId());
        ps.executeUpdate();
        return false;
    }

    public boolean delete(int id) throws SQLException {
        String req = "DELETE FROM produit WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
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

    public Produit getById(int id) {
        String query = "SELECT * FROM produit WHERE id = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = cnx.prepareStatement(query);
            pst.setInt(1, id);
            rs = pst.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getInt("prix"),
                        rs.getString("categorie"),
                        rs.getString("etat"),
                        null
                );
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du produit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
            }
        }
        return null;
    }
}