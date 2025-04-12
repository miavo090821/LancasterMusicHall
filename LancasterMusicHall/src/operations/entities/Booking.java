package operations.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents a booking for an activity at a venue.
 * <p>
 * The {@code Booking} class encapsulates details of a booking including the booking dates,
 * payment due date, associated activity and venue, seat assignments, and contact information.
 * </p>
 */
public class Booking {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate paymentDueDate; // Payment due date for the booking
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
     * Constructs a new {@code Booking} with the specified details.
     *
     * @param id              Unique booking ID.
     * @param startDate       The start date of the booking.
     * @param endDate         The end date of the booking.
     * @param paymentDueDate  The payment due date for the booking.
     * @param activity        The associated {@link Activity} for the booking.
     * @param venue           The associated {@link Venue} for the booking.
     * @param held            {@code true} if the booking is on hold; {@code false} if confirmed.
     * @param holdExpiryDate  The hold expiry date (empty string if not used).
     * @param seats           The list of {@link Seat} objects associated with the booking.
     * @param bookedBy        The staff ID (or name) that booked the event.
     * @param room            The room name (from the {@link Venue}).
     * @param companyName     The company name (from the Client).
     * @param contactDetails  The {@link ContactDetails} of the client.
     */
    public Booking(int id, LocalDate startDate, LocalDate endDate,
                   LocalDate paymentDueDate,
                   Activity activity, Venue venue, boolean held,
                   String holdExpiryDate, List<Seat> seats, String bookedBy,
                   String room, String companyName, ContactDetails contactDetails) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
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

    /**
     * Retrieves the unique booking ID.
     *
     * @return the booking ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique booking ID.
     *
     * @param id the booking ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the start date of the booking.
     *
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the booking.
     *
     * @param startDate the start date to set.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Retrieves the end date of the booking.
     *
     * @return the end date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the booking.
     *
     * @param endDate the end date to set.
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Retrieves the payment due date for the booking.
     *
     * @return the payment due date.
     */
    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }

    /**
     * Sets the payment due date for the booking.
     *
     * @param paymentDueDate the payment due date to set.
     */
    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    /**
     * Retrieves the associated activity for the booking.
     *
     * @return the {@link Activity} associated with the booking.
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the associated activity for the booking.
     *
     * @param activity the {@link Activity} to set.
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Retrieves the associated venue for the booking.
     *
     * @return the {@link Venue} of the booking.
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * Sets the associated venue for the booking.
     *
     * @param venue the {@link Venue} to set.
     */
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * Checks if the booking is currently held.
     *
     * @return {@code true} if the booking is held; {@code false} if it is confirmed.
     */
    public boolean isHeld() {
        return held;
    }

    /**
     * Sets the held status of the booking.
     *
     * @param held {@code true} to mark the booking as held; {@code false} to confirm the booking.
     */
    public void setHeld(boolean held) {
        this.held = held;
    }

    /**
     * Retrieves the hold expiry date.
     *
     * @return the hold expiry date as a string.
     */
    public String getHoldExpiryDate() {
        return holdExpiryDate;
    }

    /**
     * Sets the hold expiry date.
     *
     * @param holdExpiryDate the hold expiry date to set.
     */
    public void setHoldExpiryDate(String holdExpiryDate) {
        this.holdExpiryDate = holdExpiryDate;
    }

    /**
     * Retrieves the list of seats associated with the booking.
     *
     * @return the list of {@link Seat} objects.
     */
    public List<Seat> getSeats() {
        return seats;
    }

    /**
     * Sets the list of seats associated with the booking.
     *
     * @param seats the list of {@link Seat} objects to set.
     */
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    /**
     * Retrieves the identifier of the staff who booked the event.
     *
     * @return the staff ID (or name) as a string.
     */
    public String getBookedBy() {
        return bookedBy;
    }

    /**
     * Sets the identifier of the staff who booked the event.
     *
     * @param bookedBy the staff ID (or name) to set.
     */
    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    /**
     * Retrieves the room name from the venue.
     *
     * @return the room name as a string.
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room name from the venue.
     *
     * @param room the room name to set.
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Retrieves the company name from the client.
     *
     * @return the company name as a string.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name from the client.
     *
     * @param companyName the company name to set.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Retrieves the contact details for the client.
     *
     * @return the {@link ContactDetails} of the client.
     */
    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the contact details for the client.
     *
     * @param contactDetails the {@link ContactDetails} to set.
     */
    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    /**
     * Retrieves the name of the associated activity.
     *
     * @return the activity name if available; otherwise, "N/A".
     */
    public String getActivityName() {
        return (activity != null) ? activity.getName() : "N/A";
    }

    /**
     * Retrieves the identifier of the associated activity.
     *
     * @return the activity ID if available; otherwise, -1.
     */
    public int getActivityId() {
        return (activity != null) ? activity.getActivityId() : -1;
    }

    /**
     * Returns a string representation of the booking.
     * <p>
     * The string contains details such as booking ID, dates, payment due date, staff who booked the event,
     * room, company name, contact details, activity and venue information, held status, and seat assignments.
     * </p>
     *
     * @return A string representation of the booking.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Booking ID: ").append(id).append("\n");
        sb.append("Start Date: ").append(startDate).append("\n");
        sb.append("End Date: ").append(endDate).append("\n");
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
