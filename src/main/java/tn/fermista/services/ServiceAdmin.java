package tn.fermista.services;

import tn.fermista.models.Admin;
import tn.fermista.models.Roles;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAdmin extends ServiceUser implements IService<Admin> {

    private Connection cnx;

    public ServiceAdmin() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void ajouter(Admin admin) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, number, state, is_verified, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, admin.getEmail());
            st.setString(2, "[\"ROLE_ADMIN\"]"); // format JSON string
            st.setString(3, admin.getPassword());
            st.setString(4, admin.getFirstName());
            st.setString(5, admin.getLastName());
            st.setString(6, admin.getNumber());
            st.setBoolean(7, admin.getState());
            st.setBoolean(8, admin.isVerified());
            st.setString(9, admin.getImage() != null ? admin.getImage() : "");

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Admin ajouté avec succès.");
                System.out.println("Détails de l'admin ajouté:");
                System.out.println("Email: " + admin.getEmail());
                System.out.println("First Name: " + admin.getFirstName());
                System.out.println("Last Name: " + admin.getLastName());
                System.out.println("Number: " + admin.getNumber());
            } else {
                System.out.println("Échec de l'ajout du admin.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Admin admin) {
        String req = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ?, roles = ? WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, admin.getEmail());
            st.setString(2, admin.getPassword());
            st.setString(3, admin.getFirstName());
            st.setString(4, admin.getLastName());
            st.setString(5, admin.getNumber());
            st.setBoolean(6, admin.getState());
            st.setBoolean(7, admin.isVerified());
            st.setString(8, admin.getImage());
            st.setString(9, "[\"ROLE_ADMIN\"]"); // format JSON string
            st.setInt(10, admin.getId());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "admin modifié avec succès." : "Aucune modification apportée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du admin: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Admin admin) {
        String req = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, admin.getId());
            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "admin supprimé avec succès." : "Aucun admin supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du admin: " + e.getMessage());
        }
    }

    @Override
    public List<Admin> rechercher() {
        List<Admin> admins = new ArrayList<>();
        String req = "SELECT * FROM user WHERE JSON_CONTAINS(roles, '\"ROLE_ADMIN\"')";

        try (PreparedStatement st = cnx.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setEmail(rs.getString("email"));
                admin.setPassword(rs.getString("password"));
                admin.setFirstName(rs.getString("first_name"));
                admin.setLastName(rs.getString("last_name"));
                admin.setNumber(rs.getString("number"));
                admin.setState(rs.getBoolean("state"));
                admin.setVerified(rs.getBoolean("is_verified"));
                admin.setImage(rs.getString("image"));
                admin.setRoles(Roles.ROLE_ADMIN);

                admins.add(admin);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des admins: " + e.getMessage());
        }

        return admins;
    }
}

