package operations.entities;

public class Discount {
    private int discountId;          // ID field
    private String discountType;      // e.g., "Military", "NHS", "LocalResident"

    // Constructors, Getters, Setters...
    @Override
    public String toString() {
        return "Discount{" +
                "discountId=" + discountId +
                ", discountType='" + discountType + '\'' +
                '}';
    }
}
