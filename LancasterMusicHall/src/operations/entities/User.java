package operations.entities;

/**
 * The User class represents a system user with an id, full name, email address, role, and password.
 */
public class User {
    /**
     * The unique identifier for the user.
     */
    private int userId;

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The role of the user in the system.
     */
    private String role;

    /**
     * The password of the user.
     */
    private String password;

    /**
     * Constructs a new User with the specified details.
     *
     * @param userId   the unique identifier for the user
     * @param fullName the full name of the user
     * @param email    the email address of the user
     * @param role     the role of the user in the system
     * @param password the password for the user
     */
    public User(int userId, String fullName, String email, String role, String password) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    /**
     * Gets the user identifier.
     *
     * @return the user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user identifier.
     *
     * @param userId the user id to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the full name of the user.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the user.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the role of the user in the system.
     *
     * @return the user's role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user in the system.
     *
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets the password of the user.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string representation of the User object.
     *
     * @return a formatted string containing the user's details
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
