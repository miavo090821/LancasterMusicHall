package operations.module;

import operations.entities.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * The CalendarModule class manages an in-memory list of bookings and provides methods for querying
 * and scheduling bookings. In a complete system, the bookings would be loaded from and stored in a SQL database.
 */
public class CalendarModule {
    /**
     * In-memory list of bookings.
     */
    private List<Booking> bookings;

    /**
     * Constructs a new CalendarModule instance.
     * <p>
     * Initializes the bookings list. Note that sample data for testing is provided in the comments and
     * should be replaced with actual data loaded from the database in the final implementation.
     * </p>
     */
    public CalendarModule() {
        bookings = new ArrayList<>();

    }

    /**
     * Returns all bookings whose start and end dates fall within the specified date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate   the end date of the range (inclusive)
     * @return a list of {@link Booking} objects that fall within the specified date range
     */
    public List<Booking> getBookingsForDate(LocalDate startDate, LocalDate endDate) {
        List<Booking> results = new ArrayList<>();
        for (Booking b : bookings) {
            if (!b.getStartDate().isBefore(startDate) && !b.getEndDate().isAfter(endDate)) {
                results.add(b);
            }
        }
        return results;
    }

    /**
     * Returns the booking that has the given unique booking ID.
     *
     * @param bookingId the unique booking identifier
     * @return the {@link Booking} with the specified ID, or null if no such booking is found
     */
    public Booking getBookingById(int bookingId) {
        for (Booking b : bookings) {
            if (b.getId() == bookingId) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns the booking associated with the given activity ID.
     *
     * @param activityId the identifier of the activity
     * @return the {@link Booking} that contains the specified activity, or null if none is found
     */
    public Booking getBookingByActivityId(int activityId) {
        for (Booking b : bookings) {
            if (b.getActivity() != null && b.getActivity().getActivityId() == activityId) {
                return b;
            }
        }
        return null;
    }

    /**
     * Returns all bookings managed by the calendar.
     *
     * @return a list of all {@link Booking} objects
     */
    public List<Booking> getAllBookings() {
        return bookings;
    }

    /**
     * Simulates scheduling a film by creating a new booking for the given film ID on the proposed date.
     * <p>
     * In the final version, new bookings will be inserted into the database via SQL. For now, this method
     * uses a commented out sample implementation to demonstrate scheduling.
     * </p>
     *
     * @param filmId       the identifier of the film activity
     * @param proposedDate the proposed date for the film booking
     * @return true if the film was scheduled successfully, false otherwise
     */
    public boolean scheduleFilm(int filmId, LocalDate proposedDate) {

        return false;
    }

    /**
     * Adds a new booking to the calendar.
     *
     * @param booking the {@link Booking} object to be added
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
}
