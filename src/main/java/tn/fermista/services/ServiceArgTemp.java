package tn.fermista.services;

import tn.fermista.models.arg_temp;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceArgTemp implements CRUD<arg_temp> {

    private final Connection cnx;

    public ServiceArgTemp() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(arg_temp t) throws SQLException {
        String query = "INSERT INTO arg_temp (temperature, time_receive) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, t.getTemperature());
            ps.setTimestamp(2, Timestamp.valueOf(t.getTimeReceive()));
            ps.executeUpdate();
        }
    }

    @Override
    public void update(arg_temp t) throws SQLException {
        String query = "UPDATE arg_temp SET temperature = ?, time_receive = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, t.getTemperature());
            ps.setTimestamp(2, Timestamp.valueOf(t.getTimeReceive()));
            ps.setLong(3, t.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(arg_temp t) throws SQLException {
        String query = "DELETE FROM arg_temp WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setLong(1, t.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<arg_temp> showAll() throws SQLException {
        List<arg_temp> list = new ArrayList<>();
        String query = "SELECT * FROM arg_temp";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                arg_temp t = new arg_temp();
                t.setId(rs.getLong("id"));
                t.setTemperature(rs.getString("temperature"));
                t.setTimeReceive(rs.getTimestamp("time_receive").toLocalDateTime());
                list.add(t);
            }
        }
        return list;
    }

    // Nouvelle méthode pour récupérer la dernière température
    public arg_temp getLastTemperature() throws SQLException {
        String query = "SELECT * FROM arg_temp ORDER BY time_receive DESC LIMIT 1";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                arg_temp t = new arg_temp();
                t.setId(rs.getLong("id"));
                t.setTemperature(rs.getString("temperature"));
                t.setTimeReceive(rs.getTimestamp("time_receive").toLocalDateTime());
                return t;
            }
        }
        return null;
    }
}
