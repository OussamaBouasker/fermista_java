package tn.fermista.services;

import tn.fermista.models.Formateur;
import tn.fermista.models.User;
import tn.fermista.models.Workshop;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceWorkshop implements CRUD<Workshop> {
    private Connection cnx;

    public ServiceWorkshop() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Workshop w) throws SQLException {
        String sql = "INSERT INTO workshop (titre, description, date, prix, theme, duration, nbr_places_max, nbr_places_restantes, type, image, meetlink, user_id, keywords) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, w.getTitre());
        pst.setString(2, w.getDescription());
        pst.setTimestamp(3, w.getDate() != null ? Timestamp.valueOf(w.getDate()) : null);
        pst.setString(4, w.getPrix());
        pst.setString(5, w.getTheme());
        pst.setTime(6, w.getDuration() != null ? Time.valueOf(w.getDuration()) : null);
        pst.setInt(7, w.getNbrPlacesMax());
        pst.setInt(8, w.getNbrPlacesRestantes());
        pst.setString(9, w.getType());
        pst.setString(10, w.getImage() != null ? w.getImage() : "");
        pst.setString(11, w.getMeetlink() != null ? w.getMeetlink() : "");
        if (w.getUser() != null) {
            pst.setInt(12, w.getUser().getId());
        } else {
            pst.setNull(12, java.sql.Types.INTEGER);
        }
        pst.setString(13, w.getKeywords() != null ? w.getKeywords() : "");
        pst.executeUpdate();
        System.out.println("Workshop inserted successfully!");
    }

    @Override
    public void update(Workshop w) throws SQLException {
        String sql = "UPDATE workshop SET titre = ?, description = ?, date = ?, prix = ?, theme = ?, " +
                     "duration = ?, nbr_places_max = ?, nbr_places_restantes = ?, type = ?, " +
                     "image = ?, meetlink = ?, user_id = ?, keywords = ? WHERE id = ?";
        
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, w.getTitre());
            pst.setString(2, w.getDescription());
            pst.setTimestamp(3, w.getDate() != null ? Timestamp.valueOf(w.getDate()) : null);
            pst.setString(4, w.getPrix());
            pst.setString(5, w.getTheme());
            pst.setTime(6, w.getDuration() != null ? Time.valueOf(w.getDuration()) : null);
            pst.setInt(7, w.getNbrPlacesMax());
            pst.setInt(8, w.getNbrPlacesRestantes());
            pst.setString(9, w.getType());
            pst.setString(10, w.getImage() != null ? w.getImage() : "");
            pst.setString(11, w.getMeetlink() != null ? w.getMeetlink() : "");
            
            if (w.getUser() != null) {
                pst.setInt(12, w.getUser().getId());
            } else {
                pst.setNull(12, Types.INTEGER);
            }
            
            pst.setString(13, w.getKeywords() != null ? w.getKeywords() : "");
            pst.setInt(14, w.getId());
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Workshop updated successfully!");
            } else {
                throw new SQLException("Workshop update failed, no rows affected.");
            }
        }
    }

    @Override
    public void delete(Workshop w) throws SQLException {
        // First, check if the workshop exists
        String checkSql = "SELECT id FROM workshop WHERE id = ?";
        try (PreparedStatement checkStmt = cnx.prepareStatement(checkSql)) {
            checkStmt.setInt(1, w.getId());
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Workshop with ID " + w.getId() + " does not exist.");
            }
        }

        // If workshop exists, proceed with deletion
        String sql = "DELETE FROM workshop WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, w.getId());
            
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Workshop deleted successfully!");
            } else {
                throw new SQLException("Workshop deletion failed, no rows affected.");
            }
        }
    }

    @Override
    public List<Workshop> showAll() throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT w.*, u.first_name, u.last_name, u.email, u.roles " +
                "FROM workshop w " +
                "LEFT JOIN user u ON w.user_id = u.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Workshop w = new Workshop();
                w.setId(rs.getInt("id"));
                w.setTitre(rs.getString("titre"));
                w.setDescription(rs.getString("description"));
                w.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
                w.setPrix(rs.getString("prix"));
                w.setTheme(rs.getString("theme"));
                w.setDuration(rs.getTime("duration") != null ? rs.getTime("duration").toLocalTime() : null);
                w.setNbrPlacesMax(rs.getInt("nbr_places_max"));
                w.setNbrPlacesRestantes(rs.getInt("nbr_places_restantes"));
                w.setType(rs.getString("type"));
                w.setImage(rs.getString("image"));
                w.setMeetlink(rs.getString("meetlink"));
                w.setKeywords(rs.getString("keywords"));

                // Set User (Formateur) if exists
                int userId = rs.getInt("user_id");
                if (!rs.wasNull()) {
                    String userRoles = rs.getString("roles");
                    if (userRoles != null && userRoles.contains("ROLE_FORMATEUR")) {
                        Formateur formateur = new Formateur();
                        formateur.setId(userId);
                        formateur.setFirstName(rs.getString("first_name"));
                        formateur.setLastName(rs.getString("last_name"));
                        formateur.setEmail(rs.getString("email"));
                        w.setUser(formateur);
                    }
                }

                workshops.add(w);
            }
        }

        return workshops;
    }

    public List<Workshop> getWorkshopsByUser(int userId) throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshop WHERE user_id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, userId);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            Workshop w = new Workshop();
            w.setId(rs.getInt("id"));
            w.setTitre(rs.getString("titre"));
            w.setUser(null); // on peut aussi recréer un User avec juste l'id si nécessaire
            // Remplis d'autres champs si besoin
            workshops.add(w);
        }

        return workshops;
    }

    public Workshop getById(int id) throws SQLException {
        String sql = "SELECT * FROM workshop WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Workshop w = new Workshop(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getString("description"),
                            rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null,
                            rs.getString("prix"),
                            rs.getString("theme"),
                            new ArrayList<>(), // réservations à charger ailleurs si besoin
                            rs.getTime("duration") != null ? rs.getTime("duration").toLocalTime() : null,
                            rs.getInt("nbr_places_max"),
                            rs.getInt("nbr_places_restantes"),
                            rs.getString("type"),
                            rs.getString("image"),
                            rs.getString("meetlink"),
                            new User(rs.getInt("user_id")), // simple instanciation user par ID
                            rs.getString("keywords")
                    );
                    return w;
                }
            }
        }
        return null;
    }
}