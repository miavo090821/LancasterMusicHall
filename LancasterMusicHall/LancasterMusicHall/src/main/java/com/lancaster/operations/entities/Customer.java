package com.lancaster.operations.entities;

public class Customer {
    private int customerId;          // ID field
    private PaymentDetails paymentDetails; // Could be null if they pay on-site
    private Discount discount;             // If they have an associated discount
    private String fullName;
    private String dob;              // Date Field (String or LocalDate)
    private String email;
    private String phoneNumber;

    // Constructors, Getters, Setters...
}
