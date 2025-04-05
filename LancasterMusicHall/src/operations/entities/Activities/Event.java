package operations.entities.Activities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import operations.entities.ContactDetails;
import operations.entities.Seat;
import operations.entities.Venue;

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

    public Event() {
    }

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

    public String getBookedBy() {
        return bookedBy;
    }

    public String getRoom() {
        return room;
    }

    public String getCompanyName() {
        return companyName;
    }

    public ContactDetails getContactDetails() {
        return contactDetails;
    }

    public double getPrice() {
        return price;
    }

    public String getSortCode() {
        return sortCode;
    }

    /**
     * Returns the event description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the event layout.
     *
     * @return the layout
     */
    public String getLayout() {
        return layout;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setContactDetails(ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    /**
     * Sets the event description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the event layout.
     *
     * @param layout the layout to set
     */
    public void setLayout(String layout) {
        this.layout = layout;
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
