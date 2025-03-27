package operations.entities;

public class ContactDetails {
    private String primaryContact;
    private String telephone;
    private String email;

    public ContactDetails() {
    }

    public ContactDetails(String primaryContact, String telephone, String email) {
        this.primaryContact = primaryContact;
        this.telephone = telephone;
        this.email = email;
    }

    // Getters and Setters
    public String getPrimaryContact() {
        return primaryContact;
    }

    public void setPrimaryContact(String primaryContact) {
        this.primaryContact = primaryContact;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Primary Contact: " + primaryContact +
                "\nTelephone: " + telephone +
                "\nEmail: " + email;
    }
}