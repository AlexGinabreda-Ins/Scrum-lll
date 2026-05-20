package app.ui.dialog;

import model.Products;
import model.Ticket;
import model.User;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

public class ViewTicketsWindow extends JDialog {
    public ViewTicketsWindow(JFrame parent, User activeUser) {
        super(parent, "Ticket Printed View", true);
        setLayout(new BorderLayout(10, 10));

        JComboBox<String> ticketCombo = new JComboBox<>();
        for (Ticket ticket : activeUser.getTickets()) {
            ticketCombo.addItem("Ticket ID: " + ticket.getId());
        }

        JTextArea txtReceipt = new JTextArea(15, 30);
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtReceipt);

        ticketCombo.addActionListener(e -> {
            int index = ticketCombo.getSelectedIndex();
            if (index >= 0) {
                Ticket ticket = activeUser.getTickets().get(index);
                txtReceipt.setText(buildReceipt(ticket, activeUser));
            }
        });

        if (ticketCombo.getItemCount() > 0) {
            ticketCombo.setSelectedIndex(0);
        } else {
            txtReceipt.setText("\n   No tickets found for this user.");
        }

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Ticket:"));
        topPanel.add(ticketCombo);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private String buildReceipt(Ticket ticket, User activeUser) {
        StringBuilder text = new StringBuilder();
        text.append("===================================\n");
        text.append("        SUPERMARKET TICKET        \n");
        text.append("===================================\n");
        text.append("Ticket ID: ").append(ticket.getId()).append("\n");
        text.append("Date: ").append(ticket.getdate()).append("\n");
        text.append("Client: ").append(activeUser.getName()).append("\n");
        if (ticket.getSupermarket() != null) {
            text.append("Market: ")
                    .append(ticket.getSupermarket().getName())
                    .append(" (")
                    .append(ticket.getSupermarket().getLocation())
                    .append(")\n");
        }
        text.append("-----------------------------------\n");

        double total = 0;
        for (Products product : ticket.getProducts()) {
            double subtotal = product.getPrice() * product.getQuantity();
            text.append(product.getName())
                    .append(" x")
                    .append(product.getQuantity())
                    .append("   ")
                    .append(String.format("%.2f", subtotal))
                    .append(" EUR\n");
            total += subtotal;
        }

        text.append("-----------------------------------\n");
        text.append("TOTAL DUE: ").append(String.format("%.2f", total)).append(" EUR\n");
        text.append("===================================\n");

        return text.toString();
    }
}
