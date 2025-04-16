package tn.fermista.services;

import tn.fermista.models.RapportMedical;

import tn.fermista.utils.MyDbConnexion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.sql.*;
import java.util.ArrayList;


public class ServiceRapportMedical implements CRUD<RapportMedical> {
    private Connection cnx;

    public ServiceRapportMedical() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(RapportMedical rapportMedical) throws SQLException {
        String req = "INSERT INTO rapport_medical ( num, race, historique_de_maladie, cas_medical, solution) VALUES ?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(2, rapportMedical.getNum());
        ps.setString(3, rapportMedical.getRace());
        ps.setString(4, rapportMedical.getHistoriqueDeMaladie());
        ps.setString(5, rapportMedical.getCasMedical());
        ps.setString(6, rapportMedical.getSolution());
        ps.executeUpdate();
        return false;
    }

    @Override
    public boolean update(RapportMedical rapportMedical) throws SQLException {
        String req = "UPDATE rapport_medical SET num = ?, race = ?, historique_de_maladie = ?, cas_medical = ?, solution = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, rapportMedical.getNum());
        ps.setString(2, rapportMedical.getRace());
        ps.setString(3, rapportMedical.getHistoriqueDeMaladie());
        ps.setString(4, rapportMedical.getCasMedical());
        ps.setString(5, rapportMedical.getSolution());
        ps.setInt(6, rapportMedical.getId());
        ps.executeUpdate();
        return false;
    }

    @Override
    public boolean delete(RapportMedical rapportMedical) throws SQLException {
        String req = "DELETE FROM rapport_medical WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, rapportMedical.getId());
        ps.executeUpdate();
        return false;
    }

    @Override
    public List<RapportMedical> showAll() throws SQLException {
        List<RapportMedical> rapports = new ArrayList<>();
        String req = "SELECT * FROM rapport_medical";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            RapportMedical rm = new RapportMedical(
                    rs.getInt("id"),
                    rs.getInt("num"),
                    rs.getString("race"),
                    rs.getString("historique_de_maladie"),
                    rs.getString("cas_medical"),
                    rs.getString("solution")
            );
            rapports.add(rm);
        }
        return rapports;
    }
}
