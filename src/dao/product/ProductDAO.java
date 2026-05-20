package dao.product;

import model.Products;

import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    void create(Products product) throws SQLException;

    Products findById(int id) throws SQLException;

    List<Products> findAll() throws SQLException;

    List<Products> findByType(String typeProduct) throws SQLException;

    boolean update(Products product) throws SQLException;

    boolean delete(int id) throws SQLException;
}
