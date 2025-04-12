package operations.module;

import operations.entities.Booking;
import operations.entities.Seat;
import java.util.List;

/**
 * The BookingDetails class encapsulates details about a booking,
 * including whether an accessible seat is needed.
 */
public class BookingDetails {
    /**
     * The booking associated with the booking details.
     */
    private Booking booking;

    /**
     * Indicates whether an accessible seat is required.
     */
    private boolean needsAccessibleSeat;

    /**
     * Constructs a BookingDetails instance with the specified booking and accessibility requirement.
     *
     * @param booking             the booking object associated with this booking details
     * @param needsAccessibleSeat true if an accessible seat is required; false otherwise
     */
    public BookingDetails(Booking booking, boolean needsAccessibleSeat) {
        this.booking = booking;
        this.needsAccessibleSeat = needsAccessibleSeat;
    }

    /**
     * Returns a representative seat from the booking (e.g., the first seat).
     *
     * @return the first {@link Seat} in the booking, or null if no seats are available
     */
    public Seat getBookingSeat() {
        List<Seat> seats = booking.getSeats();
        if (seats != null && !seats.isEmpty()) {
            return seats.get(0);
        }
        return null;
    }

    /**
     * Checks whether an accessible seat is required.
     *
     * @return true if an accessible seat is needed; false otherwise
     */
    public boolean isNeedsAccessibleSeat() {
        return needsAccessibleSeat;
    }

    /**
     * Gets the booking associated with this booking details.
     *
     * @return the booking object
     */
    public Booking getBooking() {
        return booking;
    }

    /**
     * Sets the booking associated with this booking details.
     *
     * @param booking the booking object to set
     */
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
