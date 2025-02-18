package com.lancaster.boxoffice;

import java.util.List;
import java.util.Map;

public interface BoxOfficeInterface {

    // --- Ticket Management ---
    /**
     * Sells tickets for a given activity.
     * @param activityId The ID of the activity (show/film).
     * @param seatNumbers List of seat identifiers.
     * @param discountCode Optional discount code.
     * @return A confirmation message or ticket ID.
     */
    String sellTickets(int activityId, List<String> seatNumbers, String discountCode);

    /**
     * Checks seat availability for a given activity.
     * @param activityId The activity ID.
     * @return A Map of seat identifiers to their availability (true if available).
     */
    Map<String, Boolean> checkSeatAvailability(int activityId);

    // --- Group Bookings ---
    /**
     * Holds group seats for an activity (up to 12 seats).
     * @param activityId The activity ID.
     * @param numberOfSeats Number of seats to hold.
     * @param discountCode Optional discount code.
     */
    void holdGroupSeats(int activityId, int numberOfSeats, String discountCode);

    // --- Refunds & Special Permissions ---
    /**
     * Processes a refund.
     * @param ticketId The ticket or transaction ID.
     * @param amount The amount to refund.
     * @return True if successful.
     */
    boolean processRefund(String ticketId, double amount);

    // --- Online Ticket Sales Integration ---
    /**
     * Synchronizes ticket sales with an external ticketing website.
     */
    void syncOnlineTicketSales();

    // --- Calendar Access & Booking Details ---
    /**
     * Provides a calendar view with detailed booking information.
     * Details include Title, Date, Time, Location, Price, Capacity, and Seating Plan.
     * @return A formatted string of booking details.
     */
    String viewCalendar();

    /**
     * Notifies the system of updates to bookings.
     * @param bookingId The booking ID.
     * @param changeType The type of change (e.g., "UPDATED", "CANCELED", "NEW_HOLD").
     */
    void updateCalendar(int bookingId, String changeType);

    // --- Seating Plan Adjustments ---
    /**
     * Adjusts the seating plan for a specific activity.
     * @param activityId The activity ID.
     * @param newSeatingArrangement A list representing the new seating layout.
     */
    void adjustSeatingPlan(int activityId, List<String> newSeatingArrangement);

    // --- Held & Accessible Seats ---
    /**
     * Retrieves a list of held seats for Friends of Lancaster’s, wheelchair users, and companions,
     * including information on when these seats may be released.
     * @return A formatted string with held seat details.
     */
    String getHeldAccessibleSeats();

    // --- Sales Dashboard ---
    /**
     * Provides real-time sales and performance data.
     * @return A formatted string of sales dashboard information.
     */
    String getSalesDashboardData();
}
