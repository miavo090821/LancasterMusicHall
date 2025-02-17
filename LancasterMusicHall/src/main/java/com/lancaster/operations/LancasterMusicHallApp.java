package com.lancaster.operations;

public class LancasterMusicHallApp {
    public static void main(String[] args) {
        System.out.println("Lancaster Music Hall Desktop Application - Operations Team");

        // Initialize core modules
        CalendarModule calendarModule = new CalendarModule();
        RoomConfigurationSystem roomConfig = new RoomConfigurationSystem();
        IncomeTracker incomeTracker = new IncomeTracker();
        ReviewManager reviewManager = new ReviewManager();

        // Example usage
        Booking newBooking = new Booking();
        // set booking details...
        calendarModule.addBooking(newBooking);

        // Potentially launch a JavaFX or Swing UI here
        // new OperationsDashboardUI().launch();
    }
}
