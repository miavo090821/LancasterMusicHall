package operations.module;

import operations.entities.FinancialRecord;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The IncomeTracker class manages financial records in-memory and provides methods to record financial data,
 * calculate revenue for activities, and generate sample usage and daily sheet reports.
 */
public class IncomeTracker {
    /**
     * In-memory list of financial records (for demonstration purposes).
     */
    private List<FinancialRecord> records;

    /**
     * Constructs a new IncomeTracker instance and initializes the in-memory list of financial records.
     */
    public IncomeTracker() {
        records = new ArrayList<>();
        // Optionally, add sample records here if needed.
    }

    /**
     * Records a new financial record.
     *
     * @param record the {@link FinancialRecord} object to be added
     */
    public void recordFinancials(FinancialRecord record) {
        records.add(record);
    }

    /**
     * Calculates total revenue for a given activity and returns a summary string.
     *
     * @param activityId the identifier of the activity for which revenue is calculated
     * @return a summary string showing the total revenue for the specified activity
     */
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

    /**
     * Generates and returns a sample usage report for the given date range.
     *
     * @param startDate the start date of the report period (inclusive)
     * @param endDate   the end date of the report period (inclusive)
     * @return a formatted string containing the usage report details
     */
    public String getUsageReport(LocalDate startDate, LocalDate endDate) {
        return "Usage Report from " + startDate + " to " + endDate + ": 5 bookings, average occupancy 80%.";
    }

    /**
     * Returns a sample daily sheet summary for the given date.
     *
     * @param date the date for which the daily sheet is requested
     * @return a formatted string containing the daily sheet summary details
     */
    public String getDailySheet(LocalDate date) {
        return "Daily Sheet for " + date + ": 3 bookings, total revenue $1500.";
    }
}
