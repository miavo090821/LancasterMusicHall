package operations.module;

import operations.entities.Booking;
import operations.entities.Seat;
import java.util.List;

public class BookingDetails {
    private Booking booking;
    private boolean needsAccessibleSeat;

    public BookingDetails(Booking booking, boolean needsAccessibleSeat) {
        this.booking = booking;
        this.needsAccessibleSeat = needsAccessibleSeat;
    }

    // Return a representative seat from the booking (e.g., the first seat).
    public Seat getBookingSeat() {
        List<Seat> seats = booking.getSeats();
        if (seats != null && !seats.isEmpty()) {
            return seats.get(0);
        }
        return null;
    }

    public boolean isNeedsAccessibleSeat() {
        return needsAccessibleSeat;
    }

    // Optional getters/setters for booking, etc.
    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }
}
