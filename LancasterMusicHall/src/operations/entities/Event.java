package operations.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Represents an event with details such as event name, type, schedule, venue, and pricing.
 * <p>
 * The {@code Event} class encapsulates various attributes of an event including its
 * schedule, booking details, associated venue, seating, pricing, and additional information
 * like description and layout.
 * </p>
 */
public class Event {
    private int id;
    private String name;               // Event name (as provided by booking details)
    private String eventType;          // e.g., "Film", "Show", "Meeting"
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean held;
    private String holdExpiryDate;
    private Venue venue;
    private List<Seat> seats;
    private String bookedBy;
    private String room;               // e.g., the location (room name)
    private String companyName;
    private ContactDetails contactDetails;
    private double price;              // Calculated price for the event
    private String sortCode;

    // New fields added:
    private String description;        // Event description
    private String layout;             // Event layout

    /**
     * Default constructor for {@code Event}.
     * <p>
     * Creates an empty {@code Event} object.
     * </p>
     */
    public Event() {
    }

    /**
     * Constructs a new {@code Event} with the specified details.
     *
     * @param id              the unique identifier for the event.
     * @param name            the event name.
     * @param eventType       the type of event (e.g., "Film", "Show", "Meeting").
     * @param startDate       the start date of the event.
     * @param endDate         the end date of the event.
     * @param startTime       the starting time of the event.
     * @param endTime         the ending time of the event.
     * @param held            {@code true} if the event is held; {@code false} otherwise.
     * @param holdExpiryDate  the expiry date for the hold on the event.
     * @param venue           the associated {@link Venue} for the event.
     * @param seats           the list of {@link Seat} objects for the event.
     * @param bookedBy        the identifier of who booked the event.
     * @param room            the room name or location for the event.
     * @param companyName     the company name associated with the booking.
     * @param contactDetails  the {@link ContactDetails} for the event.
     * @param price           the calculated price for the event.
     * @param description     the description of the event.
     * @param layout          the layout details of the event.
     */
    public Event(int id, String name, String eventType, LocalDate startDate, LocalDate endDate,
                 LocalTime startTime, LocalTime endTime, boolean held, String holdExpiryDate,
                 Venue venue, List<Seat> seats, String bookedBy, String room,
                 String companyName, ContactDetails contactDetails, double price,
                 String description, String layout) {
        this.id = id;
        this.name = name;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.held = held;
        this.holdExpiryDate = holdExpiryDate;
        this.venue = venue;
        this.seats = seats;
        this.bookedBy = bookedBy;
        this.room = room;
        this.companyName = companyName;
        this.contactDetails = contactDetails;
        this.price = price;
        this.description = description;
        this.layout = layout;
    }

    /**
     * Retrieves the event ID.
     *
     * @return the event ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the event ID.
     *
     * @param id the event ID to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retrieves the event name.
     *
     * @return the event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the event name.
     *
     * @param name the event name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the event type.
     *
     * @return the event type.
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * Sets the event type.
     *
     * @param eventType the event type to set (e.g., "Film", "Show", "Meeting").
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Retrieves the start date of the event.
     *
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the start date of the event.
     *
     * @param startDate the start date to set.
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Retrieves the end date of the event.
     *
     * @return the end date.
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the end date of the event.
     *
     * @param endDate the end date to set.
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Retrieves the starting time of the event.
     *
     * @return the starting time.
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the starting time of the event.
     *
     * @param startTime the starting time to set.
     */
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the ending time of the event.
     *
     * @return the ending time.
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the ending time of the event.
     *
     * @param endTime the ending time to set.
     */
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Checks if the event is held.
     *
     * @return {@code true} if the event is held; {@code false} otherwise.
     */
    public boolean isHeld() {
        return held;
    }

    /**
     * Sets the held status of the event.
     *
     * @param held {@code true} to set the event as held; {@code false} for confirmed.
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
     * Retrieves the venue associated with the event.
     *
     * @return the {@link Venue} for the event.
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * Sets the venue associated with the event.
     *
     * @param venue the {@link Venue} to set.
     */
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * Retrieves the list of seats for the event.
     *
     * @return the list of {@link Seat} objects.
     */
    public List<Seat> getSeats() {
        return seats;
    }

    /**
     * Sets the list of seats for the event.
     *
     * @param seats the list of {@link Seat} objects to set.
     */
    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    /**
     * Retrieves the identifier of who booked the event.
     *
     * @return the booked by identifier.
     */
    public String getBookedBy() {
        return bookedBy;
    }

    /**
     * Sets the identifier of who booked the event.
     *
     * @param bookedBy the booked by identifier to set.
     */
    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    /**
     * Retrieves the room or location where the event takes place.
     *
     * @return the room name.
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the room or location for the event.
     *
     * @param room the room name to set.
     */
    public void setRoom(String room) {
        this.room = room;
    }

    /**
     * Retrieves the company name associated with the event booking.
     *
     * @return the company name.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the company name associated with the event booking.
     *
     * @param companyName the company name to set.
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Retrieves the contact details associated with the event.
     *
     * @return the {@link ContactDetails} for the event.
     */
    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    /**
     * Sets the contact details for the event.
     *
     * @param contactDetails the {@link ContactDetails} to set.
     */
    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    /**
     * Retrieves the price of the event.
     *
     * @return the price of the event.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the event.
     *
     * @param price the price to set.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Retrieves the sort code associated with the event.
     *
     * @return the sort code as a string.
     */
    public String getSortCode() {
        return sortCode;
    }

    /**
     * Sets the sort code associated with the event.
     *
     * @param sortCode the sort code to set.
     */
    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    /**
     * Retrieves the event description.
     *
     * @return the description of the event.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the event description.
     *
     * @param description the description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the layout details of the event.
     *
     * @return the layout information as a string.
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Sets the layout details for the event.
     *
     * @param layout the layout information to set.
     */
    public void setLayout(String layout) {
        this.layout = layout;
    }

    /**
     * Returns a string representation of the event.
     * <p>
     * The string representation includes all key details of the event such as the event ID, name,
     * type, schedule, venue information, seat assignments, booking details, price, and additional
     * information like description and layout.
     * </p>
     *
     * @return a string representation of the event.
     */
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eventType='" + eventType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", held=" + held +
                ", holdExpiryDate='" + holdExpiryDate + '\'' +
                ", venue=" + venue +
                ", seats=" + seats +
                ", bookedBy='" + bookedBy + '\'' +
                ", room='" + room + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactDetails=" + contactDetails +
                ", price=" + price +
                ", sortCode='" + sortCode + '\'' +
                ", description='" + description + '\'' +
                ", layout='" + layout + '\'' +
                '}';
    }
}
