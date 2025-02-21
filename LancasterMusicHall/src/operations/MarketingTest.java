package operations;

import marketing.MarketingService;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.RoomConfigurationSystem;

public class MarketingTest {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall - Marketing Test");

        // Mocked dependencies
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfigSystem = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();

        // Create MarketingService instance
        MarketingService marketingService = new MarketingService(calendarModule, roomConfigSystem, incomeTracker);

        // Test viewCalendar
        System.out.println("\n--- Viewing Calendar ---");
        String calendarData = marketingService.viewCalendar("2025-03-01", "2025-03-05");
        System.out.println(calendarData);

        // Test notifyCalendarChange
        System.out.println("\n--- Notifying Calendar Change ---");
        marketingService.notifyCalendarChange(1, "rescheduled");

        // Test getSpaceAvailability
        System.out.println("\n--- Checking Space Availability ---");
        System.out.println(marketingService.getSpaceAvailability("2025-03-02"));

        // Test getSeatingPlan
        System.out.println("\n--- Fetching Seating Plan ---");
        System.out.println(marketingService.getSeatingPlan(2));

        // Test getConfigurationDetails
        System.out.println("\n--- Fetching Configuration Details ---");
        System.out.println(marketingService.getConfigurationDetails(1));

        // Test getRevenueInfo
        System.out.println("\n--- Fetching Revenue Info ---");
        System.out.println(marketingService.getRevenueInfo(1));

        // Test getUsageReports
        System.out.println("\n--- Fetching Usage Reports ---");
        System.out.println(marketingService.getUsageReports("2025-03-01", "2025-03-07"));

        // Test getHeldSpaces
        System.out.println("\n--- Fetching Held Spaces ---");
        System.out.println(marketingService.getHeldSpaces());

        // Test scheduleFilm
        System.out.println("\n--- Scheduling Film ---");
        boolean scheduled = marketingService.scheduleFilm(101, "2025-03-10");
        System.out.println("Film scheduled: " + scheduled);

        // Test getDailySheet
        System.out.println("\n--- Fetching Daily Sheet ---");
        System.out.println(marketingService.getDailySheet("2025-03-05"));
    }
}
