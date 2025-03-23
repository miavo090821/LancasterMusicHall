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

    

        sb.append("\nTotal revenue: £").append(getTotalRevenue()).append("\n");
        sb.append("Refund sum: £").append(getRefundSum()).append("\n\n");

        sb.append("Refund complaints:\n");
        for (String complaint : refundComplaints) {
            sb.append("  - ").append(complaint).append("\n");
        }
        return sb.toString();
    }
}
