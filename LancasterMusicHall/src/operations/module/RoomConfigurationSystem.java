package operations.module;

import operations.entities.Booking;
import operations.entities.DailySheet;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RoomConfigurationSystem {

    // Returns a sample room availability map for a given date.
    public Map<String, String> getRoomAvailability(LocalDate date) {
        Map<String, String> availability = new HashMap<>();
        availability.put("Main Hall", "Available");
        availability.put("Small Hall", "Booked");
        availability.put("Rehearsal Room", "Available");
        return availability;
    }

    // Returns a sample seating plan for the specified activity.
    public String getSeatingPlan(int activityId) {
        return "Seating plan for activity " + activityId + ": [A1, A2, A3, B1, B2, B3]";
    }

    // Returns configuration details for the given booking.
    public String getConfiguration(Booking booking) {
        return "Configuration for booking " + booking.getId() +
                ": Layout - Theatre style; Equipment - Standard sound system.";
    }

    // Returns a sample string listing held spaces with expiry details.
    public String getHeldSpaces() {
        return "Held Spaces: Booking ID 102 (Small Hall) - Expires on 2025-03-29.";
    }

    // Generates a daily sheet (summary) for the given date.
    public DailySheet generateDailySheet(String date) {
        DailySheet sheet = new DailySheet();
        sheet.setDate(date);
        sheet.setBookings("Bookings on " + date + ": [Booking 101, Booking 102]");
        sheet.setConfigurations("Configurations: Theatre style, standard equipment.");
        sheet.setTotalRevenue(1500.00);
        return sheet;
    }
}
