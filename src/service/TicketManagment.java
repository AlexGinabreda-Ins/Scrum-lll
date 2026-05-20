package service;

import dao.productDAO.ProductDAO;
import dao.productDAO.ProductDAOImpl;
import dao.supermarketDAO.SupermarketDAO;
import dao.supermarketDAO.SupermarketDAOImpl;
import dao.ticketDAO.TicketDAO;
import dao.ticketDAO.TicketDAOImpl;
import model.Products;
import model.Supermarket;
import model.Ticket;
import model.User;
import database.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TicketManagment {
    private final TicketDAO ticketDAO;
    private final SupermarketDAO supermarketDAO;
    private final ProductDAO productDAO;
    private final ArrayList<User> users;

    public TicketManagment() {
        this(new TicketDAOImpl(), new SupermarketDAOImpl(), new ProductDAOImpl());
    }

    public TicketManagment(TicketDAO ticketDAO, SupermarketDAO supermarketDAO) {
        this(ticketDAO, supermarketDAO, new ProductDAOImpl());
    }

    public TicketManagment(TicketDAO ticketDAO, SupermarketDAO supermarketDAO, ProductDAO productDAO) {
        this.ticketDAO = ticketDAO;
        this.supermarketDAO = supermarketDAO;
        this.productDAO = productDAO;
        this.users = new ArrayList<>();
    }

    public void initializeDatabase() throws SQLException {
        try {
            DatabaseConnection.initializeDatabase();
        } catch (SQLException e) {
            if (!DatabaseConnection.databaseExists() || !DatabaseConnection.isDatabaseLocked(e)) {
                throw e;
            }
        }

        loadUsersFromDatabase();
    }

    public int getUserCount() {
        return users.size();
    }

    public boolean createNewUser(User user) throws SQLException {
        if (hasUser(user)) {
            return false;
        }

        saveUser(user);
        users.add(user);
        return true;
    }

    public User selectUser(int userOpcion) {
        return users.get(userOpcion);
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Products> getAllProducts() throws SQLException {
        return productDAO.findAll();
    }

    public List<Supermarket> getAllSupermarkets() throws SQLException {
        return supermarketDAO.findAll();
    }

    public void addProductsForTicket(Ticket ticket, Products product) throws SQLException {
        Products productToSave = product;

        for (Products product1 : ticket.getProducts()) {
            if (product1.getName().equals(product.getName())) {
                product1.setPrice(product.getPrice() + product1.getPrice());
                product1.setQuantity(product.getQuantity() + product1.getQuantity());
                productToSave = product1;
                break;
            }
        }

        if (!ticket.getProducts().contains(productToSave)) {
            ticket.getProducts().add(productToSave);
        }

        ticketDAO.addProductToTicket(ticket.getId(), productToSave);
    }

    public void createTicketForUser(int userOption, Ticket ticket) throws SQLException {
        User user  = selectUser(userOption);

        ticketDAO.create(ticket, user.getDni(), null);
        user.getTickets().add(ticket);
    }


    public void addSupermarketForUser(Supermarket supermarket, int userOption) throws SQLException {
        User user = selectUser(userOption);
        Supermarket savedSupermarket = findOrCreateSupermarket(supermarket);

        if (!hasSupermarket(user, savedSupermarket)) {
            user.getSupermarketsBuyed().add(savedSupermarket);
        }

        saveUserSupermarket(user, savedSupermarket);
    }

    public void addSupermarketToTicket(int userIndex, int ticketIndex, int supermarketIndex) throws SQLException {
        Ticket ticket = users.get(userIndex).getTickets().get(ticketIndex);
        Supermarket supermarket = users.get(userIndex).getSupermarketsBuyed().get(supermarketIndex);
        User user = users.get(userIndex);

        ticket.setSupermarket(supermarket);
        ticketDAO.update(ticket, user.getDni(), supermarket.getId());
    }

    public List<Supermarket> getSupermarketsFromUser(int userOpcion) {
        User user = selectUser(userOpcion);
        return user.getSupermarketsBuyed();
    }

    public Ticket getTicketFromUser(int userOption, int ticketOption) {
        User user = selectUser(userOption);
        return user.getTickets().get(ticketOption);
    }

    public List<Ticket> getTicketsFromUser(int userOpcion) {
        User user = selectUser(userOpcion);
        return user.getTickets();
    }

    private Supermarket findOrCreateSupermarket(Supermarket supermarket) throws SQLException {
        for (Supermarket savedSupermarket : supermarketDAO.findAll()) {
            if (isSameSupermarket(savedSupermarket, supermarket)) {
                return savedSupermarket;
            }
        }

        supermarketDAO.create(supermarket);
        return supermarket;
    }

    private boolean hasSupermarket(User user, Supermarket supermarket) {
        for (Supermarket savedSupermarket : user.getSupermarketsBuyed()) {
            if (savedSupermarket.getId() == supermarket.getId() && supermarket.getId() > 0) {
                return true;
            }

            if (isSameSupermarket(savedSupermarket, supermarket)) {
                return true;
            }
        }

        return false;
    }

    private boolean isSameSupermarket(Supermarket first, Supermarket second) {
        return first.getName().equalsIgnoreCase(second.getName())
                && first.getLocation().equalsIgnoreCase(second.getLocation());
    }

    private boolean hasUser(User user) {
        for (User savedUser : users) {
            if (savedUser.getDni().equalsIgnoreCase(user.getDni())) {
                return true;
            }
        }

        return false;
    }

    private void saveUser(User user) throws SQLException {
        String sql = "INSERT OR IGNORE INTO \"user\" (dni, name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getDni());
            statement.setString(2, user.getName());
            statement.executeUpdate();
        }
    }

    private void saveUserSupermarket(User user, Supermarket supermarket) throws SQLException {
        String sql = "INSERT OR IGNORE INTO user_supermarkets (user_dni, supermarket_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, user.getDni());
            statement.setInt(2, supermarket.getId());
            statement.executeUpdate();
        }
    }

    private void loadUsersFromDatabase() throws SQLException {
        users.clear();
        String sql = "SELECT dni, name FROM \"user\" ORDER BY name";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("dni"),
                        resultSet.getString("name")
                );

                user.getTickets().addAll(ticketDAO.findByUserDni(user.getDni()));
                user.getSupermarketsBuyed().addAll(loadSupermarketsFromUser(user.getDni()));
                addTicketSupermarketsToUser(user);
                users.add(user);
            }
        }
    }

    private List<Supermarket> loadSupermarketsFromUser(String userDni) throws SQLException {
        String sql = "SELECT s.id, s.name, s.location " +
                "FROM user_supermarkets us " +
                "INNER JOIN supermarket s ON s.id = us.supermarket_id " +
                "WHERE us.user_dni = ? " +
                "ORDER BY s.name";
        List<Supermarket> supermarkets = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, userDni);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    supermarkets.add(new Supermarket(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("location")
                    ));
                }
            }
        } catch (SQLException e) {
            if (!isMissingTable(e)) {
                throw e;
            }
        }

        return supermarkets;
    }

    private boolean isMissingTable(SQLException exception) {
        String message = exception.getMessage();
        return message != null && message.contains("no such table");
    }

    private void addTicketSupermarketsToUser(User user) {
        for (Ticket ticket : user.getTickets()) {
            if (ticket.getSupermarket() != null && !hasSupermarket(user, ticket.getSupermarket())) {
                user.getSupermarketsBuyed().add(ticket.getSupermarket());
            }
        }
    }

}
