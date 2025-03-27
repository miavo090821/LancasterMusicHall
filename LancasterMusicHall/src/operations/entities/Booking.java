package operations.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Booking {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Activity activity;
    private Venue venue;
    private boolean confirmed;
    private String holdExpiryDate;
    private List<Seat> seats;
    private String room;
    private String bookedBy;
    private String companyName;
    private ContactDetails contactDetails;


    /**
     * Parameterized constructor with all fields.
     * @param id Unique booking ID.
     * @param startDate Start LocalDate as a String.
     * @param endDate End LocalDate as a String.
     * @param activity The associated Activity.
     * @param venue The associated Venue.
     * @param confirmed Whether the booking is on hold.
     * @param seats List of seats for the booking.
     */
    public Booking(int id, LocalDate startDate, LocalDate endDate,
                   LocalTime startTime, LocalTime endTime,
                   Activity activity, Venue venue, boolean confirmed,
                   List<Seat> seats, String bookedBy, String room, String companyName,
                   ContactDetails contactDetails) {  // Changed to use ContactDetails
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.activity = activity;
        this.venue = venue;
        this.confirmed = confirmed;
        this.seats = seats;
        this.bookedBy = bookedBy;
        this.room = room;
        this.companyName = companyName;
        this.contactDetails = contactDetails;  // Set the ContactDetails object
    }

    // Complete getters and setters
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    // Additional helper methods
    public String getActivityName() {
        return (activity != null) ? activity.getName() : "N/A";
    }

    public int getActivityId() {
        return (activity != null) ? activity.getActivityId() : -1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID: ").append(id).append("\n");
        sb.append("Start Date: ").append(startDate).append("\n");
        sb.append("End Date: ").append(endDate).append("\n");
        sb.append("Start Time: ").append(startTime).append("\n");
        sb.append("End Time: ").append(endTime).append("\n");
        sb.append("Booked By: ").append(bookedBy).append("\n");
        sb.append("Room: ").append(room).append("\n");
        sb.append("Company Name: ").append(companyName).append("\n");

        if (contactDetails != null) {
            sb.append("Contact Details:\n");
            sb.append("  Primary Contact: ").append(contactDetails.getPrimaryContact()).append("\n");
            sb.append("  Telephone: ").append(contactDetails.getTelephone()).append("\n");
            sb.append("  Email: ").append(contactDetails.getEmail()).append("\n");
        }

        sb.append("Activity: ").append(getActivityName()).append("\n");
        sb.append("Venue: ").append(venue != null ? venue.getName() : "N/A").append("\n");
        sb.append("Confirmed: ").append(confirmed).append("\n");
        sb.append("Hold Expiry Date: ").append(holdExpiryDate).append("\n");

        if (seats != null && !seats.isEmpty()) {
            sb.append("Seats:\n");
            for (Seat seat : seats) {
                sb.append("  ").append(seat).append("\n");
            }
        }

        return sb.toString();
    }

    public int getActivityID() {
        return activity.getActivityId();
    }
}