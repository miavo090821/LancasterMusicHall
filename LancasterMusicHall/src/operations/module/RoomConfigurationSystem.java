package operations.module;

import operations.entities.Booking;
import operations.entities.DailySheet;
import operations.entities.Seat;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RoomConfigurationSystem {
    // Handles specifying and scheduling room setup details

    public void setConfiguration(Booking booking, String configurationDetails) { /* ... */ }
    public String getConfiguration(Booking booking) { /* ... */ return ""; }
    public DailySheet generateDailySheet(String date) { /* ... */ return null; }

    public Map<String, String> getRoomAvailability(String date) {
        return null;
    }

    public String getSeatingPlan(int activityId) {
        return null;
    }

    public String getHeldSpaces() {
        return null;
    }
}
