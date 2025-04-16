package tn.fermista.services;
import tn.fermista.models.Admin;
import tn.fermista.models.Agriculteur;
import tn.fermista.models.Roles;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceAgriculteur extends ServiceUser implements IService<Agriculteur> {

    private Connection cnx;

    public ServiceAgriculteur() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void ajouter(Agriculteur agriculteur) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, number, state, is_verified, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, agriculteur.getEmail());
            st.setString(2, "[\"ROLE_AGRICULTOR\"]"); // format JSON string
            st.setString(3, agriculteur.getPassword());
            st.setString(4, agriculteur.getFirstName());
            st.setString(5, agriculteur.getLastName());
            st.setString(6, agriculteur.getNumber());
            st.setBoolean(7, true); // ou "null" ou autre valeur selon ton modèle
            st.setBoolean(8, true);
            st.setString(9, agriculteur.getImage());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "agriculteur ajouté avec succès." : "Échec de l'ajout du agriculteur.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du agriculteur: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Agriculteur agriculteur) {
        String req = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ?, roles = ? WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, agriculteur.getEmail());
            st.setString(2, agriculteur.getPassword());
            st.setString(3, agriculteur.getFirstName());
            st.setString(4, agriculteur.getLastName());
            st.setString(5, agriculteur.getNumber());
            st.setBoolean(6, agriculteur.getState());
            st.setBoolean(7, agriculteur.isVerified());
            st.setString(8, agriculteur.getImage());
            st.setString(9, "[\"ROLE_AGRICULTOR\"]"); // format JSON string
            st.setInt(10, agriculteur.getId());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "agriculteur modifié avec succès." : "Aucune modification apportée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du agriculteur: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Agriculteur agriculteur) {
        String req = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, agriculteur.getId());
            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "agriculteur supprimé avec succès." : "Aucun agriculteur supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du agriculteur: " + e.getMessage());
        }
    }

    @Override
    public List<Agriculteur> rechercher() {
        List<Agriculteur> agriculteurs = new ArrayList<>();
        String req = "SELECT * FROM user WHERE JSON_CONTAINS(roles, '\"ROLE_AGRICULTOR\"')";

        try (PreparedStatement st = cnx.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Agriculteur agriculteur = new Agriculteur();
                agriculteur.setId(rs.getInt("id"));
                agriculteur.setEmail(rs.getString("email"));
                agriculteur.setPassword(rs.getString("password"));
                agriculteur.setFirstName(rs.getString("first_name"));
                agriculteur.setLastName(rs.getString("last_name"));
                agriculteur.setNumber(rs.getString("number"));
                agriculteur.setState(rs.getBoolean("state"));
                agriculteur.setVerified(rs.getBoolean("is_verified"));
                agriculteur.setImage(rs.getString("image"));
                agriculteur.setRoles(Roles.ROLE_AGRICULTOR);

                agriculteurs.add(agriculteur);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des agriculteurs: " + e.getMessage());
        }

        return agriculteurs;
    }
}
