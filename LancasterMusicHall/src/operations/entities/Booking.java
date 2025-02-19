package operations.entities;

import java.time.LocalDate;

public class Booking {
    private int bookingId;           // ID field
    private String dateStart;        // Date Field
    private String dateEnd;          // Date Field
    private int userId;               // Staff user who created the booking
    private int customerId;       // Customer or external client
    private int venueId;             // Which venue/room/hall is booked
    private int activityId;       // Optional: link to a show, meeting, or other event
    private boolean held;            // True if it's just a "hold"
    private String holdExpiryDate;   // 28-day rule for final confirmation

    public Booking(int bookingId, String dateStart, String dateEnd, int userId, int customerId, int venueId, int activityId, boolean held, String holdExpiryDate) {
        this.bookingId = bookingId;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.userId = userId;
        this.customerId = customerId;
        this.venueId = venueId;
        this.activityId = activityId;
    }

    // Constructors, Getters, Setters...
}
