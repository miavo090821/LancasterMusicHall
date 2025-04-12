package operations.entities;

/**
 * Represents a customer with associated payment and discount details.
 * <p>
 * The {@code Customer} class encapsulates customer-related information such as the customer's
 * full name, date of birth, email, phone number, payment details, and any associated discount.
 * </p>
 */
public class Customer {
    private int customerId;          // ID field
    private PaymentDetails paymentDetails; // Could be null if they pay on-site
    private Discount discount;             // If they have an associated discount
    private String fullName;
    private String dob;              // Date of birth (String or could be LocalDate)
    private String email;
    private String phoneNumber;

    /**
     * Default constructor for {@code Customer}.
     */
    public Customer() {
    }

    /**
     * Constructs a new {@code Customer} with the specified details.
     *
     * @param customerId     the unique identifier for the customer.
     * @param paymentDetails the payment details associated with the customer; can be {@code null} if they pay on-site.
     * @param discount       the discount associated with the customer; may be {@code null} if no discount applies.
     * @param fullName       the full name of the customer.
     * @param dob            the date of birth of the customer (as a String).
     * @param email          the email address of the customer.
     * @param phoneNumber    the phone number of the customer.
     */
    public Customer(int customerId, PaymentDetails paymentDetails, Discount discount, String fullName, String dob, String email, String phoneNumber) {
        this.customerId = customerId;
        this.paymentDetails = paymentDetails;
        this.discount = discount;
        this.fullName = fullName;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Retrieves the unique customer ID.
     *
     * @return the customer ID.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer ID.
     *
     * @param customerId the customer ID to set.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Retrieves the payment details associated with the customer.
     *
     * @return the {@link PaymentDetails} of the customer, or {@code null} if not provided.
     */
    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * Sets the payment details associated with the customer.
     *
     * @param paymentDetails the {@link PaymentDetails} to set.
     */
    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    /**
     * Retrieves the discount associated with the customer.
     *
     * @return the {@link Discount} of the customer, or {@code null} if there is no discount.
     */
    public Discount getDiscount() {
        return discount;
    }

    /**
     * Sets the discount associated with the customer.
     *
     * @param discount the {@link Discount} to set.
     */
    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    /**
     * Retrieves the full name of the customer.
     *
     * @return the full name.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the customer.
     *
     * @param fullName the full name to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Retrieves the date of birth of the customer.
     *
     * @return the date of birth as a {@code String}.
     */
    public String getDob() {
        return dob;
    }

    /**
     * Sets the date of birth of the customer.
     *
     * @param dob the date of birth to set as a {@code String}.
     */
    public void setDob(String dob) {
        this.dob = dob;
    }

    /**
     * Retrieves the email address of the customer.
     *
     * @return the email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the customer.
     *
     * @param email the email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieves the phone number of the customer.
     *
     * @return the phone number.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number of the customer.
     *
     * @param phoneNumber the phone number to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns a string representation of the customer.
     *
     * @return a string containing the customer details, including customer ID, full name, email,
     *         phone number, date of birth, discount, and payment details.
     */
    @Override
    public String toString() {
        return "Customer{" +
                "ID=" + customerId +
                ", full name='" + fullName + '\'' +
                ", email=" + email +
                ", phone number=" + phoneNumber +
                ", date of birth='" + dob + '\'' +
                ", discount=" + discount +
                ", paymentDetails=" + paymentDetails +
                '}';
    }
}
