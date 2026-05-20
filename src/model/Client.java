package model;

public class Client extends Person {
    private String email;
    private String phone;

    public Client(String dni, String name, String email, String phone) {
        super(dni, name);
        this.email = email;
        this.phone = phone;
    }

    public Client(String dni, String name) {
        this(dni, name, null, null);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Client{" +
                "dni='" + dni + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
