package GUI.MenuPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Seat;
import operations.entities.Venue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarPanel extends JPanel {
    private SQLConnection sqlConnection;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Calendar grid components
    private JLabel[][] calendarCells;
    private String[] days;
    private String[] times;

    // Define the diary/calendar view date range (one week view)
    private final LocalDate weekStart = LocalDate.of(2025, 3, 1);
    private final LocalDate weekEnd = weekStart.plusDays(6);

    private final Color[] bookingColors = {
            new Color(200, 230, 255),
            new Color(255, 230, 200),
            new Color(230, 255, 200),
            new Color(255, 200, 230),
            new Color(230, 200, 255),
            new Color(200, 255, 230),
            new Color(255, 255, 200)
    };

    public CalendarPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        this.sqlConnection = mainMenu.getSqlConnection();

        setLayout(new BorderLayout());
        setBackground(new Color(200, 170, 230));

        // Prepare day headers
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter); // e.g., "Sat 1"
        }
        times = new String[]{"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

        // Build calendar grid using GridBagLayout
        JPanel calendarPanel = new JPanel(new GridBagLayout());
        calendarPanel.setPreferredSize(new Dimension(550, 400));
        calendarPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Top-left corner (empty)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(30, 30));
        cornerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        calendarPanel.add(cornerLabel, gbc);

        calendarCells = new JLabel[times.length][days.length];

        // Day headers
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(40, 30));
            dayLabel.setBackground(new Color(220, 200, 255));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(dayLabel, gbc);
        }

        // Time labels and calendar cells
        for (int row = 0; row < times.length; row++) {
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gbc.weightx = 0.1;
            gbc.ipady = 40;
            JLabel timeLabel = new JLabel(times[row], SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setOpaque(true);
            timeLabel.setPreferredSize(new Dimension(30, 60));
            timeLabel.setBackground(Color.WHITE);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(timeLabel, gbc);

            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gbc.weightx = 0.5;
                gbc.weighty = 0.5;
                gbc.ipady = 40;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                calendarCells[row][col] = cell;
                calendarPanel.add(cell, gbc);
            }
        }

        // Load bookings from SQL and render them into the calendar grid
        refreshCalendar();

        // Wrap calendarPanel in a scroll pane and add to center
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(new Dimension(740, 520));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(scrollPane);
        add(centerContainer, BorderLayout.CENTER);

        // Bottom Panel with controls
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(Box.createHorizontalGlue());

        // Middle column: New Event button
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        middle.setBackground(Color.WHITE);
        middle.setPreferredSize(new Dimension(240, 100));
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> showNewEventForm());
        middle.add(newEventButton);
        bottomPanel.add(middle);

        // Right column: Date Picker
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 35));
        right.setBackground(Color.WHITE);
        right.setPreferredSize(new Dimension(180, 50));
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd-MM-yyyy");
        datePicker.setPreferredSize(new Dimension(100, 25));
        datePicker.setDate(new Date());
        right.add(dateLabel);
        right.add(datePicker);
        rightColumn.add(right);
        bottomPanel.add(rightColumn);
        bottomPanel.add(Box.createHorizontalGlue());
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Refresh the calendar view by fetching bookings from SQL for the week
    public void refreshCalendar() {
        ArrayList<Booking> bookings = new ArrayList<>(sqlConnection.fetchDiaryBookings(weekStart, weekEnd));
        renderBookings(bookings);
        revalidate();
        repaint();
    }

    // Renders the list of bookings into the calendar grid
    private void renderBookings(ArrayList<Booking> bookings) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);

        // Clear previous content
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                calendarCells[row][col].setText("");
                calendarCells[row][col].setBackground(Color.WHITE);
                calendarCells[row][col].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }

        for (Booking booking : bookings) {
            LocalDate bStartDate = booking.getStartDate();
            LocalDate bEndDate = booking.getEndDate();
            LocalTime bStartTime = booking.getStartTime();
            LocalTime bEndTime = booking.getEndTime();

            int startHour = bStartTime.getHour();
            int endHour = bEndTime.getHour();

            String startDayLabel = bStartDate.format(dayFormatter);
            String endDayLabel = bEndDate.format(dayFormatter);

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

            Color bookingColor = bookingColors[bookings.indexOf(booking) % bookingColors.length];

            // Render booking in merged cells
            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    JLabel cell = calendarCells[row][col];
                    if (row == startRow && col == startCol) {
                        cell.setText("<html><center>" + booking.getActivityName() + "</center></html>");
                    } else {
                        cell.setText("");
                    }
                    cell.setBackground(bookingColor);
                    cell.setOpaque(true);
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

                    cell.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            showBookingDetails(booking);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            cell.setBackground(bookingColor.darker());
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            cell.setBackground(bookingColor);
                        }
                    });
                }
            }
        }
    }

    private void showBookingDetails(Booking booking) {
        // When clicked, fetch full event details from SQL and show them
        Booking fullBooking = sqlConnection.fetchEventDetails(booking.getId());
        if (fullBooking != null) {
            JOptionPane.showMessageDialog(this,
                    "Event Details:\n" +
                            "Activity: " + fullBooking.getActivityName() + "\n" +
                            "Date: " + fullBooking.getStartDate() + " to " + fullBooking.getEndDate() + "\n" +
                            "Time: " + fullBooking.getStartTime() + " - " + fullBooking.getEndTime() + "\n" +
                            "Venue: " + fullBooking.getRoom(),
                    "Event Details",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Event details not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to display the New Event form
    private void showNewEventForm() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Create New Event", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 450);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Fields to collect event information
        JTextField bookingIdField = new JTextField();
        JTextField startDateField = new JTextField("2025-03-01");  // yyyy-MM-dd
        JTextField endDateField = new JTextField("2025-03-01");
        JTextField startTimeField = new JTextField("10:00");       // HH:mm
        JTextField endTimeField = new JTextField("12:00");
        JTextField activityIdField = new JTextField("1");          // Activity ID
        JTextField venueIdField = new JTextField("1");             // Venue ID (to get room)
        JTextField staffIdField = new JTextField("1001");          // Staff ID (bookedBy)
        JTextField clientIdField = new JTextField("2001");         // Client ID (to get company name)
        JCheckBox heldCheck = new JCheckBox("Held");

        dialog.add(new JLabel("Booking ID:"));
        dialog.add(bookingIdField);
        dialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("End Date (yyyy-MM-dd):"));
        dialog.add(endDateField);
        dialog.add(new JLabel("Start Time (HH:mm):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("End Time (HH:mm):"));
        dialog.add(endTimeField);
        dialog.add(new JLabel("Activity ID:"));
        dialog.add(activityIdField);
        dialog.add(new JLabel("Venue ID:"));
        dialog.add(venueIdField);
        dialog.add(new JLabel("Staff ID:"));
        dialog.add(staffIdField);
        dialog.add(new JLabel("Client ID:"));
        dialog.add(clientIdField);
        dialog.add(new JLabel("Held:"));
        dialog.add(heldCheck);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(ev -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                LocalDate sDate = LocalDate.parse(startDateField.getText().trim());
                LocalDate eDate = LocalDate.parse(endDateField.getText().trim());
                LocalTime sTime = LocalTime.parse(startTimeField.getText().trim());
                LocalTime eTime = LocalTime.parse(endTimeField.getText().trim());
                int activityId = Integer.parseInt(activityIdField.getText().trim());
                int venueId = Integer.parseInt(venueIdField.getText().trim());
                int staffId = Integer.parseInt(staffIdField.getText().trim());
                int clientId = Integer.parseInt(clientIdField.getText().trim());
                boolean isHeld = heldCheck.isSelected();

                // Create placeholder Activity and Venue objects
                Activity act = new Activity(activityId, "Activity " + activityId);
                Venue ven = new Venue(venueId, "Room " + venueId, "Hall", 300);
                List<Seat> seats = new ArrayList<>();

                Booking newBooking = new Booking(
                        bookingId,
                        sDate,
                        eDate,
                        sTime,
                        eTime,
                        act,
                        ven,
                        isHeld,
                        "", // holdExpiryDate empty if not used
                        seats,
                        String.valueOf(staffId),
                        ven.getName(),
                        String.valueOf(clientId),
                        null  // contactDetails
                );

                boolean inserted = sqlConnection.insertEvent(newBooking);
                if (inserted) {
                    JOptionPane.showMessageDialog(dialog, "Event created successfully!");
                    dialog.dispose();
                    refreshCalendar();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(ev -> dialog.dispose());
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}