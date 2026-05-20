package dao.client;

import model.Client;

import java.sql.SQLException;
import java.util.List;

public interface ClientDAO {
    void create(Client client) throws SQLException;

    Client findByDni(String dni) throws SQLException;

    List<Client> findAll() throws SQLException;

    boolean update(Client client) throws SQLException;

    boolean delete(String dni) throws SQLException;
}
