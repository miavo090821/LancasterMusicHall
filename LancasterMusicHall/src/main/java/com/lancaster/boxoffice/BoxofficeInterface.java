package com.lancaster.boxoffice;

public interface BoxOfficeInterface {

    // --- Ticket Management ---
    /**
     * Sells a ticket (or multiple tickets) for a given show/film.
     * @param activityId The ID of the Activity (Show or Film).
     * @param seatNumbers List of seat identifiers (rows, seat numbers).
     * @param discountCode Optional discount code or type (NHS, Military, etc.).
     * @return A confirmation or ticket object/ID.
     */
    String sellTickets(int activityId, List<String> seatNumbers, String discountCode);

    /**
     * Checks seat availability for a given activity.
     * @param activityId The ID of the Activity (Show or Film).
     * @return A structure (list/map) of available vs. occupied seats.
     */
    Map<String, Boolean> checkSeatAvailability(int activityId);

    // --- Group Bookings ---
    /**
     * Manages group bookings (up to 12 seats). Larger groups go to Marketing.
     * @param activityId The ID of the Activity.
     * @param numberOfSeats Number of seats requested (<= 12).
     * @param discountCode Any special group discount code.
     */
    void holdGroupSeats(int activityId, int numberOfSeats, String discountCode);

    // --- Refunds & Special Permissions ---
    /**
     * Process a refund (partial or full).
     * Only accessible to Manager or Deputy (handled in your actual security logic).
     * @param ticketId ID of the ticket or transaction.
     * @param amount Amount to refund.
     * @return True if refund is successful.
     */
    boolean processRefund(String ticketId, double amount);

    // --- Online Ticket Sales Integration ---
    /**
     * Syncs seat sales with an external ticketing website.
     */
    void syncOnlineTicketSales();

    // --- Sales Monitoring ---
    /**
     * Provides real-time or near real-time sales status.
     * @return A summary (could be JSON or a custom object) of ticket sales, seats sold, revenue, etc.
     */
    String getSalesDashboardData();
}
