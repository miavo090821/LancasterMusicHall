package com.lancaster.boxoffice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoxOfficeService implements BoxOfficeInterface {

    @Override
    public String sellTickets(int activityId, List<String> seatNumbers, String discountCode) {
        // Simulate ticket sale processing
        return "Tickets sold for activity " + activityId + " with seats " + seatNumbers.toString();
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

    @Override
    public void holdGroupSeats(int activityId, int numberOfSeats, String discountCode) {
        // Simulate holding group seats
        System.out.println("Group seats held for activity " + activityId + ": "
                + numberOfSeats + " seats with discount " + discountCode);
    }

    @Override
    public boolean processRefund(String ticketId, double amount) {
        // Simulate refund processing
        System.out.println("Processed refund for ticket " + ticketId + " amount: " + amount);
        return true;
    }

    @Override
    public void syncOnlineTicketSales() {
        // Simulate synchronization with an external ticketing system
        System.out.println("Synchronizing online ticket sales...");
    }

    @Override
    public String viewCalendar() {
        // Return a sample calendar view string with detailed booking information
        StringBuilder calendarDetails = new StringBuilder();
        calendarDetails.append("Calendar Details:\n")
                .append("Title: Concert, Date: 2025-03-01, Time: 19:00, Location: Main Hall, Price: $50, Capacity: 300, Seating Plan: [A1,A2,A3...]\n")
                .append("Title: Film Screening, Date: 2025-03-02, Time: 20:00, Location: Small Hall, Price: $30, Capacity: 150, Seating Plan: [B1,B2,B3...]\n");
        return calendarDetails.toString();
    }

    @Override
    public void updateCalendar(int bookingId, String changeType) {
        // Simulate updating the calendar with changes to a booking
        System.out.println("Calendar updated: Booking ID " + bookingId + " " + changeType);
    }

    @Override
    public void adjustSeatingPlan(int activityId, List<String> newSeatingArrangement) {
        // Simulate adjusting the seating plan for an activity
        System.out.println("Seating plan adjusted for activity " + activityId + ": " + newSeatingArrangement);
    }

    @Override
    public String getHeldAccessibleSeats() {
        // Return a sample string for held & accessible seats
        return "Held Seats: Friends of Lancaster's: Seat C1, C2; Wheelchair: Seat D1; Release on: 2025-03-29";
    }

    @Override
    public String getSalesDashboardData() {
        // Return a sample sales dashboard data string
        return "Sales Dashboard: Total Sales: $5000, Tickets Sold: 120, etc.";
    }
}
