package operations.module;

import operations.entities.Booking;
import operations.entities.Venue;

import java.util.List;

public class CalendarModule {
    // Responsible for creating, updating, and retrieving bookings
    // from the central calendar data structure.

    public void addBooking(Booking booking) { /* ... */ }
    public void updateBooking(Booking booking) { /* ... */ }
    public void cancelBooking(int bookingId) { /* ... */ }
    public List<Booking> getBookingsForDate(String startDate, String endDate) { /* ... */ return null; }
    public boolean isAvailable(Venue venue, String startDate, String endDate) { /* ... */ return false; }

    public Booking getBookingById(int bookingId) {
        return null;
    }
}
