package app.ui.dialog;

import app.ui.UserInterface;

import model.User;
import service.TicketManagment;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.sql.SQLException;

public class RegisterUserWindow extends JDialog {
    public RegisterUserWindow(JFrame parent, TicketManagment tm) {
        super(parent, "Register User", true);
        setLayout(new GridLayout(3, 2, 10, 10));

        JLabel lblName = new JLabel("Name:");
        JTextField txtName = new JTextField();
        JLabel lblDni = new JLabel("DNI:");
        JTextField txtDni = new JTextField();

        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Save");

        add(lblName);
        add(txtName);
        add(lblDni);
        add(txtDni);
        add(btnCancel);
        add(btnSave);

        btnSave.addActionListener(e -> {
            String name = txtName.getText().trim();
            String dni = txtDni.getText().trim();

            if (name.isEmpty() || dni.isEmpty()) {
                JOptionPane.showMessageDialog(RegisterUserWindow.this, "Fields cannot be empty.");
                return;
            }

            User newUser = new User(dni, name);
            try {
                if (!tm.createNewUser(newUser)) {
                    JOptionPane.showMessageDialog(RegisterUserWindow.this, "This user already exists.");
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(RegisterUserWindow.this, "User could not be saved: " + ex.getMessage());
                return;
            }

            UserInterface.refreshUserDropdown();
            dispose();
        });

        btnCancel.addActionListener(e -> dispose());

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
