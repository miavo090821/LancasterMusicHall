package operations;

import operations.entities.Booking;
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

        Booking newBooking = new Booking(
                101,
                "Rock Concert",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                "Main Hall",
                100,
                5000,
                List.of()
        );

        // Simulate adding the booking
        calendarModule.addBooking(newBooking);

        // Print out the booking details to verify
        System.out.println("New Booking Created: " + newBooking);
    }
}
