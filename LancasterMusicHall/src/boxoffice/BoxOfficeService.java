package boxoffice;

import Database.SQLConnection;
import operations.entities.Event;
import operations.entities.Room;
import operations.entities.Seat;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code BoxOfficeService} class provides services to query and update box office-related
 * event information stored in the database. It implements the {@code BoxOfficeInterface}.
 * <p>
 * This class uses an instance of {@code SQLConnection} to obtain database connections and
 * execute SQL queries related to events, rooms, and venues.
 * </p>
 */
public class BoxOfficeService implements BoxOfficeInterface {

    private final SQLConnection sqlCon;

    /**
     * Constructs a new {@code BoxOfficeService} using the provided SQL connection.
     *
     * @param sqlCon the {@code SQLConnection} object used for database communication
     */
    public BoxOfficeService(SQLConnection sqlCon) {
        this.sqlCon = sqlCon;
    }

    /**
     * Retrieves a list of events from the database that occur within the specified date range.
     * <p>
     * The method executes a SQL query to find events where the start date falls between the given
     * start and end dates.
     * </p>
     *
     * @param startDate the start date for filtering events
     * @param endDate   the end date for filtering events
     * @return a {@code List} of {@code Event} objects within the date range; an empty list if no events are found
     */
    @Override
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Event> eventList = new ArrayList<>();
        String query = "SELECT event_id, name, start_date, end_date, start_time, end_time, event_type, location, description, layout, venue_id, max_discount " +
                "FROM Event WHERE start_date BETWEEN ? AND ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            ps.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String name = rs.getString("name");
                LocalDate sDate = rs.getDate("start_date").toLocalDate();
                LocalDate eDate = rs.getDate("end_date").toLocalDate();
                LocalTime sTime = rs.getTime("start_time").toLocalTime();
                LocalTime eTime = rs.getTime("end_time").toLocalTime();
                String eventType = rs.getString("event_type");
                String location = rs.getString("location");
                String description = rs.getString("description");
                String layout = rs.getString("layout");
                double maxDiscount = rs.getDouble("max_discount");
                int venueId = rs.getInt("venue_id");
                // Create a basic Venue using location as name.
                operations.entities.Venue venue = new operations.entities.Venue(venueId, location, location, 0, "N/A", false, false, 0.0);
                Event event = new Event(eventId, name, eventType, sDate, eDate, sTime, eTime, false, "", venue, null, "", location, "", null, maxDiscount, description, layout);
                eventList.add(event);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return eventList;
    }

    /**
     * Updates the event details for a given event in the database.
     * <p>
     * This internal method constructs and executes an UPDATE SQL statement to modify the event details.
     * </p>
     *
     * @param eventId        the ID of the event to update
     * @param updatedDetails an {@code Event} object containing the new details for the event
     * @return {@code true} if the event details were updated successfully; {@code false} otherwise
     */
    public boolean updateEventDetails(int eventId, Event updatedDetails) {
        String updateQuery = "UPDATE Event SET name = ?, start_date = ?, end_date = ?, start_time = ?, end_time = ?, location = ?, description = ?, layout = ? WHERE event_id = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setString(1, updatedDetails.getName());
            ps.setDate(2, java.sql.Date.valueOf(updatedDetails.getStartDate()));
            ps.setDate(3, java.sql.Date.valueOf(updatedDetails.getEndDate()));
            ps.setTime(4, java.sql.Time.valueOf(updatedDetails.getStartTime()));
            ps.setTime(5, java.sql.Time.valueOf(updatedDetails.getEndTime()));
            ps.setString(6, updatedDetails.getRoom());
            ps.setString(7, updatedDetails.getDescription());
            ps.setString(8, updatedDetails.getLayout());
            ps.setInt(9, eventId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Event updated successfully: " + eventId);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Notifies the system of event changes by updating the event details.
     * <p>
     * This method acts as a wrapper to call the internal {@code updateEventDetails} method.
     * </p>
     *
     * @param eventId        the ID of the event to update
     * @param updatedDetails an {@code Event} object containing the new details for the event
     * @return {@code true} if the event was updated successfully; {@code false} otherwise
     */
    @Override
    public boolean notifyEventChanges(int eventId, Event updatedDetails) {
        return updateEventDetails(eventId, updatedDetails);
    }

    /**
     * Retrieves the seating plan for an event.
     * <p>
     * This method prints out the venue name and room details associated with the event.
     * As per the interface, it returns an empty list of {@code Seat} objects.
     * </p>
     *
     * @param eventId the ID of the event for which the seating plan is requested
     * @return an empty {@code List} of {@code Seat} objects
     */
    @Override
    public List<Seat> getSeatingPlanForEvent(int eventId) {
        int venueId = getVenueIdForEvent(eventId);
        if (venueId == 0) {
            System.err.println("No valid venue found for event: " + eventId);
            return Collections.emptyList();
        }
        String venueName = getVenueName(venueId);
        System.out.println("Venue Name: " + venueName);
        List<Room> rooms = getRoomDetailsForVenue(venueId);
        for (Room room : rooms) {
            System.out.println(room);
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves held accessible seats for an event.
     * <p>
     * This method retrieves venue information based on the event's venue and checks if the venue's seating is accessible.
     * It prints out whether the venue is accessible or not and returns an empty list of {@code Seat} objects.
     * </p>
     *
     * @param eventId the ID of the event for which to retrieve accessible seats
     * @return an empty {@code List} of {@code Seat} objects
     */
    @Override
    public List<Seat> getHeldAccessibleSeats(int eventId) {
        int venueId = getVenueIdForEvent(eventId);
        if (venueId == 0) {
            System.err.println("No valid venue found for event: " + eventId);
            return Collections.emptyList();
        }
        String query = "SELECT is_flexible_seating, is_accessible FROM Venue WHERE venue_id = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, venueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean flexible = rs.getBoolean("is_flexible_seating");
                boolean accessible = rs.getBoolean("is_accessible");
                if (accessible) {
                    System.out.println("Venue " + getVenueName(venueId) + " is accessible.");
                } else {
                    System.out.println("Venue " + getVenueName(venueId) + " is not accessible. Updating seating plan...");
                }
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Retrieves the room details for the venue associated with an event.
     *
     * @param eventId the ID of the event
     * @return a {@code List} of {@code Room} objects with the room details of the venue; may be empty if no details found
     */
    public List<Room> getRoomDetailsForEvent(int eventId) {
        int venueId = getVenueIdForEvent(eventId);
        return getRoomDetailsForVenue(venueId);
    }

    /**
     * Helper method to retrieve the venue ID for a given event.
     * <p>
     * This method queries the database for the venue associated with the event.
     * </p>
     *
     * @param eventId the ID of the event
     * @return the venue ID if found; otherwise, {@code 0}
     */
    private int getVenueIdForEvent(int eventId) {
        String query = "SELECT venue_id FROM Event WHERE event_id = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int vId = rs.getInt("venue_id");
                rs.close();
                return vId;
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * Helper method to retrieve the venue name for a given venue ID.
     * <p>
     * This method queries the database for the venue name from the {@code Venue} table.
     * </p>
     *
     * @param venueId the ID of the venue
     * @return the venue name if found; otherwise, "Unknown Venue"
     */
    private String getVenueName(int venueId) {
        String query = "SELECT venue_name FROM Venue WHERE venue_id = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, venueId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("venue_name");
                rs.close();
                return name;
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Unknown Venue";
    }

    /**
     * Helper method to retrieve room details for a given venue.
     * <p>
     * The method executes a SQL query to get details for rooms associated with the specified venue.
     * </p>
     *
     * @param venueId the ID of the venue
     * @return a {@code List} of {@code Room} objects with room details; the list is empty if no rooms found
     */
    public List<Room> getRoomDetailsForVenue(int venueId) {
        List<Room> roomList = new ArrayList<>();
        String query = "SELECT room_name, venue_id, room_number, room_capacity, classroom_capacity, boardroom_capacity, presentation_capacity, seating_type " +
                "FROM Room WHERE venue_id = ?";
        try (Connection con = sqlCon.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, venueId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String roomName = rs.getString("room_name");
                int vId = rs.getInt("venue_id");
                Integer roomNumber = rs.getObject("room_number") != null ? rs.getInt("room_number") : null;
                Integer roomCapacity = rs.getObject("room_capacity") != null ? rs.getInt("room_capacity") : null;
                int classroomCapacity = rs.getInt("classroom_capacity");
                int boardroomCapacity = rs.getInt("boardroom_capacity");
                int presentationCapacity = rs.getInt("presentation_capacity");
                String seatingType = rs.getString("seating_type");
                Room room = new Room(roomName, vId, roomNumber, roomCapacity, classroomCapacity, boardroomCapacity, presentationCapacity, seatingType);
                roomList.add(room);
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return roomList;
    }

    /**
     * Fetches a daily sheet summarizing events for a specific date.
     * <p>
     * This method executes a SQL query to retrieve event information (ID, name, start time, and end time)
     * for the given date.
     * </p>
     *
     * @param date the date for which to fetch the daily sheet
     * @return a {@code ResultSet} containing the query results; {@code null} if an error occurs
     */
    public ResultSet fetchDailySheet(LocalDate date) {
        String query = "SELECT event_id, name, start_time, end_time FROM Event WHERE start_date = ?";
        try {
            Connection con = sqlCon.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(date));
            return ps.executeQuery();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the seating plan for an event.
     * <p>
     * <strong>Note:</strong> This method calls itself which seems to be a mistake. You may need to update
     * the implementation to avoid recursive calls. For now, this returns the value of a (recursive) call.
     * </p>
     *
     * @param eventId      the ID of the event whose seating plan is to be updated
     * @param updatedSeats a {@code List} of {@code Seat} objects representing the new seating plan
     * @return {@code true} if the seating plan was updated successfully; {@code false} otherwise
     */
    @Override
    public boolean updateSeatingPlan(int eventId, List<Seat> updatedSeats) {
        // Warning: The current implementation recursively calls itself.
        // Please review and update this implementation as needed.
        return updateSeatingPlan(eventId, updatedSeats);
    }
}
