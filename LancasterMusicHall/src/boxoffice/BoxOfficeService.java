package boxoffice;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.FinancialRecord;
import operations.entities.Seat;
import operations.entities.Venue;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BoxOfficeService implements BoxOfficeInterface {

    private final Map<Integer, Booking> bookings = new HashMap<>(); // Store bookings in memory
    private final FinancialRecord financialRecord;

    public BoxOfficeService() {
        // Populate some sample data
        List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 3, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );

        // Create sample bookings using the new Booking constructor:
        // Booking(int id, String startDate, String endDate, Activity activity, Venue venue, boolean held, String holdExpiryDate, List<Seat> seats)
        Booking booking1 = new Booking(
                1,
                LocalDate.of(2025, 3, 1).toString(),
                LocalDate.of(2025, 3, 1).toString(),
                new Activity(1, "Concert A"),
                new Venue(1, "Main Hall", "Hall", 300),
                false,
                null,
                seats
        );
        Booking booking2 = new Booking(
                2,
                LocalDate.of(2025, 3, 2).toString(),
                LocalDate.of(2025, 3, 2).toString(),
                new Activity(2, "Theatre B"),
                new Venue(2, "Theatre 1", "Theatre", 200),
                false,
                null,
                seats
        );
        bookings.put(1, booking1);
        bookings.put(2, booking2);

        // Initialize financialRecord using the updated FinancialRecord constructor:
        // FinancialRecord(int financialRecordId, Booking booking, double revenue, double cost, String date)
        // We'll use booking1 as our sample booking.
        financialRecord = new FinancialRecord(1, booking1, 1000.0, 500.0, booking1.getStartDate());
    }

    // --- Calendar Access ---
    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookings.values().stream()
                .filter(booking -> {
                    LocalDate bookingDate = LocalDate.parse(booking.getStartDate());
                    return (!bookingDate.isBefore(startDate)) && (!bookingDate.isAfter(endDate));
                })
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

    @Override
    public List<Seat> getHeldAccessibleSeats(int bookingId) {
        // Fetch the seating plan for the given booking
        List<Seat> seatingPlan = getSeatingPlanForBooking(bookingId);

        // Filter the seats to return only those designated as wheelchair accessible or companion seats
        List<Seat> filteredSeats = new ArrayList<>();
        for (Seat seat : seatingPlan) {
            if (seat.isWheelchairAccessible() || seat.isCompanionSeat()) {
                filteredSeats.add(seat);
            }
        }
        return filteredSeats;
    }
}
