package operations.entities;

import java.util.Date;
import java.util.List;

public class Booking {
     int bookingId;
     String title;
     String dateStart;
     String dateEnd;
     String location;
     int price;
     int capacity;
     List<Seat> seatingPlan;  // List of seats in the seating plan

    public Booking(int bookingId, String title, String dateStart, String dateEnd,
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

    // Constructors, Getters, Setters...
}
