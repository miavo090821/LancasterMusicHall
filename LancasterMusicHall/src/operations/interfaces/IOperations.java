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
    List<IReport> getDailyReports(Date day);
    List<IReport> getWeeklyReports(Date weekStart);
    String generateDailyReport(Date day);
    String generateWeeklyReport(Date day);

    // Example: fetch number of special seats for a show
    int getSpecialRequirementSeat(String showTitle, Date date, int time);
}
