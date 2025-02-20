package operations.entities;

public class User {
    private int userId;              // ID field
    private String fullName;
    private String email;
    private String role;
    private String password;

    public User(int userId, String fullName, String email, String role, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    // Constructors, Getters, Setters...
}
