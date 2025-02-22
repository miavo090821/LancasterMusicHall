package operations.module;

import operations.entities.FinancialRecord;

import java.util.ArrayList;
import java.util.List;

public class IncomeTracker {
    // In-memory list of financial records (for demo purposes)
    private List<FinancialRecord> records;

    public IncomeTracker() {
        records = new ArrayList<>();
        // Optionally, add sample records here if needed.
    }

    // Records a new financial record.
    public void recordFinancials(FinancialRecord record) {
        records.add(record);
    }

    // Calculates total revenue for a given activity and returns a summary string.
    public String getRevenueForActivity(int activityId) {
        double totalRevenue = 0;
        for (FinancialRecord record : records) {
            if (record.getBooking() != null &&
                    record.getBooking().getActivity() != null &&
                    record.getBooking().getActivity().getActivityId() == activityId) {
                totalRevenue += record.getRevenue();
            }
        }
        return "Total revenue for activity " + activityId + ": $" + totalRevenue;
    }

    // Generates and returns a sample usage report for a given date range.
    public String getUsageReport(String startDate, String endDate) {
        return "Usage Report from " + startDate + " to " + endDate + ": 5 bookings, average occupancy 80%.";
    }

    // Returns a sample daily sheet summary as a string.
    public String getDailySheet(String date) {
        return "Daily Sheet for " + date + ": 3 bookings, total revenue $1500.";
    }
}
