package dao.supermarket;

import database.DatabaseConnection;
import model.Supermarket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupermarketDAOImpl implements SupermarketDAO {

    @Override
    public void create(Supermarket supermarket) throws SQLException {
        String sql = "INSERT INTO supermarket (name, location) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, supermarket.getName());
            statement.setString(2, supermarket.getLocation());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supermarket.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Supermarket findById(int id) throws SQLException {
        String sql = "SELECT id, name, location FROM supermarket WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapSupermarket(resultSet);
                }
            }
        }

        return null;
    }

    @Override
    public List<Supermarket> findAll() throws SQLException {
        String sql = "SELECT id, name, location FROM supermarket ORDER BY name";
        List<Supermarket> supermarkets = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                supermarkets.add(mapSupermarket(resultSet));
            }
        }

        return supermarkets;
    }

    @Override
    public boolean update(Supermarket supermarket) throws SQLException {
        String sql = "UPDATE supermarket SET name = ?, location = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, supermarket.getName());
            statement.setString(2, supermarket.getLocation());
            statement.setInt(3, supermarket.getId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM supermarket WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    private Supermarket mapSupermarket(ResultSet resultSet) throws SQLException {
        return new Supermarket(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("location")
        );
    }
}
