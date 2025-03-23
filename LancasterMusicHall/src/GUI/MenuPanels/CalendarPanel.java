package GUI.MenuPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
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

    private SQLConnection sqlConnection;

    public CalendarPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel,SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;
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

        // Click action to switch tabs and highlight the active button
        newEventButton.addActionListener(e -> {cardLayout.show(cardPanel, "NewEvent");});

        bottomPanel.add(newEventButton);

        add(bottomPanel);
    }
    private void renderBookings(ArrayList<Booking> bookings, JLabel[][] calendarCells, String[] days, String[] times) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);

        for (int bIndex = 0; bIndex < bookings.size(); bIndex++) {
            Booking booking = bookings.get(bIndex);
            LocalDate startDate = booking.getStartDate();
            LocalDate endDate = booking.getEndDate();
            LocalTime startTime = booking.getStartTime();
            LocalTime endTime = booking.getEndTime();

            int startHour = startTime.getHour();
            int endHour = endTime.getHour();

            String startDayLabel = startDate.format(dayFormatter);
            String endDayLabel = endDate.format(dayFormatter);

            int startCol = -1, endCol = -1;
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(startDayLabel)) startCol = i;
                if (days[i].equals(endDayLabel)) endCol = i;
            }

            if (startCol == -1 || endCol == -1 || startCol > endCol) continue;

            int startRow = -1, endRow = -1;
            for (int i = 0; i < times.length; i++) {
                int timeSlotHour = Integer.parseInt(times[i]);
                if (timeSlotHour == startHour) startRow = i;
                if (timeSlotHour == endHour) endRow = i;
            }

            if (startRow == -1 || endRow == -1 || startRow > endRow) continue;

            Color bookingColor = bookingColors[bIndex % bookingColors.length]; // Get color from list

            // === Render booking with merged-cell visual ===
            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    JLabel cell = calendarCells[row][col];
                    if (row == startRow && col == startCol) {
                        cell.setText("<html><center>" + booking.getActivityName() + "<br>" + booking.getVenue().getName() + "</center></html>");
                    } else {
                        cell.setText("");  // Empty for merged appearance
                    }
                    cell.setBackground(bookingColor);
                    cell.setOpaque(true);

                    // Border control
                    boolean isTop = (row == startRow);
                    boolean isBottom = (row == endRow);
                    boolean isLeft = (col == startCol);
                    boolean isRight = (col == endCol);

                    cell.setBorder(BorderFactory.createMatteBorder(
                            isTop ? 2 : 0,
                            isLeft ? 2 : 0,
                            isBottom ? 2 : 0,
                            isRight ? 2 : 0,
                            Color.black
                    ));
                }
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

        bookings.add(new Booking(
                101,
                LocalDate.of(2025, 3, 4),
                LocalDate.of(2025, 3, 6),
                LocalTime.of(17,2),
                LocalTime.of(20,20),
                movieActivity2,
                hallVenue,
                false,
                null,
                seats
        ));
        // Add more bookings as needed

        return bookings;
    }

    private final Color[] bookingColors = {
            new Color(200, 230, 255),
            new Color(255, 230, 200),
            new Color(230, 255, 200),
            new Color(255, 200, 230),
            new Color(230, 200, 255),
            new Color(200, 255, 230),
            new Color(255, 255, 200)
    };

}


