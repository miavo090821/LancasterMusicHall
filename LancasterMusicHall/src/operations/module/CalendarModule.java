package operations.module;

import operations.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarModule {
    // In-memory list of bookings.
    // In the final system, this list should be populated from SQL rather than hard-coded.
    private List<Booking> bookings;

    public CalendarModule() {
        bookings = new ArrayList<>();

        // --- Sample Data for Testing (commented out) ---
        /*
        // Sample Activity and Venue (replace with your real objects)
        Activity movieActivity1 = new Activity(1, "Movie A");
        Activity movieActivity2 = new Activity(1, "Movie B");

        Venue hallVenue = new Venue(1, "Main Hall", "Hall", 300);
        java.util.List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
        );

        // Create Booking object sample data
        String bookedBy = "Operations";
        String primaryContact = "phone";
        String telephone = "073323523"; // random number
        String email = "CinemaLtd@gmail.com";
        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);

        String room = "Hall";
        String companyName = "Cinema Ltd";

        // Create a sample booking and add to the list
        Booking booking1 = new Booking(
                101,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(10, 20),
                LocalTime.of(12, 20),
                movieActivity1,
                hallVenue,
                true,   // held (or confirmed) flag
                "",     // holdExpiryDate empty if not used
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );
        bookings.add(booking1);

        Activity activity2 = new Activity(2, "Film Screening");
        Venue venue2 = new Venue(2, "Small Hall", "Hall", 150);
        Booking booking2 = new Booking(
                102,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(10, 20),
                LocalTime.of(17, 20), // Adjusted end time
                movieActivity2,
                hallVenue,
                true,
                "",     // holdExpiryDate empty
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );
        bookings.add(booking2);
        */
    }

    // Returns all bookings whose start and end dates fall within the specified range.
    public List<Booking> getBookingsForDate(LocalDate startDate, LocalDate endDate) {
        List<Booking> results = new ArrayList<>();
        for (Booking b : bookings) {
            if (!b.getStartDate().isBefore(startDate) && !b.getEndDate().isAfter(endDate)) {
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
    // In the final version, new bookings will be inserted via SQL.
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
        // For simplicity, simulate by creating a new booking with a Film activity.
        // In production, this data would be inserted into the database.
        /*
        Activity filmActivity = new Activity(filmId, "Film Screening");
        Venue mainHall = new Venue(1, "Main Hall", "Hall", 300);
        List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
        );
        String bookedBy = "Operations";
        String primaryContact = "phone";
        String telephone = "073323523";
        String email = "CinemaLtd@gmail.com";
        String room = "Hall";
        String companyName = "Cinema Ltd";
        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);

        Booking newBooking = new Booking(
                200 + filmId,
                proposedDate,
                proposedDate,
                LocalTime.of(10, 20),
                LocalTime.of(17, 20),
                filmActivity,
                mainHall,
                false,
                "",
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );
        bookings.add(newBooking);
        System.out.println("Scheduled film booking: " + newBooking.getId());
        return true;
        */
        return false;
    }

    // Adds a new booking to the calendar.
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
}
