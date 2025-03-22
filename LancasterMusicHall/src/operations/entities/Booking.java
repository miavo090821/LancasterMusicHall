package operations.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Booking {
    private int id;                    // Unique booking identifier
    private LocalDate startDate;          // Start LocalDate as a String (e.g., "2025-03-01")
    private LocalDate endDate;            // End LocalDate as a String
    private LocalTime startTime;          // Start time as a String (e.g., "2:20")
    private LocalTime endTime;            // End time as a String
    private Activity activity;         // Associated Activity object
    private Venue venue;               // Associated Venue object
    private boolean held;              // Indicates if the booking is on hold
    private String holdExpiryDate;     // Expiry LocalDate for the hold (or null if not applicable)
    private List<Seat> seats;          // List of seats associated with the booking

    // Default constructor
    public Booking() {
    }

    /**
     * Parameterized constructor with all fields.
     * @param id Unique booking ID.
     * @param startDate Start LocalDate as a String.
     * @param endDate End LocalDate as a String.
     * @param activity The associated Activity.
     * @param venue The associated Venue.
     * @param held Whether the booking is on hold.
     * @param holdExpiryDate The hold expiry LocalDate (or null).
     * @param seats List of seats for the booking.
     */
    public Booking(int id, LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime,
                   Activity activity, Venue venue, boolean held, String holdExpiryDate, List<Seat> seats) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.venue = venue;
        this.held = held;
        this.holdExpiryDate = holdExpiryDate;
        this.seats = seats;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns the name of the associated Activity.
     */
    public String getActivityName() {
        return (activity != null) ? activity.getName() : "N/A";
    }

    /**
     * Returns the ID of the associated Activity.
     */
    public int getActivityID() {
        return (activity != null) ? activity.getActivityId() : -1;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public boolean isHeld() {
        return held;
    }

    public void setHeld(boolean held) {
        this.held = held;
    }

    public String getHoldExpiryDate() {
        return holdExpiryDate;
    }

    public void setHoldExpiryDate(String holdExpiryDate) {
        this.holdExpiryDate = holdExpiryDate;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        StringBuilder seatingPlanStr = new StringBuilder();

        for (Seat seat : seats) {
            seatingPlanStr.append(seat.toString()); // Appends each seat on a new line
        }

        return "Booking: \n" +
                "id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", activity=" + (activity != null ? activity.getName() : "N/A") +
                ", venue=" + (venue != null ? venue.getName() : "N/A") +
                ", held=" + held +
                ", holdExpiryDate='" + holdExpiryDate + '\n' +
                "Seating plan:\n" + seatingPlanStr.toString();
    }
}
