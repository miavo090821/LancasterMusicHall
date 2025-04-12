package operations.interfaces;
// this is the interface that boxoffice built for operation team
// they separated logic into different files which also let to
// creation of other operation.services files.
import java.util.Date;
import java.util.List;

/**
 * The main interface for the Operation team's methods:
 * retrieving daily/weekly reports, seat info, etc.
 */
public interface IOperations {
    /**
     * Gets all daily reports for a specific date.
     * @param day The date to get reports for (ex: 2025-04-15)
     * @return List of daily report objects
     */
    List<IReport> getDailyReports(Date day);

    /**
     * Gets all weekly reports starting from the given date.
     * @param weekStart The first day of the week (ex: 2025-04-12)
     * @return List of weekly report objects
     */
    List<IReport> getWeeklyReports(Date weekStart);

    /**
     * Generates a formatted daily report as text.
     * @param day The date to generate report for
     * @return Formatted report string ready for display/printing
     */
    String generateDailyReport(Date day);

    /**
     * Generates a formatted weekly report as text.
     * @param day The starting date of the week
     * @return Formatted report string ready for display/printing
     */
    String generateWeeklyReport(Date day);

    /**
     * Counts special requirement seats for a show.
     * @param showTitle Name of the show (ex: "Evening Concert")
     * @param date Show date
     * @param time Show time in 24h format (ex: 1930 for 7:30 PM)
     * @return Number of special seats available
     */
    int getSpecialRequirementSeat(String showTitle, Date date, int time);
}
