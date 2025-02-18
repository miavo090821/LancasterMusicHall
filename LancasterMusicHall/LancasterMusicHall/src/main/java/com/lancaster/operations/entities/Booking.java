package com.lancaster.operations.entities;

public class Booking {
    private int bookingId;           // ID field
    private String dateStart;        // Date Field
    private String dateEnd;          // Date Field
    private User user;               // Staff user who created the booking
    private Customer customer;       // Customer or external client
    private Venue venue;             // Which venue/room/hall is booked
    private Activity activity;       // Optional: link to a show, meeting, or other event

    private boolean held;            // True if it's just a "hold"
    private String holdExpiryDate;   // 28-day rule for final confirmation

    // Constructors, Getters, Setters...
}
