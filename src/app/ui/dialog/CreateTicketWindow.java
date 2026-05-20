package app.ui.dialog;

import model.Ticket;
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
import java.time.LocalDate;

public class CreateTicketWindow extends JDialog {
    public CreateTicketWindow(JFrame parent, TicketManagment tm, int activeUserIndex) {
        super(parent, "Create Ticket", true);
        setLayout(new GridLayout(3, 2, 10, 10));

        JTextField txtId = new JTextField();
        JTextField txtDate = new JTextField(LocalDate.now().toString());

        add(new JLabel("Ticket ID (Number):"));
        add(txtId);
        add(new JLabel("Date (YYYY-MM-DD):"));
        add(txtDate);

        JButton btnCancel = new JButton("Cancel");
        JButton btnSave = new JButton("Create");
        add(btnCancel);
        add(btnSave);

        btnSave.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText().trim());
                String date = txtDate.getText().trim();

                if (date.isEmpty()) {
                    JOptionPane.showMessageDialog(CreateTicketWindow.this, "Date is required.");
                    return;
                }

                Ticket newTicket = new Ticket(id, date);
                tm.createTicketForUser(activeUserIndex, newTicket);

                JOptionPane.showMessageDialog(parent, "Ticket successfully created.");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(CreateTicketWindow.this, "ID must be a clean number.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(CreateTicketWindow.this, "Ticket could not be saved: " + ex.getMessage());
            }
        });

        btnCancel.addActionListener(e -> dispose());

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
}
