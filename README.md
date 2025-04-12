
---

# Lancaster Music Hall Operation Team Application

Welcome to the Lancaster Music Hall Operation Team Application! This desktop application supports the operations and administrative tasks for Lancaster Music Hall. It provides functionality for managing client bookings, scheduling events, and handling staff operations.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Troubleshooting](#troubleshooting)
- [License](#license)

## Overview

The Lancaster Music Hall Operation Team Application is designed to streamline the daily operations of Lancaster Music Hall. The application assists in:
- Authenticating staff users (or allowing guest access).
- Creating and managing bookings and events.
- Calculating event prices and total booking costs.
- Managing contract details and client information.

## Features

- **Staff Login and Guest Access**  
  Secure staff login is available to users with valid credentials. Alternatively, users may continue using the application in guest mode with limited functionality.

- **Booking Management**  
  Create new bookings with comprehensive details including client information, contract details, and pricing breakdown. The application calculates and displays the total booking cost.

- **Event Management**  
  Add multiple events to a booking. Each event captures the event’s time, date, location, description, layout, and price. The maximum discount value is passed along to each event.

- **Pricing and Discount Calculation**  
  The application integrates with SQL stored functions to calculate costs for various venues and event types. Prices are computed based on the event duration and type.

- **SQL Database Integration**  
  Connection to a MySQL database to persist Staff, Booking, Client, Contract, and Event details.

- **GUI Panels**  
  A user-friendly interface includes separate panels for client details, booking details, pricing, and event management. Navigation is handled by a CardLayout for a smooth user experience.

## Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/miavo090821/LancasterMusicHall.git
   cd lancaster-music-hall
   ```

2. **Java Requirements:**  
   Ensure you have the Java Development Kit (JDK) 8 or later installed on your system.

3. **Database Setup:**
    - Install MySQL Server.
    - Create a database (e.g., `in2033t23`).
    - Configure the connection URL, username, and password in the `SQLConnection.java` file.

4. **JDBC Driver:**  
   Make sure to include the MySQL Connector/J (JDBC driver) in your project’s classpath. If you are using an IDE like Eclipse or IntelliJ IDEA, add the connector library to your project.

5. **Build the Application:**  
   Compile the project using your preferred Java build tool (e.g., Maven, Gradle) or directly in your IDE.

## Configuration

- **Database Configuration:**  
  In `SQLConnection.java` update the following constants with your database credentials:
  ```java
  private static final String url = "jdbc:mysql://sst-stuproj00.city.ac.uk:3306/in2033t23";
  private final String dbUser = "in2033t23_a";
  private final String dbPassword = "XUBLJfsYMHY";
  ```

- **SQL Stored Functions:**  
  Make sure your database includes the necessary stored functions for calculating venue costs, room costs, etc., as these functions are invoked in pricing methods like `calculateMainHallCost`, `calculateRoomCost`, and others.

## Usage

1. **Starting the Application:**  
   Run the main method in `StaffLoginGUI.java` to launch the Staff Login panel.

2. **Staff Login:**
    - Enter a valid Staff ID and password to log in.
    - Alternatively, choose the "Continue as Guest" option (if available) to access the Main Menu without logging in.

3. **Accessing Main Menu:**  
   After login (or guest access), the Main Menu opens. Navigation is handled via tabs including Home, Calendar, Diary, Booking, Reports, and Settings.

4. **Creating a New Booking:**
    - In the Booking panel, fill in client and booking details.
    - Add one or more events and calculate each event price.
    - Enter pricing details, including the maximum discount.
    - Submit the booking. The application passes the calculated customer bill as `total_cost` to the Booking record and saves each event with the provided details (including the max discount).

5. **Data Persistence:**  
   All booking, event, and client details are persisted to the MySQL database according to the configuration in `SQLConnection.java`.

## Technologies Used

- **Java SE:** Core programming language and Swing for the GUI.
- **MySQL:** Database for storing application data.
- **JDBC:** For connecting Java to the MySQL database.
- **SQL Stored Functions:** Used for price calculations.

## Troubleshooting

- **Database Connection Errors:**  
  Verify that the JDBC URL, username, and password in `SQLConnection.java` are correct. Make sure your database server is running and accessible.

- **Login Issues:**  
  Ensure that the Staff table contains the correct column names and data for authentication. If you see errors related to unknown columns, double-check your SQL queries.

- **GUI Issues:**  
  If the Swing GUI does not display correctly, adjust the dimensions and layouts in the respective panels. Ensure that your Java runtime is compatible with the Swing version used.

---