package operations.entities.Activities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import operations.entities.ContactDetails;
import operations.entities.Seat;
import operations.entities.Venue;

public class Event {
    private int id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean held;
    private String holdExpiryDate;
    private Venue venue;
    private List<Seat> seats;
    private String bookedBy;
    private String room;
    private String companyName;
    private ContactDetails contactDetails;

    public Event() {
    }


    public Event(int id, String name, LocalDate startDate, LocalDate endDate,
                 LocalTime startTime, LocalTime endTime, boolean held,
                 String holdExpiryDate, Venue venue, List<Seat> seats,
                 String bookedBy, String room, String companyName,
                 ContactDetails contactDetails) {
        this.id = id;
        this.name = name;
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
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public String getCompanyName() {
        return companyName;
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

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
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
                '}';
    }
}
