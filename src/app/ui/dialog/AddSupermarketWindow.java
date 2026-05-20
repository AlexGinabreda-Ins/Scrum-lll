package app.ui.dialog;

import model.Supermarket;
import model.Ticket;
import model.User;
import service.TicketManagment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddSupermarketWindow extends JDialog {
    public AddSupermarketWindow(JFrame parent, TicketManagment tm, User activeUser, int activeUserIndex) {
        super(parent, "Add Supermarket", true);
        setLayout(new GridLayout(5, 2, 10, 10));

        JComboBox<String> ticketCombo = new JComboBox<>();
        for (Ticket ticket : activeUser.getTickets()) {
            ticketCombo.addItem("Ticket ID: " + ticket.getId());
        }

        List<Supermarket> existingSupermarkets = loadExistingSupermarkets(tm);
        JComboBox<String> supermarketCombo = new JComboBox<>();
        supermarketCombo.addItem("New Supermarket");
        for (Supermarket supermarket : existingSupermarkets) {
            supermarketCombo.addItem(supermarket.getName() + " - " + supermarket.getLocation());
        }

        JTextField txtName = new JTextField();
        JTextField txtLocation = new JTextField();

        add(new JLabel("Select Ticket:"));
        add(ticketCombo);
        add(new JLabel("Existing Supermarket:"));
        add(supermarketCombo);
        add(new JLabel("Supermarket Name:"));
        add(txtName);
        add(new JLabel("Location:"));
        add(txtLocation);

        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Link Supermarket");
        add(btnCancel);
        add(btnSave);

        supermarketCombo.addActionListener(e -> {
            int index = supermarketCombo.getSelectedIndex() - 1;
            if (index >= 0) {
                Supermarket supermarket = existingSupermarkets.get(index);
                txtName.setText(supermarket.getName());
                txtLocation.setText(supermarket.getLocation());
            } else {
                txtName.setText("");
                txtLocation.setText("");
            }
        });

        btnSave.addActionListener(e -> {
            int selectedIndex = ticketCombo.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(AddSupermarketWindow.this, "Select a valid ticket.");
                return;
            }

            String name = txtName.getText().trim();
            String location = txtLocation.getText().trim();

            if (name.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(AddSupermarketWindow.this, "All fields are required.");
                return;
            }

            Supermarket supermarket = new Supermarket(name, location);
            try {
                tm.addSupermarketForUser(supermarket, activeUserIndex);
                int supermarketIndex = findSupermarketIndex(activeUser, name, location);
                if (supermarketIndex < 0) {
                    JOptionPane.showMessageDialog(AddSupermarketWindow.this, "Supermarket could not be selected.");
                    return;
                }

                tm.addSupermarketToTicket(activeUserIndex, selectedIndex, supermarketIndex);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AddSupermarketWindow.this, "Supermarket could not be saved: " + ex.getMessage());
                return;
            }

            JOptionPane.showMessageDialog(parent, "Supermarket linked to Ticket.");
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private int findSupermarketIndex(User activeUser, String name, String location) {
        for (int i = 0; i < activeUser.getSupermarketsBuyed().size(); i++) {
            Supermarket supermarket = activeUser.getSupermarketsBuyed().get(i);
            if (supermarket.getName().equalsIgnoreCase(name)
                    && supermarket.getLocation().equalsIgnoreCase(location)) {
                return i;
            }
        }

        return -1;
    }

    private List<Supermarket> loadExistingSupermarkets(TicketManagment tm) {
        try {
            return tm.getAllSupermarkets();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Existing supermarkets could not be loaded: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
}
