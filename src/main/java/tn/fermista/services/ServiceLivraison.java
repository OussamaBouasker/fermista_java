package tn.fermista.services;

import tn.fermista.models.Livraison;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceLivraison implements CRUD2<Livraison> {

    private Connection cnx;

    public ServiceLivraison() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public boolean insert(Livraison livraison) throws SQLException {
        String req = "INSERT INTO livraison (date, livreur, lieu) VALUES (?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setDate(1, new java.sql.Date(livraison.getDate().getTime()));
        ps.setString(2, livraison.getLivreur());
        ps.setString(3, livraison.getLieu());
        ps.executeUpdate();
        return true;
    }

    @Override
    public boolean update(Livraison livraison) throws SQLException {
        String req = "UPDATE livraison SET date = ?, livreur = ?, lieu = ? WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setDate(1, new java.sql.Date(livraison.getDate().getTime()));
        ps.setString(2, livraison.getLivreur());
        ps.setString(3, livraison.getLieu());
        ps.setInt(4, livraison.getId());
        ps.executeUpdate();
        return true;
    }

    @Override
    public boolean delete(Livraison livraison) throws SQLException {
        String req = "DELETE FROM livraison WHERE id = ?";
        PreparedStatement ps = cnx.prepareStatement(req);
        ps.setInt(1, livraison.getId());
        ps.executeUpdate();
        return true;
    }

    @Override
    public List<Livraison> showAll() throws SQLException {
        List<Livraison> list = new ArrayList<>();
        String req = "SELECT * FROM livraison";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("id");
            
            // Convertir java.sql.Date en java.util.Date pour éviter les problèmes de compatibilité
            java.sql.Date sqlDate = rs.getDate("date");
            java.util.Date date = new java.util.Date(sqlDate.getTime());
            
            String livreur = rs.getString("livreur");
            String lieu = rs.getString("lieu");

            Livraison livraison = new Livraison(id, date, livreur, lieu);
            list.add(livraison);
        }

        return list;
    }
}
