import java.util.ArrayList;

public class Ticket {
    private int id;
    private String date;
    private Products product;
    private Supermarket supermarket;
    private ArrayList<Products> products;

    public Ticket(int id, String date) {
        this.id = id;
        this.products = new ArrayList<>();
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Products getProduct() {
        return product;
    }

    public void setProduct(Products product) {
        this.product = product;
    }

    public ArrayList<Products> getProducts() {
        return products;
    }

    public String getdate (){return date;
    }

    public Supermarket getSupermarket(){
        return supermarket;
    }

    public void setSupermarket(Supermarket supermarket) {
        this.supermarket = supermarket;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", product=" + product +
                ", products=" + products +
                '}';
    }
}
