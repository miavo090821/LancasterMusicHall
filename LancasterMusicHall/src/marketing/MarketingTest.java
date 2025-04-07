package marketing;

import Database.SQLConnection;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MarketingTest {
    public static void main(String[] args) {
        // Create a SQLConnection instance.
        SQLConnection sqlCon = new SQLConnection();

        // Create dummy implementations for the dependencies.
        CalendarModule calendarModule = new CalendarModule() {
            @Override
            public boolean scheduleFilm(int filmId, LocalDate proposedDate) {
                System.out.println("Dummy scheduleFilm called for filmId: " + filmId + " on " + proposedDate);
                return true;
            }
        };

        RoomConfigurationSystem roomConfigSystem = new RoomConfigurationSystem() {
            @Override
            public Map<String, String> getRoomAvailability(LocalDate date) {
                Map<String, String> availability = new HashMap<>();
                availability.put("Room A", "Available");
                availability.put("Room B", "Booked");
                return availability;
            }

            @Override
            public String getSeatingPlan(int activityId) {
                return "Dummy seating plan for activity " + activityId;
            }

            @Override
            public String getHeldSpaces() {
                return "Dummy held spaces info";
            }
        };

        IncomeTracker incomeTracker = new IncomeTracker() {
            @Override
            public String getRevenueForActivity(int activityId) {
                return "Revenue for activity " + activityId + " is £1000";
            }

            @Override
            public String getUsageReport(LocalDate startDate, LocalDate endDate) {
                return "Usage report from " + startDate + " to " + endDate;
            }
        };

        // Instantiate MarketingService with the dummy dependencies.
        MarketingService marketingService = new MarketingService(calendarModule, roomConfigSystem, incomeTracker, sqlCon);

        // Call and print the marketing interface methods.
        System.out.println("=== View Calendar ===");
        String calendarData = marketingService.viewCalendar(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));
        System.out.println(calendarData);

        System.out.println("=== Configuration Details for Booking 1 ===");
        String configDetails = marketingService.getConfigurationDetails(1);
        System.out.println(configDetails);

        System.out.println("=== Revenue Information for Activity 1 ===");
        String revenueInfo = marketingService.getRevenueInfo(1);
        System.out.println(revenueInfo);

        System.out.println("=== Usage Reports from 2025-04-01 to 2025-04-30 ===");
        String usageReport = marketingService.getUsageReports(LocalDate.of(2025, 4, 1), LocalDate.of(2025, 4, 30));
        System.out.println(usageReport);

        System.out.println("=== Held Spaces ===");
        String heldSpaces = marketingService.getHeldSpaces();
        System.out.println(heldSpaces);

        System.out.println("=== Schedule Film for Film 1 on 2025-04-15 ===");
        boolean filmScheduled = marketingService.scheduleFilm(1, LocalDate.of(2025, 4, 15));
        System.out.println("Film scheduled: " + filmScheduled);

        System.out.println("=== Daily Sheet for 2025-04-15 ===");
        String dailySheet = marketingService.getDailySheet(LocalDate.of(2025, 4, 15));
        System.out.println(dailySheet);
    }
}
