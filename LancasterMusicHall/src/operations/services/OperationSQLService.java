package operations.services;

import Database.SQLConnection;
import Database.DatabaseUpdateListener;
import operations.interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;
import java.util.Date;

/**
 * Implements IOperations for the Operation team.
 * Also implements DatabaseUpdateListener so it can react
 * to any DB updates triggered by Box Office or Marketing.
 */
public class OperationSQLService implements IOperations, DatabaseUpdateListener {

    // Reuse the same credentials from SQLConnection
    private final String url = "jdbc:mysql://sst-stuproj.city.ac.uk:3306/xxx";
    private final String dbUser = "your_db_username";
    private final String dbPassword = "your_db_password";

    // A reference to the shared SQLConnection
    private final SQLConnection sqlConnection;

    public OperationSQLService() {
        sqlConnection = new SQLConnection();
        // Register this service so it gets notifications (e.g. booking updates)
        sqlConnection.registerUpdateListener(this);
    }

    @Override


    

    @Override
    public String generateDailyReport(Date day) {
        List<IReport> dailyReports = getDailyReports(day);
        StringBuilder sb = new StringBuilder("DAILY REPORT\n\n");
        for (IReport rep : dailyReports) {
            sb.append(rep.generateReport()).append("\n\n");
        }
        return sb.toString();
    }

    @Override
    public String generateWeeklyReport(Date day) {
        List<IReport> weeklyReports = getWeeklyReports(day);
        StringBuilder sb = new StringBuilder("WEEKLY REPORT\n\n");
        for (IReport rep : weeklyReports) {
            sb.append(rep.generateReport()).append("\n\n");
        }
        return sb.toString();
    }

  }
