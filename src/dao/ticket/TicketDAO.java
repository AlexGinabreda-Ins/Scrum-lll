package dao.ticket;

import model.Products;
import model.Ticket;

import java.sql.SQLException;
import java.util.List;

public interface TicketDAO {
    void create(Ticket ticket) throws SQLException;

    void create(Ticket ticket, String userDni, Integer supermarketId) throws SQLException;

    Ticket findById(int id) throws SQLException;

    List<Ticket> findAll() throws SQLException;

    List<Ticket> findByUserDni(String userDni) throws SQLException;

    boolean update(Ticket ticket) throws SQLException;

    boolean update(Ticket ticket, String userDni, Integer supermarketId) throws SQLException;

    boolean delete(int id) throws SQLException;

    void addProductToTicket(int ticketId, Products product) throws SQLException;

    void addProductToTicket(int ticketId, int productId, int quantity, double price) throws SQLException;

    boolean removeProductFromTicket(int ticketId, int productId) throws SQLException;
}
