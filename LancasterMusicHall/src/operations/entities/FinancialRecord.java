package operations.entities;

public class FinancialRecord {
    private int financialRecordId;   // ID field
    private Booking booking;         // Tied to a specific booking
    private double revenue;
    private double cost;
    private double profit;           // revenue - cost
    private String date;             // Date Field

    public FinancialRecord(double revenue, double cost, double profit) {
        revenue = this.revenue;
        cost = this.cost;
        profit = this.profit;

        System.out.println("The revenue: " + revenue + " and cost: " + cost + " and profit: " + profit);
    }

    // Constructors, Getters, Setters...
}
