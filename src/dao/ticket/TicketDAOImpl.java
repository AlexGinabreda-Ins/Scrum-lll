package dao.ticket;

import database.DatabaseConnection;
import model.Products;
import model.Supermarket;
import model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TicketDAOImpl implements TicketDAO {

    @Override
    public void create(Ticket ticket) throws SQLException {
        create(ticket, null, getSupermarketId(ticket));
    }

    @Override
    public void create(Ticket ticket, String userDni, Integer supermarketId) throws SQLException {
        String sql = "INSERT INTO ticket (id, date, user_dni, supermarket_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ticket.getId());
            statement.setString(2, ticket.getdate());
            setNullableString(statement, 3, userDni);
            setNullableInteger(statement, 4, supermarketId);
            statement.executeUpdate();
        }
    }

    @Override
    public Ticket findById(int id) throws SQLException {
        String sql = "SELECT t.id, t.date, s.id AS supermarket_id, " +
                "s.name AS supermarket_name, s.location AS supermarket_location " +
                "FROM ticket t " +
                "LEFT JOIN supermarket s ON s.id = t.supermarket_id " +
                "WHERE t.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Ticket ticket = mapTicket(resultSet);
                    loadProducts(connection, ticket);
                    return ticket;
                }
            }
        }

        return null;
    }

    @Override
    public List<Ticket> findAll() throws SQLException {
        String sql = "SELECT t.id, t.date, s.id AS supermarket_id, " +
                "s.name AS supermarket_name, s.location AS supermarket_location " +
                "FROM ticket t " +
                "LEFT JOIN supermarket s ON s.id = t.supermarket_id " +
                "ORDER BY t.id";

        return findTickets(sql, null);
    }

    @Override
    public List<Ticket> findByUserDni(String userDni) throws SQLException {
        String sql = "SELECT t.id, t.date, s.id AS supermarket_id, " +
                "s.name AS supermarket_name, s.location AS supermarket_location " +
                "FROM ticket t " +
                "LEFT JOIN supermarket s ON s.id = t.supermarket_id " +
                "WHERE t.user_dni = ? " +
                "ORDER BY t.id";

        return findTickets(sql, userDni);
    }

    @Override
    public boolean update(Ticket ticket) throws SQLException {
        String sql = "UPDATE ticket SET date = ?, supermarket_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, ticket.getdate());
            setNullableInteger(statement, 2, getSupermarketId(ticket));
            statement.setInt(3, ticket.getId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean update(Ticket ticket, String userDni, Integer supermarketId) throws SQLException {
        String sql = "UPDATE ticket SET date = ?, user_dni = ?, supermarket_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, ticket.getdate());
            setNullableString(statement, 2, userDni);
            setNullableInteger(statement, 3, supermarketId);
            statement.setInt(4, ticket.getId());

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM ticket WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public void addProductToTicket(int ticketId, int productId, int quantity, double price) throws SQLException {
        String updateSql = "UPDATE ticket_products SET quantity = quantity + ?, price = ? " +
                "WHERE ticket_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO ticket_products (ticket_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            updateStatement.setInt(1, quantity);
            updateStatement.setDouble(2, price);
            updateStatement.setInt(3, ticketId);
            updateStatement.setInt(4, productId);

            if (updateStatement.executeUpdate() == 0) {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setInt(1, ticketId);
                    insertStatement.setInt(2, productId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.setDouble(4, price);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void addProductToTicket(int ticketId, Products product) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            int productId = findOrCreateProduct(connection, product);
            product.setId(productId);
            addProductToTicket(connection, ticketId, productId, product.getQuantity(), product.getPrice());
        }
    }

    @Override
    public boolean removeProductFromTicket(int ticketId, int productId) throws SQLException {
        String sql = "DELETE FROM ticket_products WHERE ticket_id = ? AND product_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, ticketId);
            statement.setInt(2, productId);
            return statement.executeUpdate() > 0;
        }
    }

    private List<Ticket> findTickets(String sql, String userDni) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userDni != null) {
                statement.setString(1, userDni);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Ticket ticket = mapTicket(resultSet);
                    loadProducts(connection, ticket);
                    tickets.add(ticket);
                }
            }
        }

        return tickets;
    }

    private Ticket mapTicket(ResultSet resultSet) throws SQLException {
        Ticket ticket = new Ticket(
                resultSet.getInt("id"),
                resultSet.getString("date")
        );

        int supermarketId = resultSet.getInt("supermarket_id");
        if (!resultSet.wasNull()) {
            ticket.setSupermarket(new Supermarket(
                    supermarketId,
                    resultSet.getString("supermarket_name"),
                    resultSet.getString("supermarket_location")
            ));
        }

        return ticket;
    }

    private void loadProducts(Connection connection, Ticket ticket) throws SQLException {
        String sql = "SELECT p.id, p.name, tp.price, tp.quantity, p.type_product " +
                "FROM ticket_products tp " +
                "INNER JOIN products p ON p.id = tp.product_id " +
                "WHERE tp.ticket_id = ? " +
                "ORDER BY p.name";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ticket.getId());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ticket.getProducts().add(new Products(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("price"),
                            resultSet.getInt("quantity"),
                            resultSet.getString("type_product")
                    ));
                }
            }
        }
    }

    private int findOrCreateProduct(Connection connection, Products product) throws SQLException {
        String findSql = "SELECT id FROM products WHERE name = ? AND type_product = ?";

        try (PreparedStatement statement = connection.prepareStatement(findSql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getTypeProduct());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        String insertSql = "INSERT INTO products (name, price, quantity, type_product) VALUES (?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setDouble(2, product.getPrice());
            statement.setInt(3, product.getQuantity());
            statement.setString(4, product.getTypeProduct());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("Could not create product: " + product.getName());
    }

    private void addProductToTicket(Connection connection, int ticketId, int productId, int quantity, double price) throws SQLException {
        String updateSql = "UPDATE ticket_products SET quantity = ?, price = ? " +
                "WHERE ticket_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO ticket_products (ticket_id, product_id, quantity, price) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
            updateStatement.setInt(1, quantity);
            updateStatement.setDouble(2, price);
            updateStatement.setInt(3, ticketId);
            updateStatement.setInt(4, productId);

            if (updateStatement.executeUpdate() == 0) {
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setInt(1, ticketId);
                    insertStatement.setInt(2, productId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.setDouble(4, price);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    private Integer getSupermarketId(Ticket ticket) {
        if (ticket.getSupermarket() != null && ticket.getSupermarket().getId() > 0) {
            return ticket.getSupermarket().getId();
        }

        return null;
    }

    private void setNullableString(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private void setNullableInteger(PreparedStatement statement, int index, Integer value) throws SQLException {
        if (value == null) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }
}
