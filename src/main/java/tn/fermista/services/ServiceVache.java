package tn.fermista.services;

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
        String req = "INSERT INTO vache (age, race, etat_medical, name) VALUES ('"
                + vache.getAge() + "', '"
                + vache.getRace() + "', '"
                + vache.getEtat_medical() + "', '"
                + vache.getName() + "')";
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
                + "' WHERE id = " + vache.getId();
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
        try {
            Statement st = cnx.createStatement();

            // Supprimer d'abord les colliers liés à cette vache
//String deleteColliers = "DELETE FROM collier WHERE vache_id = " + vache.getId();
            //  st.executeUpdate(deleteColliers);

            // Puis supprimer la vache
            String deleteVache = "DELETE FROM vache WHERE id = " + vache.getId();
            st.executeUpdate(deleteVache);

            System.out.println("Vache (et collier associé) supprimée avec succès.");
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
                vaches.add(v);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return vaches;
    }


    public Vache findById(int id) throws SQLException {
        String req = "SELECT * FROM vache WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Vache vache = new Vache(
                    rs.getInt("id"),
                    rs.getString("name")
            );

            // Optionnel : récupérer d'autres informations comme l'âge, la race, l'état médical
            vache.setAge(rs.getInt("age"));
            vache.setRace(rs.getString("race"));
            vache.setEtat_medical(rs.getString("etat_medical"));

            // Récupérer d'autres objets associés comme "collier", si nécessaire
            // vache.setCollier(new Collier(...));

            return vache;
        }

        return null;
    }


}



