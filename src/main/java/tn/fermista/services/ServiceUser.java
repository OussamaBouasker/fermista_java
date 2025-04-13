package tn.fermista.services;

import tn.fermista.models.User;
import tn.fermista.models.Workshop;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements CRUD<User> {
    private Connection cnx;

    public ServiceUser() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(User user) throws SQLException {
        String sql = "INSERT INTO user (email, password, first_name, last_name, number, state, is_verified, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, user.getEmail());
        pst.setString(2, user.getPassword());
        pst.setString(3, user.getFirstName());
        pst.setString(4, user.getLastName());
        pst.setString(5, user.getNumber());
        pst.setBoolean(6, user.getState() != null ? user.getState() : false);
        pst.setBoolean(7, user.isVerified());
        pst.setString(8, user.getImage());
        pst.executeUpdate();
        System.out.println("User inserted successfully!");
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ? WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setString(1, user.getEmail());
        pst.setString(2, user.getPassword());
        pst.setString(3, user.getFirstName());
        pst.setString(4, user.getLastName());
        pst.setString(5, user.getNumber());
        pst.setBoolean(6, user.getState() != null ? user.getState() : false);
        pst.setBoolean(7, user.isVerified());
        pst.setString(8, user.getImage());
        pst.setInt(9, user.getId());
        pst.executeUpdate();
        System.out.println("User updated successfully!");
    }

    @Override
    public void delete(User user) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, user.getId());
        pst.executeUpdate();
        System.out.println("User deleted successfully!");
    }


    @Override
    public List<User> showAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM user";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setNumber(rs.getString("number"));
            user.setState(rs.getBoolean("state"));
            user.setVerified(rs.getBoolean("is_verified"));
            user.setImage(rs.getString("image"));
            users.add(user);
        }

        return users;
    }


    public void deleteuser(User user) throws SQLException {
        // 1. Récupérer les workshops liés
        ServiceWorkshop serviceWorkshop = new ServiceWorkshop(); // assure-toi qu'il existe
        List<Workshop> workshops = serviceWorkshop.getWorkshopsByUser(user.getId());

        // 2. Détacher chaque workshop de l'utilisateur
        for (Workshop workshop : workshops) {
            workshop.setUser(null);
            serviceWorkshop.update1(workshop); // mettre à jour le workshop dans la base
        }

        // 3. Supprimer l'utilisateur
        String sql = "DELETE FROM user WHERE id = ?";
        PreparedStatement pst = cnx.prepareStatement(sql);
        pst.setInt(1, user.getId());
        pst.executeUpdate();
        System.out.println("User deleted successfully!");
    }
}