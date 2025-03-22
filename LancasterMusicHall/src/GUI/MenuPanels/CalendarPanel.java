package GUI.MenuPanels;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Seat;
import operations.entities.Venue;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    public CalendarPanel() {
        setPreferredSize(new Dimension(800, 450)); // More width
        setBackground(Color.WHITE);

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
        LocalDate weekStart = LocalDate.of(2025, 3, 1); // Start of calendar week
        String[] days = new String[7];

        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter); // e.g., "Sat 1"
        }

        String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

        JPanel calendarPanel = new JPanel(new GridBagLayout());
        calendarPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // === Top-left corner (empty)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(30, 30)); // Small corner
        cornerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        calendarPanel.add(cornerLabel, gbc);

        JLabel[][] calendarCells = new JLabel[times.length][days.length]; // rows x columns

        // === Day headers ===
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(40, 30)); // Smaller header
            dayLabel.setBackground(new Color(220, 200, 255));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(dayLabel, gbc);
        }

        // === Time labels and calendar cells ===
        for (int row = 0; row < times.length; row++) {
            // Time label (small column)
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gbc.weightx = 0.1;
            gbc.weighty = 0.5;
            JLabel timeLabel = new JLabel(times[row], SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setOpaque(true);
            timeLabel.setPreferredSize(new Dimension(30, 60)); // Small time labels
            timeLabel.setBackground(Color.WHITE);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(timeLabel, gbc);

            // Calendar cells
            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gbc.weightx = 0.5;
                gbc.weighty = 0.5;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                calendarCells[row][col] = cell; // Store it!
                calendarPanel.add(cell, gbc);
            }
        }

        // === Booking Display Section ===
        ArrayList<Booking> bookings = getSampleBookings(); // Fetch or generate bookings
        renderBookings(bookings, calendarCells, days, times); // Apply bookings to calendar

        // Scroll if needed
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(new Dimension(580, 360)); // Smaller scroll area
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane);

        // === Bottom Panel ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.WHITE);

        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 12));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 30)); // Smaller button
        bottomPanel.add(newEventButton);

        add(bottomPanel);
    }
    private void renderBookings(ArrayList<Booking> bookings, JLabel[][] calendarCells, String[] days, String[] times) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH); // e.g., "Mon 1"

        for (Booking booking : bookings) {
            LocalDate startDate = booking.getStartDate(); // Now LocalDate
            LocalDate endDate = booking.getEndDate();     // Now LocalDate

            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                String dayLabel = currentDate.format(dayFormatter); // e.g., "Thu 20"

                // === Find matching day column ===
                int colIndex = -1;
                for (int i = 0; i < days.length; i++) {
                    if (days[i].equals(dayLabel)) {
                        colIndex = i;
                        break;
                    }
                }

                if (colIndex != -1) {
                    LocalTime startTime = booking.getStartTime();
                    LocalTime endTime = booking.getEndTime();

                    int startHour = startTime.getHour();
                    int endHour = endTime.getHour();

                    int startRow = -1;
                    int endRow = -1;

                    System.out.println("Booking ID: " + booking.getId() + ", Day: " + dayLabel + ", Col: " + colIndex);
                    System.out.println("StartHour: " + startHour + ", EndHour: " + endHour + ", Rows: " + startRow + " to " + endRow);

                    // === Find matching time rows ===
                    for (int i = 0; i < times.length; i++) {
                        int timeSlotHour = Integer.parseInt(times[i]);
                        if (timeSlotHour == startHour) startRow = i;
                        if (timeSlotHour == endHour) endRow = i;
                    }

                    // === Render cells ===
                    if (startRow != -1 && endRow != -1 && startRow <= endRow) {
                        for (int row = startRow; row <= endRow; row++) {
                            JLabel cell = calendarCells[row][colIndex];
                            cell.setText("<html><center>" + booking.getActivityName() + "<br>" + booking.getVenue().getName() + "</center></html>");
                            cell.setBackground(new Color(200, 230, 255));
                            cell.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                        }
                    }
                }
                currentDate = currentDate.plusDays(1); // Next day
            }
        }
    }

    // === Sample bookings for demo ===
    private ArrayList<Booking> getSampleBookings() {
        ArrayList<Booking> bookings = new ArrayList<Booking>();

        // Sample Activity and Venue (replace with your real objects)
        Activity movieActivity1 = new Activity(1, "Movie A");
        Activity movieActivity2 = new Activity(1, "Movie B");

        Venue hallVenue = new Venue(1, "Main Hall", "Hall", 300);
        java.util.List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
        );

        bookings.add(new Booking(
                101,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(10,20),
                LocalTime.of(12,20),
                movieActivity1,
                hallVenue,
                false,
                null,
                seats
        ));
        bookings.add(new Booking(
                101,
                LocalDate.of(2025, 3, 2),
                LocalDate.of(2025, 3, 5),
                LocalTime.of(14,20),
                LocalTime.of(16,20),
                movieActivity2,
                hallVenue,
                false,
                null,
                seats
        ));
        // Add more bookings as needed

        return bookings;
    }
}


