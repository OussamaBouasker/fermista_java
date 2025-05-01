package tn.fermista.services;

import tn.fermista.models.Commande;
import tn.fermista.models.Livraison;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande implements CRUD<Commande> {

    private Connection cnx;

    public ServiceCommande() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(Commande commande) throws SQLException {
        // Vérification des valeurs requises
        if (commande == null) {
            throw new SQLException("La commande ne peut pas être null");
        }
        if (commande.getDate() == null) {
            throw new SQLException("La date de commande est obligatoire");
        }
        if (commande.getStatus() == null || commande.getStatus().trim().isEmpty()) {
            throw new SQLException("Le statut est obligatoire");
        }
        if (commande.getTotal() <= 0) {
            throw new SQLException("Le montant total doit être supérieur à zéro");
        }

        String req = "INSERT INTO commande (date_commande, statut, montant_total, livcom_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        
        try {
            ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, java.sql.Date.valueOf(commande.getDate()));
            ps.setString(2, commande.getStatus());
            ps.setDouble(3, commande.getTotal());

            if (commande.getLivraisonId() != 0) {
                ps.setInt(4, commande.getLivraisonId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            
            // Les informations client sont stockées dans l'objet Commande mais pas dans la base de données
            // pour le moment, sauf si on modifie la structure de la table

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de l'insertion, aucune ligne affectée");
            }
            
            // Récupérer l'ID généré automatiquement
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                commande.setId(generatedKeys.getInt(1));
            }
            
            return true;
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'insertion de la commande: " + e.getMessage(), e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    // Ignorer les erreurs de fermeture
                }
            }
        }
    }

    @Override
    public boolean update(Commande commande) throws SQLException {
        String req = "UPDATE commande SET date_commande = ?, statut = ?, montant_total = ?, livcom_id = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);

        ps.setDate(1, java.sql.Date.valueOf(commande.getDate()));
        ps.setString(2, commande.getStatus());
        ps.setDouble(3, commande.getTotal());

        if (commande.getLivraisonId() != 0) {
            ps.setInt(4, commande.getLivraisonId());
        } else {
            ps.setNull(4, Types.INTEGER);
        }

        ps.setInt(5, commande.getId());
        ps.executeUpdate();
        return true;
    }

    @Override
    public boolean delete(Commande commande) throws SQLException {
        String req = "DELETE FROM commande WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commande.getId());
        ps.executeUpdate();
        return true;
    }

    @Override
    public List<Commande> showAll() throws SQLException {
        List<Commande> list = new ArrayList<>();
        String req = "SELECT * FROM commande";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            Commande commande = new Commande();
            commande.setId(rs.getInt("id"));
            commande.setDate(rs.getDate("date_commande").toLocalDate());
            commande.setStatus(rs.getString("statut"));
            commande.setTotal(rs.getDouble("montant_total"));
            
            int livcomId = rs.getInt("livcom_id");
            if (!rs.wasNull()) {
                commande.setLivraisonId(livcomId);
            }

            list.add(commande);
        }

        return list;
    }

    public Commande getById(int commandeId) throws SQLException {
        String req = "SELECT * FROM commande WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, commandeId);
        ResultSet rs = ps.executeQuery();
        
        if (rs.next()) {
            Commande commande = new Commande();
            commande.setId(rs.getInt("id"));
            commande.setDate(rs.getDate("date_commande").toLocalDate());
            commande.setStatus(rs.getString("statut"));
            commande.setTotal(rs.getDouble("montant_total"));
            
            int livcomId = rs.getInt("livcom_id");
            if (!rs.wasNull()) {
                commande.setLivraisonId(livcomId);
            }

            return commande;
        }
        
        return null;
    }
}
