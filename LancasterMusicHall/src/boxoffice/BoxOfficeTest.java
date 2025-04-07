package boxoffice;

import Database.SQLConnection;
import operations.entities.Event;
import operations.entities.Room;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

public class BoxOfficeTest {
    public static void main(String[] args) {
        // Create a SQLConnection instance.
        SQLConnection sqlCon = new SQLConnection();

        // Instantiate BoxOfficeService with the SQLConnection.
        BoxOfficeService boxOfficeService = new BoxOfficeService(sqlCon);

        // 1. Test getEventsByDateRange
        System.out.println("=== Events between 2025-04-01 and 2025-04-30 ===");
        List<Event> events = boxOfficeService.getEventsByDateRange(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));
        for (Event e : events) {
            System.out.println(e);
        }

        // 2. Test notifyEventChanges: if there is at least one event, update its name.
        if (!events.isEmpty()) {
            Event event = events.get(0);
            System.out.println("\n=== Updating first event's name ===");
            String oldName = event.getName();
            event.setName(oldName + " - Updated");
            boolean updateSuccess = boxOfficeService.notifyEventChanges(event.getId(), event);
            System.out.println("Update success: " + updateSuccess);
        }

        // 3. Test getSeatingPlanForEvent: this will print out room details from the Room table.
        if (!events.isEmpty()) {
            int eventId = events.get(0).getId();
            System.out.println("\n=== Seating Plan (Room Details) for event ID " + eventId + " ===");
            // This method prints the venue name and room details and returns an empty list.
            List<?> seats = boxOfficeService.getSeatingPlanForEvent(eventId);
            System.out.println("Seating plan returned (empty list): " + seats);
        }

        // 4. Test getHeldAccessibleSeats: prints out accessibility details from the Venue.
        if (!events.isEmpty()) {
            int eventId = events.get(0).getId();
            System.out.println("\n=== Held Accessible Seats for event ID " + eventId + " ===");
            List<?> accessibleSeats = boxOfficeService.getHeldAccessibleSeats(eventId);
            System.out.println("Accessible seats: " + accessibleSeats);
        }

        // 5. Test getRoomDetailsForEvent: retrieve room details (capacity, seating type, etc.) for the event.
        if (!events.isEmpty()) {
            int eventId = events.get(0).getId();
            System.out.println("\n=== Room Details for event ID " + eventId + " ===");
            List<Room> rooms = boxOfficeService.getRoomDetailsForEvent(eventId);
            for (Room room : rooms) {
                System.out.println(room);
            }
        }

        // 6. Test fetchDailySheet: retrieve and print a summary of events for a specific day.
        System.out.println("\n=== Daily Sheet for 2025-04-15 ===");
        try (ResultSet rs = boxOfficeService.fetchDailySheet(LocalDate.of(2025, 4, 15))) {
            if (rs != null) {
                while (rs.next()) {
                    System.out.println("Event ID: " + rs.getInt("event_id") +
                            ", Name: " + rs.getString("name") +
                            ", Start Time: " + rs.getTime("start_time") +
                            ", End Time: " + rs.getTime("end_time"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
