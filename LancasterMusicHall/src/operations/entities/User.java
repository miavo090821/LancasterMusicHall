package operations.entities;

public class User {
    private int userId;              // ID field
    private String fullName;
    private String email;
    private String role;
    private String password;

    public User(int i, String aliceJohnson, String mail, String operations, String pass123) {
        userId = i;
        fullName = aliceJohnson;
        email = mail;
        role = operations;
        password = pass123;
    }

    // Constructors, Getters, Setters...
}
