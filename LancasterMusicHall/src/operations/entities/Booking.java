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
    private LocalDate paymentDueDate; // New field: Payment due date for the booking
    private Activity activity;
    private Venue venue;
    private boolean held;             // True if booking status is "held", false if confirmed
    private String holdExpiryDate;
    private List<Seat> seats;
    private String bookedBy;          // Staff ID (or name) who booked the event
    private String room;              // Room name (from Venue)
    private String companyName;       // Company name (from Client)
    private ContactDetails contactDetails;

    /**
     * Parameterized constructor with all fields.
     *
     * @param id Unique booking ID.
     * @param startDate Start date.
     * @param endDate End date.
     * @param startTime Start time.
     * @param endTime End time.
     * @param paymentDueDate Payment due date.
     * @param activity Associated Activity.
     * @param venue Associated Venue.
     * @param held True if the booking is on hold; false if confirmed.
     * @param holdExpiryDate Hold expiry date (empty string if not used).
     * @param seats List of seats for the booking.
     * @param bookedBy Staff ID (as String) who booked it.
     * @param room Room name (from Venue).
     * @param companyName Company name (from Client).
     * @param contactDetails Contact details (from Client).
     */
    public Booking(int id, LocalDate startDate, LocalDate endDate,
                   LocalTime startTime, LocalTime endTime, LocalDate paymentDueDate,
                   Activity activity, Venue venue, boolean held,
                   String holdExpiryDate, List<Seat> seats, String bookedBy,
                   String room, String companyName, ContactDetails contactDetails) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.paymentDueDate = paymentDueDate;
        this.activity = activity;
        this.venue = venue;
        this.held = held;
        this.holdExpiryDate = holdExpiryDate;
        this.seats = seats;
        this.bookedBy = bookedBy;
        this.room = room;
        this.companyName = companyName;
        this.contactDetails = contactDetails;
    }

    // Getters and setters

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

    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
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

    public String getBookedBy() {
        return bookedBy;
    }

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
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
        sb.append("Payment Due Date: ").append(paymentDueDate != null ? paymentDueDate : "N/A").append("\n");
        sb.append("Booked By (Staff ID): ").append(bookedBy).append("\n");
        sb.append("Room: ").append(room).append("\n");
        sb.append("Company Name: ").append(companyName).append("\n");
        if (contactDetails != null) {
            sb.append("Contact Details:\n");
            sb.append("  Primary Contact: ").append(contactDetails.getPrimaryContact()).append("\n");
            sb.append("  Telephone: ").append(contactDetails.getTelephone()).append("\n");
            sb.append("  Email: ").append(contactDetails.getEmail()).append("\n");
        }
        sb.append("Activity: ").append(getActivityName()).append("\n");
        sb.append("Venue: ").append(venue != null ? venue.getVenueName() : "N/A").append("\n");
        sb.append("Held: ").append(held).append("\n");
        sb.append("Hold Expiry Date: ").append(holdExpiryDate).append("\n");
        if (seats != null && !seats.isEmpty()) {
            sb.append("Seats:\n");
            for (Seat seat : seats) {
                sb.append("  ").append(seat.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
