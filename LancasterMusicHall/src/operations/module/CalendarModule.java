package operations.module;

import operations.entities.Booking;
import operations.entities.Activity;
import operations.entities.Venue;

import java.util.ArrayList;
import java.util.List;

public class CalendarModule {
    // In-memory list of bookings (for demo purposes)
    private List<Booking> bookings;

    public CalendarModule() {
        bookings = new ArrayList<>();
        // Sample data
        // Create an Activity and a Venue with all required fields
        Activity activity1 = new Activity(1, "Concert");
        Venue venue1 = new Venue(1, "Main Hall", "Hall", 300);
        Booking booking1 = new Booking(101, "2025-03-01", "2025-03-01", activity1, venue1, false, null);
        bookings.add(booking1);

        Activity activity2 = new Activity(2, "Film Screening");
        Venue venue2 = new Venue(2, "Small Hall", "Hall", 150);
        Booking booking2 = new Booking(102, "2025-03-02", "2025-03-02", activity2, venue2, true, "2025-03-29");
        bookings.add(booking2);
    }

    // Returns all bookings whose start and end dates fall within the specified range.
    public List<Booking> getBookingsForDate(String startDate, String endDate) {
        List<Booking> results = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getStartDate().compareTo(startDate) >= 0 && b.getEndDate().compareTo(endDate) <= 0) {
                results.add(b);
            }
        }
        return results;
    }

    // Returns a booking by its unique ID.
    public Booking getBookingById(int bookingId) {
        for (Booking b : bookings) {
            if (b.getId() == bookingId) {
                return b;
            }
        }
        return null;
    }

    // Returns a booking associated with the given activity ID.
    public Booking getBookingByActivityId(int activityId) {
        for (Booking b : bookings) {
            if (b.getActivity() != null && b.getActivity().getActivityId() == activityId) {
                return b;
            }
        }
        return null;
    }

    // Returns all bookings.
    public List<Booking> getAllBookings() {
        return bookings;
    }

    // Simulate scheduling a film by creating a new booking.
    public boolean scheduleFilm(int filmId, String proposedDate) {
        // For simplicity, simulate by creating a new booking with a Film activity.
        Activity filmActivity = new Activity(filmId, "Film Screening");
        // Use Main Hall as default for film screenings
        Venue mainHall = new Venue(1, "Main Hall", "Hall", 300);
        Booking newBooking = new Booking(200 + filmId, proposedDate, proposedDate, filmActivity, mainHall, false, null);
        bookings.add(newBooking);
        System.out.println("Scheduled film booking: " + newBooking.getId());
        return true;
    }
}
