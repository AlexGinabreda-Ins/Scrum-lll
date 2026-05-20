package model;

public class Products {
    private int id;
    private String name;
    private double price;
    private String typeProduct;
    private int quantity;

    public Products(String name, double price, int quantity, String typeProduct) {
        this(0, name, price, quantity, typeProduct);
    }

    public Products(int id, String name, double price, int quantity, String typeProduct) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.typeProduct = typeProduct;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProduct) {
        this.typeProduct = typeProduct;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Products{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", typeProduct='" + typeProduct + '\'' +
                '}';
    }
}
