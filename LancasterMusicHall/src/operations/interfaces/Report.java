package operations.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A simple concrete implementation of IReport
 * with fields for title, date, etc.
 */
public class Report implements IReport {
    private final String title;
    private final Date date;
    private final int startTime;    // e.g. 1300 for 1 PM
    private final int price;        // e.g. 999 for £9.99
    private final double discount;  // e.g. 0.85 for 15% off
    private final Map<ETicketType, Integer> seats;
    private final Map<ETicketType, Integer> refundedSeats;
    private final List<String> refundComplaints;

    public Report(String title,
                  Date date,
                  int startTime,
                  int price,
                  double discount,
                  Map<ETicketType, Integer> seats,
                  Map<ETicketType, Integer> refundedSeats,
                  List<String> refundComplaints)
    {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.price = price;
        this.discount = discount;
        this.seats = seats;
        this.refundedSeats = refundedSeats;
        this.refundComplaints = refundComplaints;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public double getStartTime() {
        // Convert 1300 to 13.00, etc.
        return (double) startTime / 100.0;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public double getDoublePrice() {
        // e.g. price=999 -> 9.99
        return price / 100.0;
    }

    @Override
    public Map<ETicketType, Integer> getSeatsSold() {
        return seats;
    }

    @Override
    public Map<ETicketType, Integer> getRefundSeats() {
        return refundedSeats;
    }

    @Override
    public double getRefundSum() {
        double sum = 0;
        for (ETicketType type : ETicketType.values()) {
            int refundedCount = refundedSeats.getOrDefault(type, 0);
            sum += (refundedCount * getDoublePrice()) * (type == ETicketType.DISCOUNTED || type == ETicketType.DISABLED ? discount : 1.0);
        }
        return sum;
    }

    @Override
    public List<String> getRefundNotes() {
        return refundComplaints;
    }

    @Override
    public double getTotalRevenue() {
        // Example: total seats sold * price, minus discount for certain types
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
