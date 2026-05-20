package app;

import model.Products;
import model.Supermarket;
import model.Ticket;
import model.User;
import service.TicketManagment;

import java.util.Scanner;
import java.sql.SQLException;

public class Menu {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TicketManagment tm = new TicketManagment();

        try {
            tm.initializeDatabase();
        } catch (SQLException e) {
            System.out.println("Database could not be initialized: " + e.getMessage());
            return;
        }

        int option;

        do {
            System.out.println("\n--- MAIN MENU ---");
            System.out.println("1. Create user");
            System.out.println("2. Select user");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            option = sc.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Enter name: ");
                    String name = sc.next();

                    System.out.print("Enter dni: ");
                    String dni = sc.next();

                    User user = new User(dni, name);
                    try {
                        if (tm.createNewUser(user)) {
                            System.out.println("User created");
                        } else {
                            System.out.println("This user already exists");
                        }
                    } catch (SQLException e) {
                        System.out.println("User could not be saved: " + e.getMessage());
                    }
                    break;

                case 2:
                    if (tm.getUserCount() == 0) {
                        System.out.println("There are no users created");
                        break;
                    }

                    showUsers(tm);
                    System.out.print("Select user (index): ");
                    int userOption = sc.nextInt();


                    if (userOption < 0 || userOption >= tm.getUserCount()) {
                        System.out.println("Invalid user");
                        break;
                    }

                    userMenu(sc, tm, userOption);
                    break;

                case 3:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (option != 3);
    }


    public static void userMenu(Scanner sc, TicketManagment tm, int userOption) {
        int option;

        do {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. Create ticket");
            System.out.println("2. Add product to ticket");
            System.out.println("3. Add supermarket");
            System.out.println("4. Add supermarket to ticket");
            System.out.println("5. View all tickets");
            System.out.println("6. View one ticket");
            System.out.println("7. Back");
            System.out.print("Choose an option: ");

            option = sc.nextInt();

            switch (option) {

                case 1:
                    System.out.print("Ticket ID: ");
                    int id = sc.nextInt();

                    System.out.print("Date (dd/mm/yyyy): ");
                    String date = sc.next();

                    Ticket ticket = new Ticket(id, date);
                    try {
                        tm.createTicketForUser(userOption, ticket);
                    } catch (SQLException e) {
                        System.out.println("Ticket could not be saved: " + e.getMessage());
                        break;
                    }

                    System.out.println("Ticket created");
                    break;

                case 2:
                    if (tm.getTicketsFromUser(userOption).isEmpty()) {
                        System.out.println("There are no tickets created");
                        break;
                    }

                    showAllTickets(tm, userOption);
                    System.out.print("Select ticket: ");
                    int ticketOption = sc.nextInt();

                    if (!isValidTicketOption(tm, userOption, ticketOption)) {
                        System.out.println("Invalid ticket");
                        break;
                    }

                    Ticket t = tm.getTicketFromUser(userOption, ticketOption);

                    while (true) {
                        System.out.print("Product name (x to exit): ");
                        String nameProduct = sc.next();

                        if (nameProduct.equalsIgnoreCase("x")) break;

                        System.out.print("Price: ");
                        int price = sc.nextInt();

                        System.out.print("Quantity: ");
                        int quantity = sc.nextInt();

                        System.out.print("Type: ");
                        String type = sc.next();

                        try {
                            tm.addProductsForTicket(t, new Products(nameProduct, price, quantity, type));
                        } catch (SQLException e) {
                            System.out.println("Product could not be saved: " + e.getMessage());
                        }
                    }
                    break;

                case 3:
                    System.out.print("Supermarket name: ");
                    String nameMarket = sc.next();

                    System.out.print("Location: ");
                    String location = sc.next();

                    Supermarket supermarket = new Supermarket(nameMarket, location);
                    try {
                        tm.addSupermarketForUser(supermarket, userOption);
                    } catch (SQLException e) {
                        System.out.println("Supermarket could not be saved: " + e.getMessage());
                    }
                    break;

                case 4:
                    if (tm.getTicketsFromUser(userOption).isEmpty()) {
                        System.out.println("There are no tickets created");
                        break;
                    }

                    if (tm.getSupermarketsFromUser(userOption).isEmpty()) {
                        System.out.println("There are no supermarkets created");
                        break;
                    }

                    showAllTickets(tm, userOption);
                    System.out.print("Select ticket: ");
                    int ticketOption4 = sc.nextInt();

                    if (!isValidTicketOption(tm, userOption, ticketOption4)) {
                        System.out.println("Invalid ticket");
                        break;
                    }

                    showSupermarkets(tm, userOption);
                    System.out.print("Select supermarket: ");
                    int supermarketOption = sc.nextInt();

                    if (!isValidSupermarketOption(tm, userOption, supermarketOption)) {
                        System.out.println("Invalid supermarket");
                        break;
                    }

                    try {
                        tm.addSupermarketToTicket(userOption, ticketOption4, supermarketOption);
                    } catch (SQLException e) {
                        System.out.println("Ticket could not be updated: " + e.getMessage());
                    }
                    break;
                case 5:
                    showAllTickets(tm, userOption);
                    break;

                case 6:
                    if (tm.getTicketsFromUser(userOption).isEmpty()) {
                        System.out.println("There are no tickets created");
                        break;
                    }

                    showAllTickets(tm, userOption);
                    System.out.print("Select ticket (index): ");
                    int selectTicket = sc.nextInt();

                    if (!isValidTicketOption(tm, userOption, selectTicket)) {
                        System.out.println("Invalid ticket");
                        break;
                    }

                    showTicket(tm, userOption, selectTicket);
                    break;

                case 7:
                    System.out.println("Returning...");
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (option != 7);
    }

    private static void showUsers(TicketManagment tm) {
        for (int i = 0; i < tm.getUsers().size(); i++) {
            User user = tm.getUsers().get(i);
            System.out.println(i + ": Name: " + user.getName() + " dni: " + user.getDni());
        }
    }

    private static void showSupermarkets(TicketManagment tm, int userOption) {
        for (int i = 0; i < tm.getSupermarketsFromUser(userOption).size(); i++) {
            Supermarket supermarket = tm.getSupermarketsFromUser(userOption).get(i);
            System.out.println(i + " " + supermarket.getName() + " " + supermarket.getLocation());
        }
    }

    private static void showAllTickets(TicketManagment tm, int userOption) {
        for (int i = 0; i < tm.getTicketsFromUser(userOption).size(); i++) {
            Ticket ticket = tm.getTicketsFromUser(userOption).get(i);
            System.out.println(i + ": ID: " + ticket.getId() + ". Date from ticket: " + ticket.getdate());
        }
    }

    private static void showTicket(TicketManagment tm, int userOption, int ticketOption) {
        Ticket ticket = tm.getTicketFromUser(userOption, ticketOption);

        System.out.println("Ticket " + ticket.getId());
        System.out.printf("%30s%n", ticket.getSupermarket());
        System.out.println("------------------------------");
        System.out.printf("%-14s %8s %8s%n", "Producto", "Cantidad", "Precio");
        for (Products products : ticket.getProducts()) {
            System.out.printf("%-14s %8d %8.2f%n",
                    products.getName(),
                    products.getQuantity(),
                    products.getPrice());
        }
        System.out.println("------------------------------");
        System.out.printf("%30s%n", ticket.getdate());
    }

    private static boolean isValidTicketOption(TicketManagment tm, int userOption, int ticketOption) {
        return ticketOption >= 0 && ticketOption < tm.getTicketsFromUser(userOption).size();
    }

    private static boolean isValidSupermarketOption(TicketManagment tm, int userOption, int supermarketOption) {
        return supermarketOption >= 0 && supermarketOption < tm.getSupermarketsFromUser(userOption).size();
    }
}
