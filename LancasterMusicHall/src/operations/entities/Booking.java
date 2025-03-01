package operations.entities;

import java.util.List;

public class Booking {
    private int id;                    // Unique booking identifier
    private String startDate;          // Start date as a String (e.g., "2025-03-01")
    private String endDate;            // End date as a String
    private Activity activity;         // Associated Activity object
    private Venue venue;               // Associated Venue object
    private boolean held;              // Indicates if the booking is on hold
    private String holdExpiryDate;     // Expiry date for the hold (or null if not applicable)
    private List<Seat> seats;          // List of seats associated with the booking

    // Default constructor
    public Booking() {
    }

    /**
     * Parameterized constructor with all fields.
     * @param id Unique booking ID.
     * @param startDate Start date as a String.
     * @param endDate End date as a String.
     * @param activity The associated Activity.
     * @param venue The associated Venue.
     * @param held Whether the booking is on hold.
     * @param holdExpiryDate The hold expiry date (or null).
     * @param seats List of seats for the booking.
     */
    public Booking(int id, String startDate, String endDate, Activity activity, Venue venue, boolean held, String holdExpiryDate, List<Seat> seats) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
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
                ", activity=" + (activity != null ? activity.getName() : "N/A") +
                ", venue=" + (venue != null ? venue.getName() : "N/A") +
                ", held=" + held +
                ", holdExpiryDate='" + holdExpiryDate + '\n' +
                "Seating plan:\n" + seatingPlanStr.toString();
    }
}
