package operations.entities;

public class Booking {
    private int id;  // Unique booking identifier
    private String startDate;
    private String endDate;
    private Activity activity; // Associated activity (e.g., Concert, Film Screening)
    private Venue venue;
    private boolean held;
    private String holdExpiryDate;

    // Default constructor
    public Booking() {
    }

    // Parameterized constructor
    public Booking(int id, String startDate, String endDate, Activity activity, Venue venue, boolean held, String holdExpiryDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activity = activity;
        this.venue = venue;
        this.held = held;
        this.holdExpiryDate = holdExpiryDate;
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

    // --- Activity Functions ---
    /**
     * Returns the associated Activity object.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the associated Activity object.
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns the name of the associated Activity.
     * @return The activity name, or "N/A" if no activity is associated.
     */
    public String getActivityName() {
        return (activity != null) ? activity.getName() : "N/A";
    }

    /**
     * Returns the ID of the associated Activity.
     * @return The activity ID if available, or -1 if no activity is associated.
     */
    public int getActivityID() {
        return (activity != null) ? activity.getActivityId() : -1;
    }

    // --- Venue Functions ---
    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    // --- Held & Expiry Functions ---
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

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", activity=" + (activity != null ? activity.getName() : "N/A") +
                ", venue=" + (venue != null ? venue.getName() : "N/A") +
                ", held=" + held +
                ", holdExpiryDate='" + holdExpiryDate + '\'' +
                '}';
    }
}
