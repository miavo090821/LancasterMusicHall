package com.lancaster.operations;

import com.lancaster.operations.entities.Booking;
import com.lancaster.operations.entities.User;
import com.lancaster.operations.modules.CalendarModule;
import com.lancaster.operations.modules.IncomeTracker;
import com.lancaster.operations.modules.ReviewManager;
import com.lancaster.operations.modules.RoomConfigurationSystem;

public class LancasterMusicHallApp {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall Desktop Application - Operations Team");

        // Initialize core modules
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfig = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();
        ReviewManager reviewManager = new ReviewManager();

        // Example: Create a new booking and add it to the calendar
        User staff = new User(1, "Alice Johnson", "alice@lancasterhall.com", "Operations", "pass123");
        Booking newBooking = new Booking(101, "2025-03-01", "2025-03-03", staff, null, null, null, true, "2025-03-29");
        calendarModule.addBooking(newBooking);

        // You could now launch a JavaFX or Swing-based UI here.
        // For example: new OperationsDashboardUI().launch();
    }
}
