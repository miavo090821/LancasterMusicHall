package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BoxOfficeService implements BoxOfficeInterface {

    private final Map<Integer, Booking> bookings = new HashMap<>(); // Store bookings in memory
    private final FinancialRecord financialRecord = new FinancialRecord(1000.0, 500.0, 500.0); // Example financial data

    public BoxOfficeService() {
        // Populate some sample data
        List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 3, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );

        bookings.put(1, new Booking(1, "Concert A", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 3, 1), "Main Hall", 50, 5000, seats));
        bookings.put(2, new Booking(2, "Theatre B", LocalDate.of(2025, 3, 2), LocalDate.of(2025, 3, 2), "Theatre 1", 30, 3000, seats));
    }

    // --- Calendar Access ---
    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookings.values().stream()
                .filter(booking -> !booking.getStartDate().isBefore(startDate) && !booking.getStartDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    @Override
    public boolean notifyBookingChanges(int bookingId, Booking updatedDetails) {
        if (!bookings.containsKey(bookingId)) {
            System.err.println("Booking ID not found: " + bookingId);
            return false;
        }
        bookings.put(bookingId, updatedDetails);
        System.out.println("Booking updated successfully: " + bookingId);
        return true;
    }

    // --- Sales Monitoring ---
    @Override
    public FinancialRecord getSalesDashboardData() {
        return financialRecord; // Return stored financial data
    }

    // --- Revenue Info ---
    @Override
    public FinancialRecord getRevenueBreakdownForBooking(int bookingId) {
        if (!bookings.containsKey(bookingId)) {
            System.err.println("Invalid booking ID: " + bookingId);
            return new FinancialRecord(0, 0, 0);
        }
        return financialRecord; // Ideally, calculate per-booking revenue
    }

    // --- Seating Plans ---
    @Override
    public List<Seat> getSeatingPlanForBooking(int bookingId) {
        return bookings.containsKey(bookingId) ? bookings.get(bookingId).getSeats() : Collections.emptyList();
    }

    @Override
    public boolean updateSeatingPlan(int bookingId, List<Seat> updatedSeats) {
        if (!bookings.containsKey(bookingId)) {
            System.err.println("Booking ID not found: " + bookingId);
            return false;
        }
        bookings.get(bookingId).setSeats(updatedSeats);
        System.out.println("Seating plan updated for booking ID " + bookingId);
        return true;
    }
}
