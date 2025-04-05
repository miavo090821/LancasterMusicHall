package operations.module;

import operations.entities.ContactDetails;
import operations.entities.Seat;
import operations.entities.Venue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event {
    private int id;
    private String name;
    private String eventType;  // Allowed values: "Film", "Show", "Meeting"
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean held;
    private String holdExpiryDate;
    private Venue venue;
    private List<Seat> seats;
    private String room;
    private String description;    // New field for event description.
    private String layout;         // New field for event layout.
    private String companyName;
    private double price;          // New field for event price.
    private ContactDetails contactDetails;

    /**
     * Full parameterized constructor updated to include eventType, description, layout, and price.
     *
     * @param id             Unique identifier for the event.
     * @param name           Name of the event.
     * @param eventType      Type of event (Film, Show, or Meeting).
     * @param startDate      Start date of the event.
     * @param endDate        End date of the event.
     * @param startTime      Start time of the event.
     * @param endTime        End time of the event.
     * @param held           Flag indicating if the event is held.
     * @param holdExpiryDate Expiry date of the hold (if applicable).
     * @param venue          Venue of the event.
     * @param seats          List of seats.
     * @param room           Room or location identifier.
     * @param description    Description provided for the event.
     * @param layout         Layout information provided for the event.
     * @param companyName    Name of the company booking the event.
     * @param price          Calculated price for the event.
     */
    public Event(int id, String name, String eventType, LocalDate startDate, LocalDate endDate,
                 LocalTime startTime, LocalTime endTime, boolean held, String holdExpiryDate,
                 Venue venue, List<Seat> seats, String room, String description,
                 String layout, String companyName, double price) {
        this.id = id;
        this.name = name;
        setEventType(eventType); // Using setter to validate.
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.held = held;
        this.holdExpiryDate = holdExpiryDate;
        this.venue = venue;
        this.seats = seats;
        this.room = room;
        this.description = description;
        this.layout = layout;
        this.companyName = companyName;
        this.price = price;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEventType() {
        return eventType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public boolean isHeld() {
        return held;
    }

    public String getHoldExpiryDate() {
        return holdExpiryDate;
    }

    public Venue getVenue() {
        return venue;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public String getRoom() {
        return room;
    }

    /**
     * Returns the description for this event.
     *
     * @return the event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the layout for this event.
     *
     * @return the event layout.
     */
    public String getLayout() {
        return layout;
    }

    public String getCompanyName() {
        return companyName;
    }

    public double getPrice() {
        return price;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEventType(String eventType) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null.");
        }
        String lower = eventType.toLowerCase();
        if (!lower.equals("film") && !lower.equals("show") && !lower.equals("meeting")) {
            throw new IllegalArgumentException("Invalid event type. Allowed values: Film, Show, Meeting.");
        }
        this.eventType = capitalize(eventType);
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public void setHeld(boolean held) {
        this.held = held;
    }

    public void setHoldExpiryDate(String holdExpiryDate) {
        this.holdExpiryDate = holdExpiryDate;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

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
                ", room='" + room + '\'' +
                ", description='" + description + '\'' +
                ", layout='" + layout + '\'' +
                ", companyName='" + companyName + '\'' +
                ", price=" + price +
                ", contactDetails=" + contactDetails +
                '}';
    }
}
