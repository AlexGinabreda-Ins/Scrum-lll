import java.util.ArrayList;

public class User extends Person {
    private ArrayList<Ticket> tickets;
    private ArrayList<Supermarket> supermarketsBuyed;

    public User(String dni, String name) {
        super(dni, name);
        this.tickets = new ArrayList<>();
        this.supermarketsBuyed = new ArrayList<>();
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    public ArrayList<Supermarket> getSupermarketsBuyed() {
        return supermarketsBuyed;
    }

    @Override
    public String toString() {
        return "User{" +
                "tickets=" + tickets +
                ", supermarketsBuyed=" + supermarketsBuyed +
                ", dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
