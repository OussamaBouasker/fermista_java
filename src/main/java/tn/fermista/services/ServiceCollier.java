package tn.fermista.services;

import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.utils.MyDbConnexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceCollier implements CRUD<Collier> {

    private Connection cnx;

    public ServiceCollier() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }
    @Override
    public void insert(Collier collier) {
        // Si une vache est associée, on ajoute l'ID de la vache dans la table collier
        String req = "INSERT INTO collier (reference, taille, valeur_temperature, valeur_agitation, vache_id) VALUES ('"
                + collier.getReference() + "', '"
                + collier.getTaille() + "', "
                + collier.getValeurTemperature() + ", "
                + collier.getValeurAgitation() + ", "
                + (collier.getVache() != null ? collier.getVache().getId() : "NULL") + ")";
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Collier ajouté avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public void update(Collier collier) {
        // Mise à jour de la vache associée avec la clé étrangère
        String req = "UPDATE collier SET reference = '" + collier.getReference()
                + "', taille = '" + collier.getTaille()
                + "', valeur_Temperature = " + collier.getValeurTemperature()
                + ", valeur_Agitation = " + collier.getValeurAgitation()
                + ", vache_id = " + (collier.getVache() != null ? collier.getVache().getId() : "NULL")
                + " WHERE id = " + collier.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Collier modifié avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void delete(Collier collier) {
        String req = "DELETE FROM collier WHERE id = " + collier.getId();
        try {
            Statement st = cnx.createStatement();
            st.executeUpdate(req);
            System.out.println("Collier supprimé avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    @Override
    public List<Collier> showAll() {
        List<Collier> colliers = new ArrayList<>();
        String req = "SELECT * FROM collier";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            while (rs.next()) {
                Collier c = new Collier();
                c.setId(rs.getInt("id"));
                c.setReference(rs.getString("reference"));
                c.setTaille(rs.getString("taille"));
                c.setValeurTemperature(rs.getDouble("valeur_Temperature"));
                c.setValeurAgitation(rs.getDouble("valeur_Agitation"));

                // Récupérer la vache associée à ce collier
                int vacheId = rs.getInt("vache_id");
                if (vacheId != 0) {
                    Vache vache = getVacheById(vacheId);  // Méthode pour obtenir la vache
                    c.setVache(vache);
                }

                colliers.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return colliers;
    }

    public boolean vacheHasCollier(int vacheId) {
        String req = "SELECT COUNT(*) FROM collier WHERE vache_id = " + vacheId;
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la vérification du collier : " + e.getMessage());
        }
        return false;
    }

    // Méthode pour récupérer une vache par son ID
    private Vache getVacheById(int vacheId) {
        Vache vache = null;
        String req = "SELECT * FROM vache WHERE id = " + vacheId;
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);
            if (rs.next()) {
                vache = new Vache();
                vache.setId(rs.getInt("id"));
                vache.setAge(rs.getInt("age"));
                vache.setRace(rs.getString("race"));
                vache.setEtat_medical(rs.getString("etat_medical"));
                vache.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche de la vache : " + e.getMessage());
        }
        return vache;
    }
}