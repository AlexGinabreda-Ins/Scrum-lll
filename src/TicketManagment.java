import java.util.ArrayList;

public class TicketManagment {
    private Supermarket supermarket;
    private Ticket ticket;
    private Products products;
    private User user;
    ArrayList<User> users;

    public TicketManagment() {
        this.users = new ArrayList<>();
    }

    public Supermarket getSupermarket() {
        return supermarket;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public Products getProducts() {
        return products;
    }

    public User getUser() {
        return user;
    }

    public void createNewUser(User user) {
        if (users.contains(user)) {
            System.out.println("This user already exists");
        } else users.add(user);
    }

    public void showUsers() {
        for (int i = 0; users.size()>i;i++){
            System.out.println(i + ": Name: " + users.get(i).getName()+ " dni: "+ users.get(i).getDni());
        }
    }

    public User selectUser(int userOpcion) {
        return users.get(userOpcion);
    }

    public void addProductsForTicket(Ticket ticket, Products product) {
        for (Products product1 : ticket.getProducts()) {
            if (product1.getName().equals(product.getName())) {
                product1.setPrice(product.getPrice() + product1.getPrice());
                product1.setQuantity(product.getQuantity() + product1.getQuantity());
                return;
            }
        }
        ticket.getProducts().add(product);
    }
        public void createTicketForUser(int userOption, Ticket ticket) {
            User user  = selectUser(userOption);
            user.getTickets().add(ticket);
    }


        public void addsupermarket(Supermarket supermarket, int userOption){
            User user = selectUser(userOption);
            if (user.getSupermarketsBuyed().contains(supermarket)) {
            } else user.getSupermarketsBuyed().add(supermarket);
        }


    public void addSupermarketToTicket(int userIndex, int ticketIndex, int supermarketIndex) {
        Ticket ticket = users.get(userIndex).getTickets().get(ticketIndex);
        Supermarket supermarket = users.get(userIndex).getSupermarketsBuyed().get(supermarketIndex);

        ticket.setSupermarket(supermarket);
    }
      public void showSupermarkets(int userOpcion){
        User user = selectUser(userOpcion);
        for (int i = 0; user.getSupermarketsBuyed().size()>i;i++){
            System.out.println(i + " " + supermarket.getName()+ " " + supermarket.getLocation());
        }
      }

    public Ticket showTicketFromUser(int userOption, int ticketOption) {
        User user = selectUser(userOption);
        return user.getTickets().get(ticketOption);
    }

        public void showAllTickets(int userOpcion){
        User user = selectUser(userOpcion);
        for(Ticket ticket : user.getTickets()){
            System.out.println("ID: " + ticket.getId() + ". Date from ticket: " +ticket.getdate());
        }
        }


        public void showTicket(int userOpcion, int ticketOpcion) {
            showTicketFromUser(userOpcion,ticketOpcion);
            Ticket ticket = showTicketFromUser(userOpcion,ticketOpcion);

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


}