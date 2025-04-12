package operations.module;

import operations.entities.Booking;
import operations.entities.DailySheet;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * The RoomConfigurationSystem class provides methods for retrieving room availability,
 * seating plans, configuration details, held spaces, and generating daily sheets.
 * <p>
 * This is a sample implementation intended for demonstration purposes.
 * </p>
 */
public class RoomConfigurationSystem {

    /**
     * Initializes the room configuration system with default settings.
     * <p>
     * Subclasses should call this constructor to ensure proper initialization
     * of common configuration components.
     * </p>
     */
    public RoomConfigurationSystem() {
        // Base initialization logic
    }

    /**
     * Returns a sample room availability map for a given date.
     *
     * @param date the {@link LocalDate} for which room availability is requested
     * @return a {@link Map} where the key is the room name and the value is its availability status
     */
    public Map<String, String> getRoomAvailability(LocalDate date) {
        Map<String, String> availability = new HashMap<>();
        availability.put("Main Hall", "Available");
        availability.put("Small Hall", "Booked");
        availability.put("Rehearsal Room", "Available");
        return availability;
    }

    /**
     * Initializes a new configuration system with default settings.
     */
    public void ConfigurationSystem() {
    }


    /**
     * Returns a sample seating plan for the specified activity.
     *
     * @param activityId the unique identifier of the activity
     * @return a {@link String} representing the seating plan for the activity
     */
    public String getSeatingPlan(int activityId) {
        return "Seating plan for activity " + activityId + ": [A1, A2, A3, B1, B2, B3]";
    }

    /**
     * Returns configuration details for the given booking.
     *
     * @param booking the {@link Booking} for which configuration details are requested
     * @return a {@link String} containing configuration details such as layout and equipment information
     */
    public String getConfiguration(Booking booking) {
        return "Configuration for booking " + booking.getId() +
                ": Layout - Theatre style; Equipment - Standard sound system.";
    }

    /**
     * Returns a sample string listing held spaces with expiry details.
     *
     * @return a {@link String} listing held spaces along with expiry information
     */
    public String getHeldSpaces() {
        return "Held Spaces: Booking ID 102 (Small Hall) - Expires on 2025-03-29.";
    }

    /**
     * Generates a daily sheet summary for the given date.
     *
     * @param date the date for which the daily sheet is to be generated (in String format)
     * @return a {@link DailySheet} object containing the day's details including bookings, configurations, and total revenue
     */
    public DailySheet generateDailySheet(String date) {
        DailySheet sheet = new DailySheet();
        sheet.setDate(date);
        sheet.setBookings("Bookings on " + date + ": [Booking 101, Booking 102]");
        sheet.setConfigurations("Configurations: Theatre style, standard equipment.");
        sheet.setTotalRevenue(1500.00);
        return sheet;
    }
}
