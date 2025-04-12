package operations.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A concrete implementation of the {@link IReport} interface that represents
 * a report with details such as title, date, start time, pricing, and ticket sales information.
 */
public class Report implements IReport {
    /**
     * The title of the report.
     */
    private final String title;

    /**
     * The date of the report.
     */
    private final Date date;

    /**
     * The start time of the event in military time (e.g., 1300 for 1 PM).
     */
    private final int startTime;

    /**
     * The price in integer form (e.g., 999 represents £9.99).
     */
    private final int price;

    /**
     * The discount factor (e.g., 0.85 represents a 15% discount).
     */
    private final double discount;

    /**
     * A map representing the number of seats sold for each ticket type.
     */
    private final Map<ETicketType, Integer> seats;

    /**
     * A map representing the number of refunded seats for each ticket type.
     */
    private final Map<ETicketType, Integer> refundedSeats;

    /**
     * A list of refund complaint notes.
     */
    private final List<String> refundComplaints;

    /**
     * Constructs a Report instance with all required details.
     *
     * @param title            the title of the report
     * @param date             the date of the report
     * @param startTime        the event start time in military format (e.g., 1300 for 1 PM)
     * @param price            the ticket price in integer format (e.g., 999 represents £9.99)
     * @param discount         the discount factor applied to certain ticket types (e.g., 0.85 for 15% off)
     * @param seats            a map of seats sold per {@link ETicketType}
     * @param refundedSeats    a map of refunded seats per {@link ETicketType}
     * @param refundComplaints a list of refund complaint notes
     */
    public Report(String title,
                  Date date,
                  int startTime,
                  int price,
                  double discount,
                  Map<ETicketType, Integer> seats,
                  Map<ETicketType, Integer> refundedSeats,
                  List<String> refundComplaints) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.price = price;
        this.discount = discount;
        this.seats = seats;
        this.refundedSeats = refundedSeats;
        this.refundComplaints = refundComplaints;
    }

    /**
     * Returns the title of the report.
     *
     * @return the report title
     */
    @Override
    public String getTitle() {
        return title;
    }

    /**
     * Returns the date of the report.
     *
     * @return the report date
     */
    @Override
    public Date getDate() {
        return date;
    }

    /**
     * Returns the start time of the event as a double.
     * <p>
     * This converts an integer start time (e.g., 1300) into a more readable double (e.g., 13.00).
     * </p>
     *
     * @return the start time as a double
     */
    @Override
    public double getStartTime() {
        return (double) startTime / 100.0;
    }

    /**
     * Returns the price as an integer.
     *
     * @return the price in integer format
     */
    @Override
    public int getPrice() {
        return price;
    }

    /**
     * Returns the ticket price as a double.
     * <p>
     * This converts the integer price (e.g., 999) to a double representing the actual monetary value (e.g., 9.99).
     * </p>
     *
     * @return the price as a double value
     */
    @Override
    public double getDoublePrice() {
        return price / 100.0;
    }

    /**
     * Returns the map of seats sold per ticket type.
     *
     * @return a map with ticket types and the corresponding seats sold
     */
    @Override
    public Map<ETicketType, Integer> getSeatsSold() {
        return seats;
    }

    /**
     * Returns the map of refunded seats per ticket type.
     *
     * @return a map with ticket types and the corresponding refunded seats
     */
    @Override
    public Map<ETicketType, Integer> getRefundSeats() {
        return refundedSeats;
    }

    /**
     * Calculates and returns the total refund sum.
     * <p>
     * The refund sum is computed based on the number of refunded seats multiplied by the ticket price,
     * taking into account any discounts for discounted or disabled ticket types.
     * </p>
     *
     * @return the total refund sum as a double
     */
    @Override
    public double getRefundSum() {
        double sum = 0;
        for (ETicketType type : ETicketType.values()) {
            int refundedCount = refundedSeats.getOrDefault(type, 0);
            sum += (refundedCount * getDoublePrice()) * (type == ETicketType.DISCOUNTED || type == ETicketType.DISABLED ? discount : 1.0);
        }
        return sum;
    }

    /**
     * Returns the list of refund complaint notes.
     *
     * @return a list of strings representing refund complaints
     */
    @Override
    public List<String> getRefundNotes() {
        return refundComplaints;
    }

    /**
     * Calculates and returns the total revenue from all sold seats.
     * <p>
     * This calculation multiplies the number of seats sold for each ticket type with the ticket price,
     * applying a discount where necessary.
     * </p>
     *
     * @return the total revenue as a double
     */
    @Override
    public double getTotalRevenue() {
        double revenue = 0;
        for (ETicketType type : ETicketType.values()) {
            int soldCount = seats.getOrDefault(type, 0);
            double seatRevenue = soldCount * getDoublePrice();
            if (type == ETicketType.DISCOUNTED || type == ETicketType.DISABLED) {
                seatRevenue *= discount;
            }
            revenue += seatRevenue;
        }
        return revenue;
    }

    /**
     * Generates and returns a detailed report as a formatted string.
     * <p>
     * The report includes information such as title, date, start time, price, discount,
     * seats sold, refunded seats, total revenue, refund sum, and refund complaints.
     * </p>
     *
     * @return the generated report as a string
     */
    @Override
    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(title).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Start Time: ").append(getStartTime()).append("\n");
        sb.append("Price: £").append(getDoublePrice()).append("\n");
        sb.append("Discount: ").append((1 - discount) * 100).append("%\n\n");

        sb.append("Seats Sold:\n");
        for (ETicketType type : ETicketType.values()) {
            int soldCount = seats.getOrDefault(type, 0);
            int refundCount = refundedSeats.getOrDefault(type, 0);
            sb.append("  ").append(type).append(": Sold=").append(soldCount)
                    .append(", Refunded=").append(refundCount).append("\n");
        }

        sb.append("\nTotal revenue: £").append(getTotalRevenue()).append("\n");
        sb.append("Refund sum: £").append(getRefundSum()).append("\n\n");

        sb.append("Refund complaints:\n");
        for (String complaint : refundComplaints) {
            sb.append("  - ").append(complaint).append("\n");
        }
        return sb.toString();
    }
}
