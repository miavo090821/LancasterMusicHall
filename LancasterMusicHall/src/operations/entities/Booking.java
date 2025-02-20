package operations.entities;

import java.time.LocalDate;
import java.util.List;

public class Booking {
    private int bookingId;
    private String title;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private String location;
    private int price;
    private int capacity;
    private List<Seat> seatingPlan; // List of seats in the seating plan

    public Booking(int bookingId, String title, LocalDate dateStart, LocalDate dateEnd,
                   String location, int price, int capacity, List<Seat> seatingPlan) {
        this.bookingId = bookingId;
        this.title = title;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.location = location;
        this.price = price;
        this.capacity = capacity;
        this.seatingPlan = seatingPlan;
    }

    public LocalDate getStartDate() {
        return dateStart;
    }

    public List<Seat> getSeats() {
        return seatingPlan;
    }

    public void setSeats(List<Seat> newSeats) {
        this.seatingPlan = newSeats;
    }

    // Getters and Setters
}
