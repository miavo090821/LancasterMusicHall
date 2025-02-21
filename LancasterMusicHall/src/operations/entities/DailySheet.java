package operations.entities;

public class DailySheet {
    private int dailySheetId;        // ID field
    private FinancialRecord financialRecord; // Summaries for that day
    private String date;             // Date Field
    private String bookings;         // Could store JSON or CSV summary
    private double totalRevenue;
    private String configurations;   // JSON or text about room setups

}
