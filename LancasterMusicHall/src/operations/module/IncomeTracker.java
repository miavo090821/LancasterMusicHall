package com.lancaster.operations.module;

import operations.entities.FinancialRecord;

import java.util.List;

public class IncomeTracker {
    // Manages revenue, cost, profit calculations, plus aggregated usage data

    public void recordFinancials(FinancialRecord record) { /* ... */ }
    public double calculateProfit(int bookingId) { /* ... */ return 0.0; }
    public double getTotalRevenueForPeriod(String startDate, String endDate) { /* ... */ return 0.0; }
    public List<FinancialRecord> getAllRecords() { /* ... */ return null; }
}
