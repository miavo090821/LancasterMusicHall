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

public class BoxOfficeService implements BoxOfficeInterface {

    private final SQLConnection sqlCon;

    public BoxOfficeService(SQLConnection sqlCon) {
        this.sqlCon = sqlCon;
    }

    // Retrieve events by date range from the database.
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

    // Internal method: update event details in the database.
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

    // Interface method: notifyEventChanges calls our internal updateEventDetails.
    @Override
    public boolean notifyEventChanges(int eventId, Event updatedDetails) {
        return updateEventDetails(eventId, updatedDetails);
    }

    // getSeatingPlanForEvent: For Box Office, we want to print out room details.
    // This method retrieves the venue_id from the event, prints the venue name and room details,
    // then returns an empty list (as the interface expects List<Seat>).
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

//    // updateSeatingPlan: Simply print out a message (no update).
//    @Override
//    public boolean updateSeatingPlan(int eventId, List<Seat> updatedSeats) {
//        System.out.println("No update performed for seating plan for event: " + eventId);
//        return false;
//    }

    // getHeldAccessibleSeats: Retrieve venue information from the Event's venue,
    // then check the Venue entity's is_flexible_seating and is_accessible flags.
    // Print out the values accordingly.
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

    // New method: Retrieve room details for the venue of an event.
    public List<Room> getRoomDetailsForEvent(int eventId) {
        int venueId = getVenueIdForEvent(eventId);
        return getRoomDetailsForVenue(venueId);
    }

    // Helper: Retrieve venue_id for a given event.
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

    // Helper: Retrieve the venue name from the Venue table.
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

    // Helper: Retrieve room details for a given venue.
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

    // New helper method: Fetch Daily Sheet (summary of events for a given date).
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


    @Override
    public boolean updateSeatingPlan(int eventId, List<Seat> updatedSeats) {
        // Already implemented above.
        return updateSeatingPlan(eventId, updatedSeats);
    }
}

