//package operations;
//
//import boxoffice.BoxOfficeService;
//import operations.entities.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;
//
//public class BoxOfficeTest {
//    public static void main(String[] args) {
//        System.out.println("Lancaster Music Hall - Box Office Test");
//
//        // Create a BoxOfficeService instance
//        BoxOfficeService boxOffice = new BoxOfficeService();
//
//        // Test getBookingsByDateRange - convert LocalDate to String for our Booking constructor
//        System.out.println("\n--- Bookings Between 2025-03-01 and 2025-03-05 ---");
//        List<Booking> bookings = boxOffice.getBookingsByDateRange(
//                LocalDate.of(2025, 3, 1),
//                LocalDate.of(2025, 3, 5)
//        );
//        for (Booking booking : bookings) {
//            System.out.println(booking);
//        }
//
//
//        // Test notifyBookingChanges:
//        // Create updated Activity and Venue for the updated booking.
//        Activity updatedActivity = new Activity(1, "Updated Concert A");
//        Venue updatedVenue = new Venue(1, "Main Hall", "Hall", 300);
//        // Create a sample list of seats for the updated booking.
//        List<Seat> updatedSeats = List.of(
//                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
//                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
//        );
//        // Create Booking object
//        String bookedBy = "Operations";
//        String primaryContact = "phone";
//        String telephone = "073323523"; //random number
//        String email = "CinemaLtd@gmail.com";
//        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);
//        List<Seat> seats = List.of(
//                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
//                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
//        );
//        String room = "Hall";
//        String companyName = "Cinema Ltd";
//        // Create updated booking using the correct constructor with 8 parameters:
//        Booking updatedBooking = new Booking(
//                101,
//                LocalDate.of(2025, 3, 1),
//                LocalDate.of(2025, 3, 3),
//                LocalTime.of(10,20),
//                LocalTime.of(5,20),
//                updatedActivity,
//                updatedVenue,
//                true,
//                seats,
//                bookedBy,
//                room,
//                companyName,
//                contactDetails
//        );
//        System.out.println("\n--- Updating Booking ID 1 ---");
//        boolean updated = boxOffice.notifyBookingChanges(1, updatedBooking);
//        System.out.println("Update successful: " + updated);
//
//        // Test getSeatingPlanForBooking
//        System.out.println("\n--- Seating Plan for Booking ID 1 ---");
//        System.out.println("Seats: " + seats);
//
//        // Test updateSeatingPlan
//        System.out.println("\n--- Updating Seating Plan for Booking ID 1 ---");
//        List<Seat> newSeats = List.of(
//                new Seat('C', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
//                new Seat('C', 2, Seat.Type.WHEELCHAIR, Seat.Status.HELD),
//                new Seat('C', 3, Seat.Type.COMPANION, Seat.Status.HELD)
//        );
//        boolean seatUpdate = boxOffice.updateSeatingPlan(1, newSeats);
//        System.out.println("Seat update successful: " + seatUpdate);
//
//        // Test getHeldAccessibleSeats
//        System.out.println("\n--- Held Accessible Seats for Booking ID 1 ---");
//        List<Seat> heldAccessibleSeats = boxOffice.getHeldAccessibleSeats(1);
//        System.out.println("Held Accessible Seats: " + heldAccessibleSeats);
//    }
//}
