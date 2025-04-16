package tn.fermista.services;

import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import tn.fermista.models.Vache;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceConsultation implements CRUD<Consultation> {

    private final Connection cnx;

    public ServiceConsultation() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(Consultation consultation) throws SQLException {
        if (consultation.getRapportMedical() == null || consultation.getVache() == null) {
            throw new SQLException("La consultation doit avoir un rapport médical et une vache associés.");
        }

        String req = "INSERT INTO consultation (rapportmedical_id, vache_id, nom, date, heure, lieu) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, consultation.getRapportMedical().getId());
        ps.setInt(2, consultation.getVache().getId());
        ps.setString(3, consultation.getNom());
        ps.setDate(4, consultation.getDate());
        ps.setTime(5, consultation.getHeure());
        ps.setString(6, consultation.getLieu());
        ps.executeUpdate();
    }

    @Override
    public void update(Consultation consultation) throws SQLException {
        if (consultation.getRapportMedical() == null || consultation.getVache() == null) {
            throw new SQLException("La consultation doit avoir un rapport médical et une vache associés.");
        }

        String req = "UPDATE consultation SET rapportmedical_id = ?, vache_id = ?, nom = ?, date = ?, heure = ?, lieu = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, consultation.getRapportMedical().getId());
        ps.setInt(2, consultation.getVache().getId());
        ps.setString(3, consultation.getNom());
        ps.setDate(4, consultation.getDate());
        ps.setTime(5, consultation.getHeure());
        ps.setString(6, consultation.getLieu());
        ps.setInt(7, consultation.getId());
        ps.executeUpdate();
    }

    @Override
    public void delete(Consultation consultation) throws SQLException {
        String req = "DELETE FROM consultation WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, consultation.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Consultation> showAll() throws SQLException {
        List<Consultation> list = new ArrayList<>();
        String req = "SELECT * FROM consultation";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id");
            int rapportMedicalId = rs.getInt("rapportmedical_id");
            int vacheId = rs.getInt("vache_id");
            String nom = rs.getString("nom");
            Date date = rs.getDate("date");
            Time heure = rs.getTime("heure");
            String lieu = rs.getString("lieu");

            // 👉 Récupération réelle via services (à implémenter pour être propre)
            RapportMedical rm = new ServiceRapportMedical().findById(rapportMedicalId);
            Vache vache = new ServiceVache().findById(vacheId);

            Consultation consultation = new Consultation(id, rm, vache, nom, date, heure, lieu);
            list.add(consultation);
        }

        return list;
    }
}
