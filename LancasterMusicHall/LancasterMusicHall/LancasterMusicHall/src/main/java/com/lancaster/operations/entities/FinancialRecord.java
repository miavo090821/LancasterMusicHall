package com.lancaster.operations.entities;

public class FinancialRecord {
    private int financialRecordId;   // ID field
    private Booking booking;         // Tied to a specific booking
    private double revenue;
    private double cost;
    private double profit;           // revenue - cost
    private String date;             // Date Field

    // Constructors, Getters, Setters...
}
