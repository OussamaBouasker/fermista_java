package tn.fermista.services;

import tn.fermista.models.User;
import tn.fermista.models.Workshop;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceWorkshop implements CRUD<Workshop> {
    private Connection cnx;

    public ServiceWorkshop() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(Workshop w) throws SQLException {
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
        pst.setString(10, w.getImage());
        pst.setString(11, w.getMeetlink());
        pst.setLong(12, w.getUser() != null ? w.getUser().getId() : null);
        pst.setString(13, w.getKeywords());
        pst.executeUpdate();
        System.out.println("Workshop inserted successfully!");
        return false;
    }

//    @Override
    public boolean update(Workshop w) throws SQLException {
        String sql = "UPDATE workshop SET titre = ?, description = ?, date = ?, prix = ?, theme = ?, duration = ?, nbr_places_max = ?, nbr_places_restantes = ?, type = ?, image = ?, meetlink = ?, user_id = ?, keywords = ? WHERE id = ?";
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
        pst.setString(10, w.getImage());
        pst.setString(11, w.getMeetlink());
        pst.setLong(12, w.getUser() != null ? w.getUser().getId() : null);
        pst.setString(13, w.getKeywords());
        pst.setLong(14, w.getId());
        pst.executeUpdate();
        System.out.println("Workshop updated successfully!");
        return false;
    }

    public void update1(Workshop workshop) throws SQLException {
        String sql = "UPDATE workshop SET titre = ?, user_id = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, workshop.getTitre());
        if (workshop.getUser() != null) {
            pst.setInt(2, workshop.getUser().getId());
        } else {
            pst.setNull(2, java.sql.Types.INTEGER);
        }
        pst.setInt(3, workshop.getId());
        pst.executeUpdate();
    }

    @Override
    public boolean delete(Workshop w) throws SQLException {
        String sql = "DELETE FROM workshop WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setLong(1, w.getId());
        pst.executeUpdate();
        System.out.println("Workshop deleted successfully!");
        return false;
    }

    @Override
    public List<Workshop> showAll() throws SQLException {
        List<Workshop> workshops = new ArrayList<>();
        String sql = "SELECT * FROM workshop";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
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

            workshops.add(w);
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

}