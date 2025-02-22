package operations;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Seat;
import operations.entities.Venue;
import operations.entities.User;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.ReviewManager;
import operations.module.RoomConfigurationSystem;

import java.time.LocalDate;
import java.util.List;

public class LancasterMusicHallApp {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall Desktop Application - Operations Team");

        // Initialize core modules
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfig = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();
        ReviewManager reviewManager = new ReviewManager();

        // Example: Create a new booking and add it to the calendar
        User staff = new User(1, "Alice Johnson", "alice@lancasterhall.com", "Operations", "pass123");

        // Create an Activity and a Venue for the booking
        Activity rockConcert = new Activity(3, "Rock Concert");
        Venue mainHall = new Venue(1, "Main Hall", "Hall", 300);

        // Create a sample list of seats (replace with actual seat data if available)
        List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 3, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );

        // Create a new Booking using the updated constructor (8 parameters)
        Booking newBooking = new Booking(
                101,
                LocalDate.of(2025, 3, 1).toString(),
                LocalDate.of(2025, 3, 3).toString(),
                rockConcert,
                mainHall,
                false,
                null,
                seats
        );

        // Simulate adding the booking
        calendarModule.addBooking(newBooking);

        // Print out the booking details to verify
        System.out.println("New Booking Created: " + newBooking);
    }
}
