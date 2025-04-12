package operations.entities;

/**
 * The Seat class represents a seat in an event or venue. Each seat is identified by a row and number,
 * and it has a specified type (e.g., regular, restricted, wheelchair, companion) and status (available, sold, held).
 */
public class Seat {
    /**
     * Row letter of the seat.
     */
    public char row;

    /**
     * Seat number within the row.
     */
    public int number;

    /**
     * Type of seat (regular, restricted, wheelchair, companion).
     */
    private Type type;

    /**
     * Seat status (available, sold, held).
     */
    private Status status;

    /**
     * Represents the different types of seating available in the venue.
     * Each type corresponds to specific accessibility and usage requirements.
     */
    public enum Type {
        /**
         * Standard seating available to all patrons.
         * No special restrictions or accommodations.
         */
        REGULAR,

        /**
         * Seating with restricted views or limited legroom.
         * Typically offered at a reduced price.
         */
        RESTRICTED,

        /**
         * Seating designed for wheelchair users.
         * Includes appropriate space and accessibility features.
         */
        WHEELCHAIR,

        /**
         * Companion seating located adjacent to wheelchair spaces.
         * Reserved for companions of wheelchair users.
         */
        COMPANION
    }

    /**
     * Represents the current booking status of a seat or ticket in the venue.
     * Used to track availability and reservation state throughout the booking process.
     */
    public enum Status {
        /**
         * The seat is currently available for booking.
         * Can be selected by customers and transition to HELD or SOLD status.
         */
        AVAILABLE,

        /**
         * The seat has been successfully sold to a customer.
         * Represents a completed transaction - cannot be modified.
         */
        SOLD,

        /**
         * The seat is temporarily reserved during the booking process.
         * May expire and return to AVAILABLE or convert to SOLD after payment.
         * hold duration is 15 minutes for online bookings.
         */
        HELD;
    }

    /**
     * Returns a human-readable string representation of the given seat status.
     *
     * @param status the seat status to be converted to a string
     * @return a string representing the given status
     */
    public String stringStatus(Status status) {
        return switch (status) {
            case AVAILABLE -> "Available";
            case HELD -> "Held";
            case SOLD -> "Sold";
        };
    }

    /**
     * Returns a human-readable string representation of the given seat type.
     *
     * @param type the seat type to be converted to a string
     * @return a string representing the given type
     */
    public String stringType(Type type) {
        return switch (type) {
            case COMPANION -> "Companion";
            case REGULAR -> "Regular";
            case RESTRICTED -> "Restricted";
            case WHEELCHAIR -> "Wheelchair";
        };
    }

    /**
     * Constructs a new Seat with the specified row, number, type, and status.
     *
     * @param row    the row letter of the seat
     * @param number the seat number within the row
     * @param type   the type of the seat
     * @param status the initial status of the seat
     */
    public Seat(char row, int number, Type type, Status status) {
        this.row = row;
        this.number = number;
        this.type = type;
        this.status = status;
    }

    /**
     * Checks if the seat is wheelchair accessible.
     *
     * @return true if the seat type is WHEELCHAIR, false otherwise
     */
    public boolean isWheelchairAccessible() {
        return this.type == Type.WHEELCHAIR;
    }

    /**
     * Checks if the seat is designated for a companion.
     *
     * @return true if the seat type is COMPANION, false otherwise
     */
    public boolean isCompanionSeat() {
        return this.type == Type.COMPANION;
    }

    /**
     * Gets the type of the seat.
     *
     * @return the seat type
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the type of the seat.
     *
     * @param type the seat type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Gets the status of the seat.
     *
     * @return the seat status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the seat.
     *
     * @param status the seat status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Returns a string representation of the Seat object.
     *
     * @return a formatted string containing the seat's row, number, type, and status
     */
    @Override
    public String toString() {
        return "Seat:" +
                "row=" + row +
                ", number=" + number +
                ", type=" + stringType(type) +
                ", status=" + stringStatus(status) +
                "\n";
    }
}
