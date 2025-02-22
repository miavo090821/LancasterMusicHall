package operations.entities;

public class FinancialRecord {
    private int financialRecordId;
    private Booking booking;
    private double revenue;
    private double cost;
    private double profit;
    private String date;

    public FinancialRecord() {}

    public FinancialRecord(int financialRecordId, Booking booking, double revenue, double cost, String date) {
        this.financialRecordId = financialRecordId;
        this.booking = booking;
        this.revenue = revenue;
        this.cost = cost;
        this.profit = revenue - cost;
        this.date = date;
    }

    // Getters and Setters
    public int getFinancialRecordId() {
        return financialRecordId;
    }

    public void setFinancialRecordId(int financialRecordId) {
        this.financialRecordId = financialRecordId;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
        this.profit = this.revenue - this.cost;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
        this.profit = this.revenue - this.cost;
    }

    public double getProfit() {
        return profit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
