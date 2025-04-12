package operations.entities;

import java.time.LocalDate;

/**
 * The FinancialRecord class represents a financial record with details about revenue, cost,
 * profit, and associated booking for a specific date.
 */
public class FinancialRecord {
    private int financialRecordId;
    private Booking booking;
    private double revenue;
    private double cost;
    private double profit;
    private LocalDate date;

    /**
     * Default constructor for FinancialRecord.
     */
    public FinancialRecord() {}

    /**
     * Constructs a FinancialRecord with the specified details.
     *
     * @param financialRecordId the identifier of the financial record
     * @param booking the booking associated with the financial record
     * @param revenue the revenue amount for the financial record
     * @param cost the cost amount for the financial record
     * @param date the date of the financial record
     */
    public FinancialRecord(int financialRecordId, Booking booking, double revenue, double cost, LocalDate date) {
        this.financialRecordId = financialRecordId;
        this.booking = booking;
        this.revenue = revenue;
        this.cost = cost;
        this.profit = revenue - cost;
        this.date = date;
    }

    /**
     * Gets the financial record identifier.
     *
     * @return the financial record identifier
     */
    public int getFinancialRecordId() {
        return financialRecordId;
    }

    /**
     * Sets the financial record identifier.
     *
     * @param financialRecordId the financial record identifier to set
     */
    public void setFinancialRecordId(int financialRecordId) {
        this.financialRecordId = financialRecordId;
    }

    /**
     * Gets the booking associated with the financial record.
     *
     * @return the booking
     */
    public Booking getBooking() {
        return booking;
    }

    /**
     * Sets the booking for the financial record.
     *
     * @param booking the booking to associate with the financial record
     */
    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    /**
     * Gets the revenue amount.
     *
     * @return the revenue
     */
    public double getRevenue() {
        return revenue;
    }

    /**
     * Sets the revenue amount and updates the profit accordingly.
     *
     * @param revenue the revenue amount to set
     */
    public void setRevenue(double revenue) {
        this.revenue = revenue;
        this.profit = this.revenue - this.cost;
    }

    /**
     * Gets the cost amount.
     *
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * Sets the cost amount and updates the profit accordingly.
     *
     * @param cost the cost amount to set
     */
    public void setCost(double cost) {
        this.cost = cost;
        this.profit = this.revenue - this.cost;
    }

    /**
     * Gets the calculated profit (revenue minus cost).
     *
     * @return the profit
     */
    public double getProfit() {
        return profit;
    }

    /**
     * Gets the date of the financial record.
     *
     * @return the date of the financial record
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date of the financial record.
     *
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
