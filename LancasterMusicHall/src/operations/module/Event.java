package operations.module;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import operations.entities.Booking;

public class Event {
    private String room;
    private LocalDate date;
    private LocalTime time;
    private String typeOfEvent; // e.g., "movie", "venue tour", etc.
    private String showing;     // For film events, the film title or similar.
    private List<Booking> bookings;

    public Event() {
        bookings = new ArrayList<>();
    }

    public Event(String room, LocalDate date, LocalTime time, String typeOfEvent, String showing) {
        this.room = room;
        this.date = date;
        this.time = time;
        this.typeOfEvent = typeOfEvent;
        this.showing = showing;
        this.bookings = new ArrayList<>();
    }

    public String getRoom() {
        return room;
    }
    public void setRoom(String room) {
        this.room = room;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }
    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getTypeOfEvent() {
        return typeOfEvent;
    }
    public void setTypeOfEvent(String typeOfEvent) {
        this.typeOfEvent = typeOfEvent;
    }

    public String getShowing() {
        return showing;
    }
    public void setShowing(String showing) {
        this.showing = showing;
    }

    public List<Booking> getBookings() {
        return bookings;
    }
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
}
