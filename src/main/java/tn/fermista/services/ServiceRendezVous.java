package tn.fermista.services;

import tn.fermista.models.Agriculteur;
import tn.fermista.models.RendezVous;
import tn.fermista.models.Veterinaire;
import tn.fermista.utils.MyDbConnexion;
import java.time.LocalDate;
import java.time.LocalTime;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRendezVous implements CRUD<RendezVous> {

    private Connection cnx;

    public ServiceRendezVous() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(RendezVous rendezVous) throws SQLException {
        String req = "INSERT INTO rendezvous (date, heure, sex, cause, veterinaire_id, agriculteur_id, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, new java.sql.Date(rendezVous.getDate().getTime()));
            pst.setTime(2, new java.sql.Time(rendezVous.getHeure().getTime()));
            pst.setString(3, rendezVous.getSex());
            pst.setString(4, rendezVous.getCause());
            pst.setInt(5, rendezVous.getVeterinaire().getId());
            pst.setInt(6, rendezVous.getAgriculteur().getId());
            pst.setString(7, rendezVous.getStatus());
            pst.executeUpdate();
        }
    }

    @Override
    public void update(RendezVous rendezVous) throws SQLException {
        String req = "UPDATE rendezvous SET date = ?, heure = ?, sex = ?, cause = ?, veterinaire_id = ?, agriculteur_id = ?, status = ? WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setDate(1, new java.sql.Date(rendezVous.getDate().getTime()));
            pst.setTime(2, new java.sql.Time(rendezVous.getHeure().getTime()));
            pst.setString(3, rendezVous.getSex());
            pst.setString(4, rendezVous.getCause());
            pst.setInt(5, rendezVous.getVeterinaire().getId());
            pst.setInt(6, rendezVous.getAgriculteur().getId());
            pst.setString(7, rendezVous.getStatus());
            pst.setInt(8, rendezVous.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public void delete(RendezVous rendezVous) throws SQLException {
        String req = "DELETE FROM rendezvous WHERE id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, rendezVous.getId());
            pst.executeUpdate();
        }
    }

    @Override
    public List<RendezVous> showAll() throws SQLException {
        List<RendezVous> list = new ArrayList<>();
        String req = "SELECT * FROM rendezvous";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                RendezVous rdv = new RendezVous();
                rdv.setId(rs.getInt("id"));
                rdv.setDate(rs.getDate("date"));
                rdv.setHeure(rs.getTime("heure"));
                rdv.setSex(rs.getString("sex"));
                rdv.setCause(rs.getString("cause"));
                rdv.setStatus(rs.getString("status"));

                // Pour les relations, tu peux créer des objets Vétérinaire/Agriculteur vides avec juste l'ID
                // ou faire un vrai fetch si tu veux les charger complètement
                var veterinaire = new Veterinaire();
                veterinaire.setId(rs.getInt("veterinaire_id"));
                rdv.setVeterinaire(veterinaire);

                var agriculteur = new Agriculteur();
                agriculteur.setId(rs.getInt("agriculteur_id"));
                rdv.setAgriculteur(agriculteur);

                list.add(rdv);
            }
        }
        return list;
    }
}
