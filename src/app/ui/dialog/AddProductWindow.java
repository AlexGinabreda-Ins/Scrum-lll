package app.ui.dialog;

import model.Products;
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

public class AddProductWindow extends JDialog {
    public AddProductWindow(JFrame parent, TicketManagment tm, User activeUser) {
        super(parent, "Add Product to Ticket", true);
        setLayout(new GridLayout(7, 2, 10, 10));

        JComboBox<String> ticketCombo = new JComboBox<>();
        for (Ticket ticket : activeUser.getTickets()) {
            ticketCombo.addItem("ID: " + ticket.getId());
        }

        List<Products> existingProducts = loadExistingProducts(tm);
        JComboBox<String> productCombo = new JComboBox<>();
        productCombo.addItem("New Product");
        for (Products product : existingProducts) {
            productCombo.addItem(product.getName() + " - " + product.getTypeProduct());
        }

        JTextField txtName = new JTextField();
        JTextField txtPrice = new JTextField();
        JTextField txtQty = new JTextField();
        JTextField txtType = new JTextField();

        add(new JLabel("Select Ticket:"));
        add(ticketCombo);
        add(new JLabel("Existing Product:"));
        add(productCombo);
        add(new JLabel("Product Name:"));
        add(txtName);
        add(new JLabel("Price:"));
        add(txtPrice);
        add(new JLabel("Quantity:"));
        add(txtQty);
        add(new JLabel("Product Type:"));
        add(txtType);

        JButton btnCancel = new JButton("Cancel");
        JButton btnAdd = new JButton("Add");
        add(btnCancel);
        add(btnAdd);

        productCombo.addActionListener(e -> {
            int index = productCombo.getSelectedIndex() - 1;
            if (index >= 0) {
                Products product = existingProducts.get(index);
                txtName.setText(product.getName());
                txtPrice.setText(String.valueOf(product.getPrice()));
                txtQty.setText("1");
                txtType.setText(product.getTypeProduct());
            } else {
                txtName.setText("");
                txtPrice.setText("");
                txtQty.setText("");
                txtType.setText("");
            }
        });

        btnAdd.addActionListener(e -> {
            int selectedIndex = ticketCombo.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(AddProductWindow.this, "Create a ticket first.");
                return;
            }

            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                int qty = Integer.parseInt(txtQty.getText().trim());
                String type = txtType.getText().trim();

                if (name.isEmpty() || type.isEmpty()) {
                    JOptionPane.showMessageDialog(AddProductWindow.this, "All fields are required.");
                    return;
                }

                Products product = new Products(name, price, qty, type);
                Ticket targetTicket = activeUser.getTickets().get(selectedIndex);
                tm.addProductsForTicket(targetTicket, product);

                JOptionPane.showMessageDialog(parent, "Product added to ticket.");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddProductWindow.this, "Check Price and Quantity values.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(AddProductWindow.this, "Product could not be saved: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dispose());

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private List<Products> loadExistingProducts(TicketManagment tm) {
        try {
            return tm.getAllProducts();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Existing products could not be loaded: " + ex.getMessage());
            return new ArrayList<>();
        }
    }
}
