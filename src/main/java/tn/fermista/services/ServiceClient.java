package tn.fermista.services;

import tn.fermista.models.Client;
import tn.fermista.models.Roles;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceClient extends ServiceUser implements IService<Client> {

    private Connection cnx;

    public ServiceClient() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void ajouter(Client client) {
        String req = "INSERT INTO user (email, roles, password, first_name, last_name, number, state, is_verified, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, client.getEmail());
            st.setString(2, "[\"ROLE_CLIENT\"]"); // format JSON string
            st.setString(3, client.getPassword());
            st.setString(4, client.getFirstName());
            st.setString(5, client.getLastName());
            st.setString(6, client.getNumber());
            st.setBoolean(7, true); // ou "null" ou autre valeur selon ton modèle
            st.setBoolean(8, true);
            st.setString(9, client.getImage());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Client ajouté avec succès." : "Échec de l'ajout du client.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du client: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Client client) {
        String req = "UPDATE user SET email = ?, password = ?, first_name = ?, last_name = ?, number = ?, state = ?, is_verified = ?, image = ?, roles = ? WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setString(1, client.getEmail());
            st.setString(2, client.getPassword());
            st.setString(3, client.getFirstName());
            st.setString(4, client.getLastName());
            st.setString(5, client.getNumber());
            st.setBoolean(6, client.getState());
            st.setBoolean(7, client.isVerified());
            st.setString(8, client.getImage());
            st.setString(9, "[\"ROLE_CLIENT\"]"); // format JSON string
            st.setInt(10, client.getId());

            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Client modifié avec succès." : "Aucune modification apportée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification du client: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Client client) {
        String req = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, client.getId());
            int rowsAffected = st.executeUpdate();
            System.out.println(rowsAffected > 0 ? "Client supprimé avec succès." : "Aucun client supprimé.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression du client: " + e.getMessage());
        }
    }

    @Override
    public List<Client> rechercher() {
        List<Client> clients = new ArrayList<>();
        String req = "SELECT * FROM user WHERE JSON_CONTAINS(roles, '\"ROLE_CLIENT\"')";

        try (PreparedStatement st = cnx.prepareStatement(req);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setEmail(rs.getString("email"));
                client.setPassword(rs.getString("password"));
                client.setFirstName(rs.getString("first_name"));
                client.setLastName(rs.getString("last_name"));
                client.setNumber(rs.getString("number"));
                client.setState(rs.getBoolean("state"));
                client.setVerified(rs.getBoolean("is_verified"));
                client.setImage(rs.getString("image"));
                client.setRoles(Roles.ROLE_CLIENT);

                clients.add(client);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des clients: " + e.getMessage());
        }

        return clients;
    }

    public Client getById(int id) throws SQLException {
        String req = "SELECT * FROM user WHERE id = ? AND JSON_CONTAINS(roles, '\"ROLE_CLIENT\"')";
        
        try (PreparedStatement st = cnx.prepareStatement(req)) {
            st.setInt(1, id);
            
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setEmail(rs.getString("email"));
                    client.setPassword(rs.getString("password"));
                    client.setFirstName(rs.getString("first_name"));
                    client.setLastName(rs.getString("last_name"));
                    client.setNumber(rs.getString("number"));
                    client.setState(rs.getBoolean("state"));
                    client.setVerified(rs.getBoolean("is_verified"));
                    client.setImage(rs.getString("image"));
                    client.setRoles(Roles.ROLE_CLIENT);
                    return client;
                }
            }
        }
        return null;
    }
}
