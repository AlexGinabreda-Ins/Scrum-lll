package dao.client;

import database.DatabaseConnection;
import model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOImpl implements ClientDAO {

    @Override
    public void create(Client client) throws SQLException {
        String sql = "INSERT INTO client (dni, name, email, phone) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, client.getDni());
            statement.setString(2, client.getName());
            setNullableString(statement, 3, client.getEmail());
            setNullableString(statement, 4, client.getPhone());
            statement.executeUpdate();
        }
    }

    @Override
    public Client findByDni(String dni) throws SQLException {
        String sql = "SELECT dni, name, email, phone FROM client WHERE dni = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dni);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapClient(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Client> findAll() throws SQLException {
        String sql = "SELECT dni, name, email, phone FROM client ORDER BY name";
        List<Client> clients = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                clients.add(mapClient(resultSet));
            }
        }

        return clients;
    }

    @Override
    public boolean update(Client client) throws SQLException {
        String sql = "UPDATE client SET name = ?, email = ?, phone = ? WHERE dni = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, client.getName());
            setNullableString(statement, 2, client.getEmail());
            setNullableString(statement, 3, client.getPhone());
            statement.setString(4, client.getDni());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String dni) throws SQLException {
        String sql = "DELETE FROM client WHERE dni = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, dni);
            return statement.executeUpdate() > 0;
        }
    }

    private Client mapClient(ResultSet resultSet) throws SQLException {
        return new Client(
                resultSet.getString("dni"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("phone")
        );
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }
}
