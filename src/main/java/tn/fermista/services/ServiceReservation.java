package tn.fermista.services;

import tn.fermista.models.Reservation;
import tn.fermista.models.Workshop;
import tn.fermista.models.User;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements CRUD<Reservation> {
    private Connection cnx;

    public ServiceReservation() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservation (reservation_date, status, prix, commentaire, confirmation, workshop_id, email, num_tel, num_carte_bancaire, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setTimestamp(1, reservation.getReservationDate() != null ? Timestamp.valueOf(reservation.getReservationDate()) : null);
        pst.setString(2, reservation.getStatus());
        pst.setString(3, reservation.getPrix());
        pst.setString(4, reservation.getCommentaire());
        pst.setBoolean(5, reservation.getConfirmation() != null ? reservation.getConfirmation() : false);
        pst.setInt(6, reservation.getWorkshop() != null ? reservation.getWorkshop().getId() : null);
        pst.setString(7, reservation.getEmail());
        pst.setInt(8, reservation.getNumTel());
        pst.setString(9, reservation.getNumCarteBancaire());
        pst.setInt(10, reservation.getUser() != null ? reservation.getUser().getId() : null);
        pst.executeUpdate();
        System.out.println("Reservation inserted successfully!");
        return false;
    }

    @Override
    public boolean update(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservation SET reservation_date = ?, status = ?, prix = ?, commentaire = ?, confirmation = ?, workshop_id = ?, email = ?, num_tel = ?, num_carte_bancaire = ?, user_id = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setTimestamp(1, reservation.getReservationDate() != null ? Timestamp.valueOf(reservation.getReservationDate()) : null);
        pst.setString(2, reservation.getStatus());
        pst.setString(3, reservation.getPrix());
        pst.setString(4, reservation.getCommentaire());
        pst.setBoolean(5, reservation.getConfirmation() != null ? reservation.getConfirmation() : false);
        pst.setInt(6, reservation.getWorkshop() != null ? reservation.getWorkshop().getId() : null);
        pst.setString(7, reservation.getEmail());
        pst.setInt(8, reservation.getNumTel());
        pst.setString(9, reservation.getNumCarteBancaire());
        pst.setInt(10, reservation.getUser() != null ? reservation.getUser().getId() : null);
        pst.setInt(11, reservation.getId());
        pst.executeUpdate();
        System.out.println("Reservation updated successfully!");
        return false;
    }

    @Override
    public boolean delete(Reservation reservation) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, reservation.getId());
        pst.executeUpdate();
        System.out.println("Reservation deleted successfully!");
        return false;
    }

    @Override
    public List<Reservation> showAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Reservation r = new Reservation();
            r.setId(rs.getInt("id"));
            r.setReservationDate(rs.getTimestamp("reservation_date") != null ? rs.getTimestamp("reservation_date").toLocalDateTime() : null);
            r.setStatus(rs.getString("status"));
            r.setPrix(rs.getString("prix"));
            r.setCommentaire(rs.getString("commentaire"));
            r.setConfirmation(rs.getBoolean("confirmation"));
            r.setEmail(rs.getString("email"));
            r.setNumTel(rs.getInt("num_tel"));
            r.setNumCarteBancaire(rs.getString("num_carte_bancaire"));

            // Relations fictives (à adapter selon l'implémentation réelle)
            Workshop w = new Workshop();
            w.setId(rs.getInt("workshop_id"));
            r.setWorkshop(w);

            User u = new User();
            u.setId(rs.getInt("user_id"));
            r.setUser(u);

            reservations.add(r);
        }

        return reservations;
    }


    public void deleteUserReservations(User user) throws SQLException {
        String sql = "DELETE FROM reservation WHERE user_id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, user.getId());
        pst.executeUpdate();
        System.out.println("Reservations for user deleted.");
    }
}
