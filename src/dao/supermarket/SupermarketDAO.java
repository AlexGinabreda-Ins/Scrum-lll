package dao.supermarket;

import model.Supermarket;

import java.sql.SQLException;
import java.util.List;

public interface SupermarketDAO {
    void create(Supermarket supermarket) throws SQLException;

    Supermarket findById(int id) throws SQLException;

    List<Supermarket> findAll() throws SQLException;

    boolean update(Supermarket supermarket) throws SQLException;

    boolean delete(int id) throws SQLException;
}
