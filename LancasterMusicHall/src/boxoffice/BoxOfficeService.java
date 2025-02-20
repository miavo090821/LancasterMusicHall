package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;

public class BoxOfficeService implements BoxOfficeInterface {

    // --- Calendar Access ---
    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Simulate fetching bookings within the date range
        // In a real implementation, this would query a database or external system
        return List.of(
            new Booking(
                    1,
                    "Hello",
                    "2025-03-01",
                    "2025-03-02",
                    "Hall",
                    10,
                    1000,
                    List.of(
                            new Seat(
                                    'a',
                                    1,
                                    Seat.Type.REGULAR
                            ),
                            new Seat(
                                    'a',
                                    2,
                                    Seat.Type.REGULAR
                            ),
                            new Seat(
                                    'a',
                                    3,
                                    Seat.Type.WHEELCHAIR
                            )
                    )

            ),
            new Booking(
                    1,
                    "Hello 2",
                    "2025-03-01",
                    "2025-03-02",
                    "Hall",
                    10,
                    1000,
                    List.of(
                            new Seat(
                                    'a',
                                    1,
                                    Seat.Type.REGULAR
                            ),
                            new Seat(
                                    'a',
                                    2,
                                    Seat.Type.REGULAR
                            ),
                            new Seat(
                                    'a',
                                    3,
                                    Seat.Type.WHEELCHAIR
                            )
                    )
            )
        );
    }

    @Override
    public boolean notifyBookingChanges(int bookingId, Booking updatedDetails) {
        // Simulate notifying the Box Office of booking changes
        System.out.println("Notified Box Office of changes to booking ID " + bookingId);
        return true; // Assume notification is always successful
    }

    // --- Sales Monitoring ---
    @Override
    public FinancialRecord getSalesDashboardData() {
        // Simulate fetching sales dashboard data
        return new FinancialRecord(200.0, 100.0, 100.0); // 1000.0, 500.0, 500.0 Example revenue, cost, and profit
    }

    // --- Revenue Info ---
    @Override
    public FinancialRecord getRevenueBreakdownForBooking(int bookingId) {
        // Simulate fetching revenue breakdown for a booking
        return new FinancialRecord(200.0, 100.0, 100.0); //  Example revenue, cost, and profit
    }

    @Override
    public List<Seat> getSeatingPlanForBooking(int bookingId) {
        return List.of();
    }

    @Override
    public boolean updateSeatingPlan(int bookingId, List<Seat> updatedSeats) {
        return false;
    }


}