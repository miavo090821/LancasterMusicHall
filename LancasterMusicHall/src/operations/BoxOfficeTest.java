package operations;

import boxoffice.BoxOfficeService;
import operations.entities.Booking;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;

public class BoxOfficeTest {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall - Box Office Test");
        // Populate some sample data
        List<Seat> updatedSeats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 4, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 5, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );

        // Create a BoxOfficeService instance
        BoxOfficeService boxOffice = new BoxOfficeService();

        // Test getBookingsByDateRange
        System.out.println("\n--- Bookings Between 2025-03-01 and 2025-03-05 ---");
        List<Booking> bookings = boxOffice.getBookingsByDateRange(LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 5));
        for (Booking booking : bookings) {
            System.out.println(booking);
        }

        // Test notifyBookingChanges
        System.out.println("\n--- Updating Booking ID 1 ---");
        Booking updatedBooking = new Booking(1, "Updated Concert A", LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 2), "Main Hall", 60, 5000, updatedSeats);
        boolean updated = boxOffice.notifyBookingChanges(1, updatedBooking);
        System.out.println("Update successful: " + updated);

        // Test getSalesDashboardData
        System.out.println("\n--- Sales Dashboard Data ---");
        System.out.println(boxOffice.getSalesDashboardData());

        // Test getSeatingPlanForBooking
        System.out.println("\n--- Seating Plan for Booking ID 1 ---");
        List<Seat> seats = boxOffice.getSeatingPlanForBooking(1);
        System.out.println("Seats: " + seats);

        // Test updateSeatingPlan
        System.out.println("\n--- Updating Seating Plan for Booking ID 1 ---");
        List<Seat> newSeats = List.of(
                new Seat('B', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('B', 2, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );
        boolean seatUpdate = boxOffice.updateSeatingPlan(1, newSeats);
        System.out.println("Seat update successful: " + seatUpdate);
    }

}
