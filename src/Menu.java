import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TicketManagment tm = new TicketManagment();

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
                    tm.createNewUser(user);

                    System.out.println("User created");
                    break;

                case 2:
                    tm.showUsers();
                    System.out.print("Select user (index): ");
                    int userOption = sc.nextInt();


                    if (userOption < 0 || userOption >= tm.users.size()) {
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
                    tm.createTicketForUser(userOption, ticket);

                    System.out.println("Ticket created");
                    break;

                case 2:
                    System.out.print("Select ticket: ");
                    int ticketOption = sc.nextInt();

                    Ticket t = tm.showTicketFromUser(userOption, ticketOption);

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

                        tm.addProductsForTicket(t, new Products(nameProduct, price, quantity, type));
                    }
                    break;

                case 3:
                    System.out.print("Supermarket name: ");
                    String nameMarket = sc.next();

                    System.out.print("Location: ");
                    String location = sc.next();

                    Supermarket supermarket = new Supermarket(nameMarket, location);
                    tm.addsupermarket(supermarket, userOption);
                    break;

                case 4:
                    tm.showAllTickets(userOption);
                    System.out.print("Select ticket: ");
                    int ticketOption4 = sc.nextInt();

                    tm.showSupermarkets(userOption);
                    System.out.print("Select supermarket: ");
                    int supermarketOption = sc.nextInt();

                    tm.addSupermarketToTicket(userOption, ticketOption4, supermarketOption);
                    break;
                case 5:
                    tm.showAllTickets(userOption);
                    break;

                case 6:
                    System.out.print("Select ticket (index): ");
                    int selectTicket = sc.nextInt();

                    tm.showTicket(userOption, selectTicket);
                    break;

                case 7:
                    System.out.println("Returning...");
                    break;

                default:
                    System.out.println("Invalid option");
            }

        } while (option != 6);
    }
}