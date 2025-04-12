package operations.entities;

/**
 * Represents the contact details of a client or stakeholder.
 * <p>
 * This class encapsulates the primary contact name, telephone number, and email address.
 * </p>
 */
public class ContactDetails {
    private String primaryContact;
    private String telephone;
    private String email;

    /**
     * Default constructor for {@code ContactDetails}.
     * <p>
     * Creates a {@code ContactDetails} object with default values.
     * </p>
     */
    public ContactDetails() {
    }

    /**
     * Constructs a new {@code ContactDetails} with the specified contact information.
     *
     * @param primaryContact the primary contact name.
     * @param telephone      the telephone number.
     * @param email          the email address.
     */
    public ContactDetails(String primaryContact, String telephone, String email) {
        this.primaryContact = primaryContact;
        this.telephone = telephone;
        this.email = email;
    }

    /**
     * Retrieves the primary contact name.
     *
     * @return the primary contact name.
     */
    public String getPrimaryContact() {
        return primaryContact;
    }

    /**
     * Sets the primary contact name.
     *
     * @param primaryContact the primary contact name to set.
     */
    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    /**
     * Retrieves the telephone number.
     *
     * @return the telephone number.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * Sets the telephone number.
     *
     * @param telephone the telephone number to set.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * Retrieves the email address.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns a string representation of the contact details.
     *
     * @return a string containing the primary contact, telephone, and email.
     */
    @Override
    public String toString() {
        return "Primary Contact: " + primaryContact +
                "\nTelephone: " + telephone +
                "\nEmail: " + email;
    }
}
