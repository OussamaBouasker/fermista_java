package tn.fermista.services;

import tn.fermista.models.Admin;
import tn.fermista.models.Veterinaire;
import tn.fermista.models.Roles;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceVeterinaire extends ServiceUser implements IService<Veterinaire> {

    private Connection cnx;

    public ServiceVeterinaire() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void ajouter(Veterinaire veterinaire) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, number, state, is_verified, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, veterinaire.getEmail());
            st.setString(2, "[\"ROLE_VETERINAIR\"]"); // format JSON string
            st.setString(3, veterinaire.getPassword());
            st.setString(4, veterinaire.getFirstName());
            st.setString(5, veterinaire.getLastName());
            st.setString(6, veterinaire.getNumber());
            st.setBoolean(7, true); // ou "null" ou autre valeur selon ton modèle
            st.setBoolean(8, true);
            st.setString(9, veterinaire.getImage());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "veterinaire ajouté avec succès." : "Échec de l'ajout du veterinaire.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du veterinaire: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Veterinaire veterinaire) {
        String req = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ?, roles = ? WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, veterinaire.getEmail());
            st.setString(2, veterinaire.getPassword());
            st.setString(3, veterinaire.getFirstName());
            st.setString(4, veterinaire.getLastName());
            st.setString(5, veterinaire.getNumber());
            st.setBoolean(6, veterinaire.getState());
            st.setBoolean(7, veterinaire.isVerified());
            st.setString(8, veterinaire.getImage());
            st.setString(9, "[\"ROLE_VETERINAIR\"]"); // format JSON string
            st.setInt(10, veterinaire.getId());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "veterinaire modifié avec succès." : "Aucune modification apportée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du veterinaire: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Veterinaire veterinaire) {
        String req = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, veterinaire.getId());
            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "veterinaire supprimé avec succès." : "Aucun veterinaire supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du veterinaire: " + e.getMessage());
        }
    }

    @Override
    public List<Veterinaire> rechercher() {
        List<Veterinaire> veterinaires = new ArrayList<>();
        String req = "SELECT * FROM user WHERE JSON_CONTAINS(roles, '\"ROLE_VETERINAIR\"')";

        try (PreparedStatement st = cnx.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Veterinaire veterinaire = new Veterinaire();
                veterinaire.setId(rs.getInt("id"));
                veterinaire.setEmail(rs.getString("email"));
                veterinaire.setPassword(rs.getString("password"));
                veterinaire.setFirstName(rs.getString("first_name"));
                veterinaire.setLastName(rs.getString("last_name"));
                veterinaire.setNumber(rs.getString("number"));
                veterinaire.setState(rs.getBoolean("state"));
                veterinaire.setVerified(rs.getBoolean("is_verified"));
                veterinaire.setImage(rs.getString("image"));
                veterinaire.setRoles(Roles.ROLE_VETERINAIR); // Set the role

                veterinaires.add(veterinaire);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des veterinaires: " + e.getMessage());
        }

        return veterinaires;
    }
}
