package operations.entities;

/**
 * Represents a daily sheet containing booking and revenue details for a specific date.
 * <p>
 * The {@code DailySheet} class encapsulates information such as the sheet ID, date,
 * booking details, total revenue, and configuration details for the day.
 * </p>
 */
public class DailySheet {
    private int dailySheetId;
    private String date;
    private String bookings;
    private double totalRevenue;
    private String configurations;

    /**
     * Default constructor for {@code DailySheet}.
     * <p>
     * Creates an empty {@code DailySheet} object.
     * </p>
     */
    public DailySheet() {
    }

    /**
     * Constructs a new {@code DailySheet} with the specified details.
     *
     * @param dailySheetId   the unique identifier for the daily sheet.
     * @param date           the date for which the daily sheet is applicable.
     * @param bookings       a string representation of the bookings for the day.
     * @param totalRevenue   the total revenue generated on the specified date.
     * @param configurations the configurations associated with the daily sheet.
     */
    public DailySheet(int dailySheetId, String date, String bookings, double totalRevenue, String configurations) {
        this.dailySheetId = dailySheetId;
        this.date = date;
        this.bookings = bookings;
        this.totalRevenue = totalRevenue;
        this.configurations = configurations;
    }

    /**
     * Retrieves the daily sheet ID.
     *
     * @return the daily sheet ID.
     */
    public int getDailySheetId() {
        return dailySheetId;
    }

    /**
     * Sets the daily sheet ID.
     *
     * @param dailySheetId the daily sheet ID to set.
     */
    public void setDailySheetId(int dailySheetId) {
        this.dailySheetId = dailySheetId;
    }

    /**
     * Retrieves the date for which the daily sheet is applicable.
     *
     * @return the date as a string.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date for the daily sheet.
     *
     * @param date the date to set.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Retrieves the bookings information for the day.
     *
     * @return a string representation of the bookings.
     */
    public String getBookings() {
        return bookings;
    }

    /**
     * Sets the bookings information for the day.
     *
     * @param bookings the bookings information to set.
     */
    public void setBookings(String bookings) {
        this.bookings = bookings;
    }

    /**
     * Retrieves the total revenue for the day.
     *
     * @return the total revenue.
     */
    public double getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Sets the total revenue for the day.
     *
     * @param totalRevenue the total revenue to set.
     */
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    /**
     * Retrieves the configurations associated with the daily sheet.
     *
     * @return a string representation of the configurations.
     */
    public String getConfigurations() {
        return configurations;
    }

    /**
     * Sets the configurations associated with the daily sheet.
     *
     * @param configurations the configurations to set.
     */
    public void setConfigurations(String configurations) {
        this.configurations = configurations;
    }

    /**
     * Returns a string representation of the daily sheet.
     * <p>
     * The returned string includes details such as the daily sheet ID, date, bookings,
     * total revenue, and configurations.
     * </p>
     *
     * @return a string representation of the daily sheet.
     */
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
