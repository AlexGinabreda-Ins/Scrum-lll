package app.ui;

import app.ui.dialog.AddProductWindow;
import app.ui.dialog.AddSupermarketWindow;
import app.ui.dialog.CreateTicketWindow;
import app.ui.dialog.RegisterUserWindow;
import app.ui.dialog.ViewTicketsWindow;

import model.User;
import service.TicketManagment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;

public class UserInterface {
    private static final TicketManagment tm = new TicketManagment();
    private static User activeUser = null;
    private static int activeUserIndex = -1;

    private static final JComboBox<String> userDropdown = new JComboBox<>();
    private static final JLabel lblActiveUser = new JLabel("No active user selected", SwingConstants.CENTER);
    private static final JButton btnCreateTicket = new JButton("1. Create New Ticket");
    private static final JButton btnAddProduct = new JButton("2. Add Product to Ticket");
    private static final JButton btnAddMarket = new JButton("3. Add Supermarket to Ticket");
    private static final JButton btnShowTickets = new JButton("4. View Printed Tickets");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UserInterface::createAndShowWindow);
    }

    private static void createAndShowWindow() {
        try {
            tm.initializeDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database could not be initialized: " + e.getMessage());
            return;
        }

        JFrame window = new JFrame("Supermarket Hub");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton btnRegisterUser = new JButton("Register New User");
        JButton btnSelectUser = new JButton("Select User");

        topPanel.add(new JLabel("Users:"));
        topPanel.add(userDropdown);
        topPanel.add(btnRegisterUser);
        topPanel.add(btnSelectUser);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        lblActiveUser.setFont(new Font("Arial", Font.BOLD, 12));
        centerPanel.add(lblActiveUser, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        setActionsEnabled(false);

        actionsPanel.add(btnCreateTicket);
        actionsPanel.add(btnAddProduct);
        actionsPanel.add(btnAddMarket);
        actionsPanel.add(btnShowTickets);
        centerPanel.add(actionsPanel, BorderLayout.CENTER);

        window.getContentPane().add(topPanel, BorderLayout.NORTH);
        window.getContentPane().add(centerPanel, BorderLayout.CENTER);
        ((JPanel) window.getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnRegisterUser.addActionListener(e -> new RegisterUserWindow(window, tm));

        btnSelectUser.addActionListener(e -> {
            int index = userDropdown.getSelectedIndex();
            if (index >= 0 && index < tm.getUserCount()) {
                activeUserIndex = index;
                activeUser = tm.selectUser(index);
                lblActiveUser.setText("Active User: " + activeUser.getName() + " (" + activeUser.getDni() + ")");
                setActionsEnabled(true);
            } else {
                JOptionPane.showMessageDialog(window, "Please register a user first.");
            }
        });

        btnCreateTicket.addActionListener(e -> new CreateTicketWindow(window, tm, activeUserIndex));

        btnAddProduct.addActionListener(e -> new AddProductWindow(window, tm, activeUser));

        btnAddMarket.addActionListener(e -> new AddSupermarketWindow(window, tm, activeUser, activeUserIndex));

        btnShowTickets.addActionListener(e -> new ViewTicketsWindow(window, activeUser));

        refreshUserDropdown();

        window.pack();
        window.setMinimumSize(new Dimension(400, 300));
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void refreshUserDropdown() {
        userDropdown.removeAllItems();
        for (User user : tm.getUsers()) {
            userDropdown.addItem(user.getName() + " - " + user.getDni());
        }
    }

    private static void setActionsEnabled(boolean enabled) {
        btnCreateTicket.setEnabled(enabled);
        btnAddProduct.setEnabled(enabled);
        btnAddMarket.setEnabled(enabled);
        btnShowTickets.setEnabled(enabled);
    }
}
