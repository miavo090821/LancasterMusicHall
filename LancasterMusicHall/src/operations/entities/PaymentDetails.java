package operations.entities;

/**
 * Represents the payment details required to process a payment.
 * This includes payer information and payment details such as name, billing address,
 * card information, and the amount to be paid.
 */
public class PaymentDetails {
    // ID field representing the payment details identifier.
    private int paymentDetailsId;

    // Full name of the card owner.
    private String fullName;

    // Billing address associated with the payment method.
    private String billingAddress;

    // Card number used for the payment.
    private String cardNo;

    // Expiry date of the card.
    private String expDate;

    // Card verification value.
    private String cvv;

    // The final amount to be paid.
    private double amount;

    /**
     * Default constructor.
     */
    public PaymentDetails() {
    }

    /**
     * Constructs a new PaymentDetails instance with the specified parameters.
     *
     * @param paymentDetailsId the unique identifier for the payment details
     * @param fullName the full name of the card owner
     * @param billingAddress the billing address associated with the payment
     * @param cardNo the card number used for the payment
     * @param expDate the expiration date of the card
     * @param cvv the card verification value
     * @param amount the final amount to be paid
     */
    public PaymentDetails(int paymentDetailsId, String fullName, String billingAddress, String cardNo,
                          String expDate, String cvv, double amount) {
        this.paymentDetailsId = paymentDetailsId;
        this.fullName = fullName;
        this.billingAddress = billingAddress;
        this.cardNo = cardNo;
        this.expDate = expDate;
        this.cvv = cvv;
        this.amount = amount;
    }

    /**
     * Gets the payment details identifier.
     *
     * @return the payment details identifier
     */
    public int getPaymentDetailsId() {
        return paymentDetailsId;
    }

    /**
     * Sets the payment details identifier.
     *
     * @param paymentDetailsId the payment details identifier to set
     */
    public void setPaymentDetailsId(int paymentDetailsId) {
        this.paymentDetailsId = paymentDetailsId;
    }

    /**
     * Gets the full name of the card owner.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full name of the card owner.
     *
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the billing address associated with the payment.
     *
     * @return the billing address
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * Sets the billing address associated with the payment.
     *
     * @param billingAddress the billing address to set
     */
    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    /**
     * Gets the card number used for the payment.
     *
     * @return the card number
     */
    public String getCardNo() {
        return cardNo;
    }

    /**
     * Sets the card number used for the payment.
     *
     * @param cardNo the card number to set
     */
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    /**
     * Gets the expiration date of the card.
     *
     * @return the expiration date
     */
    public String getExpDate() {
        return expDate;
    }

    /**
     * Sets the expiration date of the card.
     *
     * @param expDate the expiration date to set
     */
    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    /**
     * Gets the card verification value.
     *
     * @return the card verification value (cvv)
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * Sets the card verification value.
     *
     * @param cvv the card verification value to set
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /**
     * Gets the final amount to be paid.
     *
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the final amount to be paid.
     *
     * @param amount the amount to set
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
