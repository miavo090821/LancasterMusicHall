package operations;

import operations.entities.*;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.ReviewManager;
import operations.module.RoomConfigurationSystem;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LancasterMusicHallApp {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall Desktop Application - Operations Team");

        // Initialize core modules
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfig = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();
        ReviewManager reviewManager = new ReviewManager();

        // The following sample data is now commented out because event data will be retrieved from SQL.
        /*
        // Create a new user (staff) - sample test data.
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

        // Create sample booking details
        String bookedBy = "Operations"; // staff ID or name
        String primaryContact = "phone";
        String telephone = "073323523"; // random number
        String email = "CinemaLtd@gmail.com";
        String room = "Hall";
        String companyName = "Cinema Ltd";
        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);

        // Create a new Booking using the updated constructor
        Booking newBooking = new Booking(
                101,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(10, 20),
                LocalTime.of(17, 20),
                rockConcert,
                mainHall,
                false,
                "",          // holdExpiryDate (empty if not used)
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );

        // Simulate adding the booking to the calendar module.
        calendarModule.addBooking(newBooking);

        // Print out the booking details for verification.
        System.out.println("New Booking Created: " + newBooking);
        */

        // In the final integrated system, the CalendarModule will fetch event data from the SQL database.
    }
}
