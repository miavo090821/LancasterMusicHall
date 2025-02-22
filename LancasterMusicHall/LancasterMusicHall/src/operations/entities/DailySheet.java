package operations.entities;

public class DailySheet {
    private int dailySheetId;
    private String date;
    private String bookings;
    private double totalRevenue;
    private String configurations;

    // Default constructor
    public DailySheet() {
    }

    // Parameterized constructor
    public DailySheet(int dailySheetId, String date, String bookings, double totalRevenue, String configurations) {
        this.dailySheetId = dailySheetId;
        this.date = date;
        this.bookings = bookings;
        this.totalRevenue = totalRevenue;
        this.configurations = configurations;
    }

    // Getters and Setters
    public int getDailySheetId() {
        return dailySheetId;
    }

    public void setDailySheetId(int dailySheetId) {
        this.dailySheetId = dailySheetId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBookings() {
        return bookings;
    }

    public void setBookings(String bookings) {
        this.bookings = bookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getConfigurations() {
        return configurations;
    }

    public void setConfigurations(String configurations) {
        this.configurations = configurations;
    }

    @Override
    public String toString() {
        return "DailySheet{" +
                "dailySheetId=" + dailySheetId +
                ", date='" + date + '\'' +
                ", bookings='" + bookings + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", configurations='" + configurations + '\'' +
                '}';
    }
}
