package tn.fermista.services;

import tn.fermista.models.Reclamation;
import tn.fermista.models.User;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation implements CRUD<Reclamation> {
    private Connection cnx;

    public ServiceReclamation() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Reclamation r) throws SQLException {
        String sql = "INSERT INTO reclamation (titre, description, status, date_soumission, user_id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, r.getTitre());
        pst.setString(2, r.getDescription());
        pst.setString(3, r.getStatus() != null ? r.getStatus() : Reclamation.STATUS_PENDING);
        pst.setTimestamp(4, Timestamp.valueOf(r.getDateSoumission() != null ? r.getDateSoumission() : LocalDateTime.now()));
        pst.setInt(5, r.getUser().getId());
        pst.executeUpdate();
        System.out.println("Reclamation inserted successfully!");
    }

    @Override
    public void update(Reclamation r) throws SQLException {
        String sql = "UPDATE reclamation SET titre = ?, description = ?, status = ?, date_soumission = ?, user_id = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, r.getTitre());
        pst.setString(2, r.getDescription());
        pst.setString(3, r.getStatus());
        pst.setTimestamp(4, Timestamp.valueOf(r.getDateSoumission()));
        pst.setInt(5, r.getUser().getId());
        pst.setInt(6, r.getId());
        pst.executeUpdate();
        System.out.println("Reclamation updated successfully!");
    }

    @Override
    public void delete(Reclamation r) throws SQLException {
        String sql = "DELETE FROM reclamation WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, r.getId());
        pst.executeUpdate();
        System.out.println("Reclamation deleted successfully!");
    }

    @Override
    public List<Reclamation> showAll() throws SQLException {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT r.*, u.email, u.first_name, u.last_name FROM reclamation r JOIN user u ON r.user_id = u.id";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Reclamation r = new Reclamation();
            r.setId(rs.getInt("id"));
            r.setTitre(rs.getString("titre"));
            r.setDescription(rs.getString("description"));
            r.setStatus(rs.getString("status"));
            r.setDateSoumission(rs.getTimestamp("date_soumission").toLocalDateTime());

            // Mini objet User lié à la réclamation
            User u = new User();
            u.setId(rs.getInt("user_id"));
            u.setEmail(rs.getString("email"));
            u.setFirstName(rs.getString("first_name"));
            u.setLastName(rs.getString("last_name"));
            r.setUser(u);

            list.add(r);
        }

        return list;
    }

    public int countReclamationsByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reclamation WHERE status = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, status);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}