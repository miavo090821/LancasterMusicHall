package operations.entities;

/**
 * Represents a discount that can be applied to a booking or customer.
 * <p>
 * The {@code Discount} class encapsulates the details of a discount, including its unique
 * identifier and the type of discount, such as "Military", "NHS", or "LocalResident".
 * </p>
 */
public class Discount {
    private int discountId;          // ID field
    private String discountType;     // e.g., "Military", "NHS", "LocalResident"

    /**
     * Default constructor for {@code Discount}.
     * <p>
     * Creates a new instance of {@code Discount} with default values.
     * </p>
     */
    public Discount() {
    }

    /**
     * Constructs a new {@code Discount} with the specified discount ID and discount type.
     *
     * @param discountId   the unique identifier for the discount.
     * @param discountType the type of the discount (e.g., "Military", "NHS", "LocalResident").
     */
    public Discount(int discountId, String discountType) {
        this.discountId = discountId;
        this.discountType = discountType;
    }


    /**
     * Returns a string representation of the {@code Discount} object.
     *
     * @return a string representation containing the discount ID and discount type.
     */
    @Override
    public String toString() {
        return "Discount{" +
                "discountId=" + discountId +
                ", discountType='" + discountType + '\'' +
                '}';
    }
}
