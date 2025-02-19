package boxoffice;

import operations.entities.Booking;
import operations.entities.FinancialRecord;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxOfficeService implements BoxOfficeInterface {

    // --- Calendar Access ---
    @Override
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        // Simulate fetching bookings within the date range
        // In a real implementation, this would query a database or external system
        return List.of(
            new Booking(
                    1,                      // bookingId
                    "2025-03-01",           // dateStart
                    "2025-03-02",           // dateEnd
                    101,                    // userId
                    201,                    // customerId
                    301,                    // venueId
                    401,                    // activityId
                    true,                   // held
                    "2025-03-29"            // holdExpiryDate
            ),
            new Booking(
                    2,                      // bookingId
                    "2025-04-01",           // dateStart
                    "2025-05-02",           // dateEnd
                    101,                    // userId
                    201,                    // customerId
                    301,                    // venueId
                    401,                    // activityId
                    true,                   // held
                    "2025-03-29"            // holdExpiryDate
            )
        );
    }

    @Override
    public boolean notifyBookingChanges(int bookingId, Booking updatedDetails) {
        // Simulate notifying the Box Office of booking changes
        System.out.println("Notified Box Office of changes to booking ID " + bookingId);
        return true; // Assume notification is always successful
    }

    // --- Ticket Management ---
    @Override
    public String sellTickets(int bookingId, int activityId, List<String> seatNumbers, String discountCode) {
        // Simulate selling tickets
        String confirmation = "Tickets sold for activity " + activityId + " with seats " + seatNumbers.toString();
        if (discountCode != null) {
            confirmation += " (Discount applied: " + discountCode + ")";
        }
        return confirmation;
    }

    @Override
    public Map<String, Boolean> checkSeatAvailability(int activityId) {
        // Simulate seat availability for the given activity
        Map<String, Boolean> availability = new HashMap<>();
        availability.put("A1", true);
        availability.put("A2", false);
        availability.put("A3", true);
        return availability;
    }

    // --- Group Bookings ---
    @Override
    public void holdGroupSeats(String groupName, int activityId, int numberOfSeats, String discountCode) {
        // Simulate holding group seats
        System.out.println("Group seats held for activity " + activityId + ": "
                + numberOfSeats + " seats for group " + groupName + " with discount " + discountCode);
    }

    // --- Refunds & Special Permissions ---
    @Override
    public boolean processRefund(int ticketId, String reason, double amount) {
        // Simulate refund processing
        System.out.println("Processed refund for ticket " + ticketId + " amount: " + amount);
        return true; // Assume refund is always successful
    }

    // --- Online Ticket Sales Integration ---
    @Override
    public boolean syncOnlineTicketSales() {
        // Simulate synchronization with an external ticketing system
        System.out.println("Synchronizing online ticket sales...");
        return true; // Assume synchronization is always successful
    }

    // --- Sales Monitoring ---
    @Override
    public FinancialRecord getSalesDashboardData() {
        // Simulate fetching sales dashboard data
        return new FinancialRecord(200.0, 100.0, 100.0); // 1000.0, 500.0, 500.0 Example revenue, cost, and profit
    }

    // --- Held & Accessible Seats ---
    @Override
    public boolean releaseHeldSeats(int bookingId, List<String> seatNumbers) {
        // Simulate releasing held seats
        System.out.println("Released held seats for booking ID " + bookingId + ": " + seatNumbers.toString());
        return true; // Assume release is always successful
    }

    // --- Revenue Info ---
    @Override
    public FinancialRecord getRevenueBreakdownForBooking(int bookingId) {
        // Simulate fetching revenue breakdown for a booking
        return new FinancialRecord(200.0, 100.0, 100.0); //  Example revenue, cost, and profit
    }

    // --- Refund Log ---
    @Override
    public boolean logRefund(int bookingId, int ticketId, double amount, String reason) {
        // Simulate logging a refund
        System.out.println("Logged refund for booking ID " + bookingId + ", ticket ID " + ticketId
                + ", amount: " + amount + ", reason: " + reason);
        return true; // Assume logging is always successful
    }

    // --- Guest Estimates ---
    @Override
    public int getGuestEstimatesForBooking(int bookingId) {
        // Simulate fetching guest estimates for a booking
        return 150; // Example guest estimate
    }
}