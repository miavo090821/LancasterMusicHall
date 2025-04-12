package operations;

import operations.entities.*;
import operations.module.CalendarModule;
import operations.module.IncomeTracker;
import operations.module.ReviewManager;
import operations.module.RoomConfigurationSystem;

public class LancasterMusicHallApp {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall Desktop Application - Operations Team");

        // Initialize core modules
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfig = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();
        ReviewManager reviewManager = new ReviewManager();


    }
}
