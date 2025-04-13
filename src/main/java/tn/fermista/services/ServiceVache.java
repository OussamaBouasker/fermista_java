package tn.fermista.services;

import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.utils.MyDbConnexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceVache implements CRUD<Vache> {

    private Connection cnx;

    public ServiceVache() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }
    @Override
    public void insert(Vache vache) {
        String req = "INSERT INTO vache (age, race, etat_medical, name, collier_id) VALUES ('"
                + vache.getAge() + "', '"
                + vache.getRace() + "', '"
                + vache.getEtat_medical() + "', '"
                + vache.getName() + "', "
                + (vache.getCollier() != null ? vache.getCollier().getId() : "NULL") + ")";
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Vache ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Vache vache) {
        String req = "UPDATE vache SET age = '" + vache.getAge()
                + "', race = '" + vache.getRace()
                + "', etat_medical = '" + vache.getEtat_medical()
                + "', name = '" + vache.getName()
                + "', collier_id = " + (vache.getCollier() != null ? vache.getCollier().getId() : "NULL")
                + " WHERE id = " + vache.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Vache modifiée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void delete(Vache vache) {
        // Optionnellement, supprimer le collier associé avant la vache
        if (vache.getCollier() != null) {
            String deleteCollier = "DELETE FROM collier WHERE id = " + vache.getCollier().getId();
            try {
                Statement st = cnx.createStatement();
                st.executeUpdate(deleteCollier);
            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression du collier : " + e.getMessage());
            }
        }

        String req = "DELETE FROM vache WHERE id = " + vache.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Vache supprimée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Vache> showAll() {
        List<Vache> vaches = new ArrayList<>();
        String req = "SELECT * FROM vache";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Vache v = new Vache();
                v.setId(rs.getInt("id"));
                v.setAge(rs.getInt("age"));
                v.setRace(rs.getString("race"));
                v.setEtat_medical(rs.getString("etat_medical"));
                v.setName(rs.getString("name"));

                // Rechercher le collier associé si il existe
                int collierId = rs.getInt("collier_id");
                if (collierId != 0) {
                    Collier collier = getCollierById(collierId);  // Méthode pour obtenir le collier
                    v.setCollier(collier);
                }

                vaches.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return vaches;
    }

    private Collier getCollierById(int collierId) {
        Collier collier = null;
        String req = "SELECT * FROM collier WHERE id = " + collierId;
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            if (rs.next()) {
                collier = new Collier();
                collier.setId(rs.getInt("id"));
                collier.setReference(rs.getString("reference"));
                collier.setTaille(rs.getString("taille"));
                collier.setValeurTemperature(rs.getDouble("valeurTemperature"));
                collier.setValeurAgitation(rs.getDouble("valeurAgitation"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du collier : " + e.getMessage());
        }
        return collier;
    }
}

