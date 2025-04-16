package tn.fermista.services;

import tn.fermista.models.Reservation;
import tn.fermista.models.Workshop;
import tn.fermista.models.User;
import tn.fermista.models.Client;
import tn.fermista.models.Agriculteur;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements CRUD<Reservation> {
    private Connection cnx;
    private ServiceWorkshop serviceWorkshop;
    private ServiceClient serviceClient;
    private ServiceAgriculteur serviceAgriculteur;

    public ServiceReservation() {
        cnx = MyDbConnexion.getInstance().getCnx();
        serviceWorkshop = new ServiceWorkshop();
        serviceClient = new ServiceClient();
        serviceAgriculteur = new ServiceAgriculteur();
    }

    @Override
    public void insert(Reservation reservation) throws SQLException {
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
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
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
    }

    @Override
    public void delete(Reservation reservation) throws SQLException {
        String sql = "DELETE FROM reservation WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, reservation.getId());
        pst.executeUpdate();
        System.out.println("Reservation deleted successfully!");
    }

    @Override
    public List<Reservation> showAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.*, w.titre as workshop_titre, u.first_name, u.last_name, u.email as user_email, " +
                "u.roles as user_roles " +
                "FROM reservation r " +
                "LEFT JOIN workshop w ON r.workshop_id = w.id " +
                "LEFT JOIN user u ON r.user_id = u.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Reservation r = new Reservation();
                r.setId(rs.getInt("id"));
                r.setReservationDate(rs.getTimestamp("reservation_date") != null ?
                        rs.getTimestamp("reservation_date").toLocalDateTime() : null);
                r.setStatus(rs.getString("status"));
                r.setPrix(rs.getString("prix"));
                r.setCommentaire(rs.getString("commentaire"));
                r.setConfirmation(rs.getBoolean("confirmation"));
                r.setEmail(rs.getString("email"));
                r.setNumTel(rs.getInt("num_tel"));
                r.setNumCarteBancaire(rs.getString("num_carte_bancaire"));

                // Set Workshop with basic info
                int workshopId = rs.getInt("workshop_id");
                if (!rs.wasNull()) {
                    Workshop w = new Workshop();
                    w.setId(workshopId);
                    w.setTitre(rs.getString("workshop_titre"));
                    r.setWorkshop(w);
                }

                // Set User with basic info
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    String userRoles = rs.getString("user_roles");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String userEmail = rs.getString("user_email");

                    User user;
                    if (userRoles != null && userRoles.contains("ROLE_CLIENT")) {
                        user = new Client();
                    } else {
                        user = new Agriculteur();
                    }
                    user.setId(userId);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(userEmail);
                    r.setUser(user);
                }

                reservations.add(r);
            }
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

    public Reservation getById(int id) throws SQLException {
        String sql = "SELECT r.*, w.titre as workshop_titre, u.first_name, u.last_name, u.email as user_email, " +
                "u.roles as user_roles " +
                "FROM reservation r " +
                "LEFT JOIN workshop w ON r.workshop_id = w.id " +
                "LEFT JOIN user u ON r.user_id = u.id " +
                "WHERE r.id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Reservation r = new Reservation();
                    r.setId(rs.getInt("id"));
                    r.setReservationDate(rs.getTimestamp("reservation_date") != null ?
                            rs.getTimestamp("reservation_date").toLocalDateTime() : null);
                    r.setStatus(rs.getString("status"));
                    r.setPrix(rs.getString("prix"));
                    r.setCommentaire(rs.getString("commentaire"));
                    r.setConfirmation(rs.getBoolean("confirmation"));
                    r.setEmail(rs.getString("email"));
                    r.setNumTel(rs.getInt("num_tel"));
                    r.setNumCarteBancaire(rs.getString("num_carte_bancaire"));

                    // Set Workshop with basic info
                    int workshopId = rs.getInt("workshop_id");
                    if (!rs.wasNull()) {
                        Workshop w = new Workshop();
                        w.setId(workshopId);
                        w.setTitre(rs.getString("workshop_titre"));
                        r.setWorkshop(w);
                    }

                    // Set User with basic info
                    int userId = rs.getInt("user_id");
                    if (!rs.wasNull()) {
                        String userRoles = rs.getString("user_roles");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        String userEmail = rs.getString("user_email");

                        User user;
                        if (userRoles != null && userRoles.contains("ROLE_CLIENT")) {
                            user = new Client();
                        } else {
                            user = new Agriculteur();
                        }
                        user.setId(userId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        user.setEmail(userEmail);
                        r.setUser(user);
                    }

                    return r;
                }
            }
        }
        return null;
    }
}
