package tn.fermista.services;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {

    boolean insert(T t) throws SQLException;
    boolean update(T t)  throws SQLException;
    boolean delete(T t) throws SQLException;
    List<T> showAll() throws SQLException;

}