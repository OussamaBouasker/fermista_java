package tn.fermista.services;

import tn.fermista.models.Formateur;
import tn.fermista.models.Roles;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFormateur extends ServiceUser implements IService<Formateur> {

    private Connection cnx;

    public ServiceFormateur() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void ajouter(Formateur formateur) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, number, state, is_verified, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, formateur.getEmail());
            st.setString(2, "[\"ROLE_FORMATEUR\"]"); // format JSON string
            st.setString(3, formateur.getPassword());
            st.setString(4, formateur.getFirstName());
            st.setString(5, formateur.getLastName());
            st.setString(6, formateur.getNumber());
            st.setBoolean(7, formateur.getState());
            st.setBoolean(8, formateur.isVerified());
            st.setString(9, formateur.getImage() != null ? formateur.getImage() : "");

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("formateur ajouté avec succès.");
                System.out.println("Détails de l'formateur ajouté:");
                System.out.println("Email: " + formateur.getEmail());
                System.out.println("First Name: " + formateur.getFirstName());
                System.out.println("Last Name: " + formateur.getLastName());
                System.out.println("Number: " + formateur.getNumber());
            } else {
                System.out.println("Échec de l'ajout du formateur.");
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du formateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Formateur formateur) {
        String req = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ?, roles = ? WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, formateur.getEmail());
            st.setString(2, formateur.getPassword());
            st.setString(3, formateur.getFirstName());
            st.setString(4, formateur.getLastName());
            st.setString(5, formateur.getNumber());
            st.setBoolean(6, formateur.getState());
            st.setBoolean(7, formateur.isVerified());
            st.setString(8, formateur.getImage());
            st.setString(9, "[\"ROLE_FORMATEUR\"]"); // format JSON string
            st.setInt(10, formateur.getId());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "formateur modifié avec succès." : "Aucune modification apportée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du formateur: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Formateur formateur) {
        String req = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, formateur.getId());
            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "formateur supprimé avec succès." : "Aucun formateur supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du formateur: " + e.getMessage());
        }
    }

    @Override
    public List<Formateur> rechercher() {
        List<Formateur> formateurs = new ArrayList<>();
        String req = "SELECT * FROM user WHERE JSON_CONTAINS(roles, '\"ROLE_FORMATEUR\"')";

        try (PreparedStatement st = cnx.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Formateur formateur = new Formateur();
                formateur.setId(rs.getInt("id"));
                formateur.setEmail(rs.getString("email"));
                formateur.setPassword(rs.getString("password"));
                formateur.setFirstName(rs.getString("first_name"));
                formateur.setLastName(rs.getString("last_name"));
                formateur.setNumber(rs.getString("number"));
                formateur.setState(rs.getBoolean("state"));
                formateur.setVerified(rs.getBoolean("is_verified"));
                formateur.setImage(rs.getString("image"));
                formateur.setRoles(Roles.ROLE_FORMATEUR);

                formateurs.add(formateur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des formateurs: " + e.getMessage());
        }

        return formateurs;
    }
}

