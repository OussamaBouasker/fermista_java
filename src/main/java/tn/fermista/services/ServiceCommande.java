package tn.fermista.services;

import tn.fermista.models.Commande;
import tn.fermista.models.Livraison;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommande implements CRUD2<Commande> {

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
        if (commande.getDate_commander() == null) {
            throw new SQLException("La date de commande est obligatoire");
        }
        if (commande.getStatut() == null || commande.getStatut().trim().isEmpty()) {
            throw new SQLException("Le statut est obligatoire");
        }
        if (commande.getMontant_total() <= 0) {
            throw new SQLException("Le montant total doit être supérieur à zéro");
        }

        String req = "INSERT INTO commande (date_commande, statut, montant_total, livcom_id) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = null;
        
        try {
            ps = cnx.prepareStatement(req);
            ps.setDate(1, new java.sql.Date(commande.getDate_commander().getTime()));
            ps.setString(2, commande.getStatut());
            ps.setInt(3, commande.getMontant_total());

            if (commande.getLivcom_id() != null) {
                ps.setInt(4, commande.getLivcom_id().getId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Échec de l'insertion, aucune ligne affectée");
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

        ps.setDate(1, new java.sql.Date(commande.getDate_commander().getTime()));
        ps.setString(2, commande.getStatut());
        ps.setInt(3, commande.getMontant_total());

        if (commande.getLivcom_id() != null) {
            ps.setInt(4, commande.getLivcom_id().getId());
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
            int id = rs.getInt("id");
            Date date_commander = rs.getDate("date_commande");
            String statut = rs.getString("statut");
            int montant_total = rs.getInt("montant_total");
            
            // Gérer le cas où livcom_id est NULL
            Integer livcomId = rs.getInt("livcom_id");
            Livraison livraison = null;
            
            if (!rs.wasNull()) {
                livraison = new Livraison();
                livraison.setId(livcomId);
            }

            Commande commande = new Commande(id, date_commander, statut, montant_total, livraison);
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
            int id = rs.getInt("id");
            Date date_commander = rs.getDate("date_commande");
            String statut = rs.getString("statut");
            int montant_total = rs.getInt("montant_total");
            
            // Gérer le cas où livcom_id est NULL
            Integer livcomId = rs.getInt("livcom_id");
            Livraison livraison = null;
            
            if (!rs.wasNull()) {
                livraison = new Livraison();
                livraison.setId(livcomId);
            }

            return new Commande(id, date_commander, statut, montant_total, livraison);
        }
        
        return null;
    }
}
