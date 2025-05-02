package tn.fermista.services;

import tn.fermista.models.arg_agit;
import tn.fermista.utils.MyDbConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceArgAgit implements CRUD<arg_agit> {

    private final Connection cnx;

    public ServiceArgAgit() {
        cnx = MyDbConnexion.getInstance().getCnx();
    }

    @Override
    public void insert(arg_agit a) throws SQLException {
        String query = "INSERT INTO arg_agit (accel_x, accel_y, accel_z, time_receive) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, a.getAccelX());
            ps.setString(2, a.getAccelY());
            ps.setString(3, a.getAccelZ());
            ps.setTimestamp(4, Timestamp.valueOf(a.getTimeReceive()));
            ps.executeUpdate();
        }
    }

    @Override
    public void update(arg_agit a) throws SQLException {
        String query = "UPDATE arg_agit SET accel_x = ?, accel_y = ?, accel_z = ?, time_receive = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, a.getAccelX());
            ps.setString(2, a.getAccelY());
            ps.setString(3, a.getAccelZ());
            ps.setTimestamp(4, Timestamp.valueOf(a.getTimeReceive()));
            ps.setLong(5, a.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(arg_agit a) throws SQLException {
        String query = "DELETE FROM arg_agit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setLong(1, a.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<arg_agit> showAll() throws SQLException {
        List<arg_agit> list = new ArrayList<>();
        String query = "SELECT * FROM arg_agit";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                arg_agit a = new arg_agit();
                a.setId(Math.toIntExact(rs.getLong("id")));
                a.setAccelX(rs.getString("accel_x"));
                a.setAccelY(rs.getString("accel_y"));
                a.setAccelZ(rs.getString("accel_z"));
                a.setTimeReceive(rs.getTimestamp("time_receive").toLocalDateTime());
                list.add(a);
            }
        }
        return list;
    }
}
