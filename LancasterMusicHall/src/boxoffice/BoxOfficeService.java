package boxoffice;

import operations.entities.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class BoxOfficeService implements BoxOfficeInterface {

    private final Map<Integer, Booking> bookings = new HashMap<>();
    private static int filmId = 1; // Added filmId as it was being used but not declared

    public BoxOfficeService() {
        // Sample contact details
        String bookedBy = "Operations";
        String primaryContact = "John Smith";
        String telephone = "073323523";
        String email = "CinemaLtd@gmail.com";
        String room = "Hall";
        String companyName = "Cinema Ltd";
        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);

        // Sample seats
        List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 3, Seat.Type.WHEELCHAIR, Seat.Status.AVAILABLE)
        );

        // Sample activities
        Activity filmActivity = new Activity(filmId, "Film Screening");
        Activity concertActivity = new Activity(2, "Concert A");
        Activity theatreActivity = new Activity(3, "Theatre B");

        // Sample venues
        Venue mainHall = new Venue(1, "Main Hall", "Hall", 300);
        Venue theatre1 = new Venue(2, "Theatre 1", "Theatre", 200);

        // Create sample bookings using the updated Booking constructor
        Booking booking1 = new Booking(
                1,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 1),
                LocalTime.of(10, 20),
                LocalTime.of(17, 20), // Fixed end time (was 5:20 which is earlier than start)
                concertActivity,
                mainHall,
                true, // confirmed
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );

        Booking booking2 = new Booking(
                2,
                LocalDate.of(2025, 3, 2),
                LocalDate.of(2025, 3, 2),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                theatreActivity,
                theatre1,
                true, // confirmed
                seats,
                bookedBy,
                room,
                companyName,
                contactDetails
        );

        bookings.put(1, booking1);
        bookings.put(2, booking2);

    }

    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookings.values().stream()
                .filter(booking -> {
                    LocalDate bookingDate = booking.getStartDate();
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

    @Override
    public List<Seat> getSeatingPlanForBooking(int bookingId) {
        return bookings.containsKey(bookingId) ?
                bookings.get(bookingId).getSeats() :
                Collections.emptyList();
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
        List<Seat> seatingPlan = getSeatingPlanForBooking(bookingId);
        if (seatingPlan == null) {
            return Collections.emptyList();
        }

        return seatingPlan.stream()
                .filter(seat -> seat.getType() == Seat.Type.WHEELCHAIR ||
                        seat.getType() == Seat.Type.COMPANION)
                .collect(Collectors.toList());
    }
}