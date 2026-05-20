public class Products {
    private String name;
    private double price;
    private String typeProduct;
    private int quantity;

    public Products(String name, double price, int quantity, String typeProduct) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.typeProduct = typeProduct;
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
                "name='" + name + '\'' +
                ", price=" + price +
                ", typeProduct='" + typeProduct + '\'' +
                '}';
    }
}
