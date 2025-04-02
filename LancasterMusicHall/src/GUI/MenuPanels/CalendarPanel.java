package GUI.MenuPanels;

import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
import operations.entities.ContactDetails;
import operations.entities.Seat;
import operations.entities.Venue;
import operations.module.Event;

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
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // Calendar grid components
    private JLabel[][] calendarCells;
    private String[] days;
    private String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    // Define the diary/calendar view date range (one week view)
    private LocalDate weekStart = LocalDate.of(2025, 3, 1);
    private LocalDate weekEnd = weekStart.plusDays(6);

    // Local storage for events
    private List<Event> events = new ArrayList<>();

    private final Color[] eventColors = {
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

        setLayout(new BorderLayout());
        setBackground(new Color(200, 170, 230));

        // Initialize with sample events
        initializeSampleEvents();

        // Prepare day headers with full day names
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH); // Full day name
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter); // e.g., "Monday"
        }

        // Create week range panel
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JPanel weekRangePanel = new JPanel();
        weekRangePanel.setBackground(Color.white);
        weekRangePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel weekRangeLabel = new JLabel(
                weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter),
                SwingConstants.CENTER
        );
        weekRangeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        weekRangePanel.add(weekRangeLabel);

        // Build calendar grid using GridBagLayout
        JPanel calendarPanel = new JPanel(new GridBagLayout());
        calendarPanel.setPreferredSize(new Dimension(550, 400));
        calendarPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Top-left corner
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(30, 30));
        cornerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        calendarPanel.add(cornerLabel, gbc);

        calendarCells = new JLabel[times.length][days.length];

        // Day headers with full day names
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(80, 30)); // Wider to accommodate full day names
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
                cell.setPreferredSize(new Dimension(80, 30)); // Wider cells to match headers
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                calendarCells[row][col] = cell;
                calendarPanel.add(cell, gbc);
            }
        }

        // Load events and render them into the calendar grid
        refreshCalendar();

        // Wrap calendarPanel in a scroll pane and add to center
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(new Dimension(720, 480)); // Wider to accommodate full day names
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Create a container for the week range and calendar
        JPanel calendarContainer = new JPanel(new BorderLayout());
        calendarContainer.add(weekRangePanel, BorderLayout.NORTH);
        calendarContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(calendarContainer);
        add(centerContainer, BorderLayout.CENTER);

        // Bottom Panel with controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Left side: Empty space
        bottomPanel.add(Box.createHorizontalGlue(), BorderLayout.WEST);

        // Center: New Event button
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        centerPanel.setBackground(Color.WHITE);
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> showNewEventForm());
        centerPanel.add(newEventButton);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);

        // Right side: Two panels stacked vertically
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);

        // Upper panel: Date Picker
        JPanel datePickerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        datePickerPanel.setBackground(Color.WHITE);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(100, 25));
        datePicker.setDate(new Date());

        datePicker.addPropertyChangeListener("date", evt -> {
            Date selectedDate = datePicker.getDate();
            if (selectedDate != null) {
                weekStart = LocalDate.of(
                        selectedDate.getYear() + 1900,
                        selectedDate.getMonth() + 1,
                        selectedDate.getDate()
                );
                // Adjust to start of week (Sunday)
                while (weekStart.getDayOfWeek().getValue() != 7) { // 7 is Sunday
                    weekStart = weekStart.minusDays(1);
                }
                weekEnd = weekStart.plusDays(6);
                updateDayHeaders(dayFormatter);
                weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
                refreshCalendar();
            }
        });

        datePickerPanel.add(dateLabel);
        datePickerPanel.add(datePicker);
        rightColumn.add(datePickerPanel);

        // Lower panel: Navigation buttons
        JPanel navButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navButtonPanel.setBackground(Color.WHITE);

        // Left arrow button
        JButton leftArrow = new JButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 16));
        leftArrow.setPreferredSize(new Dimension(50, 30));
        leftArrow.addActionListener(e -> {
            weekStart = weekStart.minusDays(7);
            weekEnd = weekEnd.minusDays(7);
            updateDayHeaders(dayFormatter);
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        // Today button
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            weekStart = LocalDate.now();
            // Adjust to start of week (Sunday)
            while (weekStart.getDayOfWeek().getValue() != 7) { // 7 is Sunday
                weekStart = weekStart.minusDays(1);
            }
            weekEnd = weekStart.plusDays(6);
            updateDayHeaders(dayFormatter);
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        // Right arrow button
        JButton rightArrow = new JButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 16));
        rightArrow.setPreferredSize(new Dimension(50, 30));
        rightArrow.addActionListener(e -> {
            weekStart = weekStart.plusDays(7);
            weekEnd = weekEnd.plusDays(7);
            updateDayHeaders(dayFormatter);
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        mainMenu.stylizeButton(leftArrow);
        mainMenu.stylizeButton(rightArrow);
        mainMenu.stylizeButton(todayButton);

        navButtonPanel.add(leftArrow);
        navButtonPanel.add(todayButton);
        navButtonPanel.add(rightArrow);
        rightColumn.add(navButtonPanel);

        bottomPanel.add(rightColumn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateDayHeaders(DateTimeFormatter dayFormatter) {
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter); // Full day names
        }
    }

    // Initialize with some sample events
    private void initializeSampleEvents() {
        Venue mainHall = new Venue(1, "Main Hall", "Conference", 500);
        Venue room101 = new Venue(2, "Room 101", "Meeting", 30);
        Venue auditorium = new Venue(3, "Auditorium", "Performance", 1000);

        // Empty lists and null values for optional fields
        List<Seat> emptySeats = new ArrayList<>();
        ContactDetails emptyContact = null;

        events.add(new Event(
                1,
                "Tech Conference",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 1),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                false,
                "",
                mainHall,
                emptySeats,
                "admin",
                mainHall.getName(),
                "Tech Corp",
                emptyContact
        ));
        events.add(new Event(
                1,
                "Thor",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 1),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                false,
                "",
                mainHall,
                emptySeats,
                "admin",
                mainHall.getName(),
                "Tech Corp",
                emptyContact
        ));

        events.add(new Event(
                2,
                "Team Meeting",
                LocalDate.of(2025, 3, 3),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                true,
                "2025-03-02",
                room101,
                emptySeats,
                "manager1",
                room101.getName(),
                "Sales Team",
                emptyContact
        ));

        events.add(new Event(
                3,
                "Music Concert",
                LocalDate.of(2025, 3, 5),
                LocalDate.of(2025, 3, 5),
                LocalTime.of(15, 0),
                LocalTime.of(17, 0),
                false,
                "",
                auditorium,
                emptySeats,
                "event_coord",
                auditorium.getName(),
                "Arts Council",
                emptyContact
        ));

        events.add(new Event(
                3,
                "Batman",
                LocalDate.of(2025, 3, 5),
                LocalDate.of(2025, 3, 5),
                LocalTime.of(11, 0),
                LocalTime.of(13, 0),
                false,
                "",
                auditorium,
                emptySeats,
                "event_coord",
                auditorium.getName(),
                "Arts Council",
                emptyContact
        ));

        events.add(new Event(
                3,
                "Batman",
                LocalDate.of(2025, 3, 15),
                LocalDate.of(2025, 3, 15),
                LocalTime.of(11, 0),
                LocalTime.of(13, 0),
                false,
                "",
                auditorium,
                emptySeats,
                "event_coord",
                auditorium.getName(),
                "Arts Council",
                emptyContact
        ));
    }

    // Refresh the calendar view with local events
    public void refreshCalendar() {
        // Filtering events to only show those in the current week
        ArrayList<Event> eventsThisWeek = new ArrayList<>();
        for (Event event : events) {
            LocalDate eventDate = event.getStartDate();
            if (!eventDate.isBefore(weekStart) && !eventDate.isAfter(weekEnd)) {
                eventsThisWeek.add(event);
            }
        }
        renderEvents(eventsThisWeek);
        revalidate();
        repaint();
    }

    // Renders the list of events into the calendar grid
    private void renderEvents(ArrayList<Event> events) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH);

        // Clear all of last week's events
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                JLabel cell = calendarCells[row][col];
                cell.setText("");
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setCursor(Cursor.getDefaultCursor());

                // Remove all mouse listeners
                for (MouseListener listener : cell.getMouseListeners()) {
                    cell.removeMouseListener(listener);
                }
            }
        }

        // Only proceed if there are events to render
        if (events.isEmpty()) {
            return;
        }

        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                calendarCells[row][col].setText("");
                calendarCells[row][col].setBackground(Color.WHITE);
                calendarCells[row][col].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }

        for (Event event : events) {
            LocalDate eStartDate = event.getStartDate();
            LocalDate eEndDate = event.getEndDate();
            LocalTime eStartTime = event.getStartTime();
            LocalTime eEndTime = event.getEndTime();

            int startHour = eStartTime.getHour();
            int endHour = eEndTime.getHour();

            // Format using the same full day name pattern
            String startDayLabel = eStartDate.format(dayFormatter);
            String endDayLabel = eEndDate.format(dayFormatter);

            int startCol = -1, endCol = -1;
            for (int i = 0; i < days.length; i++) {
                if (days[i].equalsIgnoreCase(startDayLabel)) startCol = i;
                if (days[i].equalsIgnoreCase(endDayLabel)) endCol = i;
            }

            // Skip if event doesn't fall in this week
            if (startCol == -1 || endCol == -1 || startCol > endCol) continue;

            int startRow = -1, endRow = -1;
            for (int i = 0; i < times.length; i++) {
                int timeSlotHour = Integer.parseInt(times[i]);
                if (timeSlotHour == startHour) startRow = i;
                if (timeSlotHour == endHour) endRow = i;
            }
            if (startRow == -1 || endRow == -1 || startRow > endRow) continue;

            Color eventColor = eventColors[events.indexOf(event) % eventColors.length];

            // Render event in merged cells
            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    JLabel cell = calendarCells[row][col];
                    if (row == startRow && col == startCol) {
                        cell.setText("<html><center>" + event.getName() + "</center></html>");
                    } else {
                        cell.setText("");
                    }
                    cell.setBackground(eventColor);
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
                            showEventDetails(event);
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                            cell.setBackground(eventColor.darker());
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            cell.setBackground(eventColor);
                        }
                    });
                }
            }
        }
    }

    private void showEventDetails(Event event) {
        JOptionPane.showMessageDialog(this,
                "Event Details:\n" +
                        "ID: " + event.getId() + "\n" +
                        "Name: " + event.getName() + "\n" +
                        "Date: " + event.getStartDate() + " to " + event.getEndDate() + "\n" +
                        "Time: " + event.getStartTime() + " - " + event.getEndTime() + "\n" +
                        "Held: " + (event.isHeld() ? "Yes" : "No") + "\n" +
                        "Hold Expiry: " + event.getHoldExpiryDate() + "\n" +
                        "Venue: " + event.getVenue().getName() + "\n" +
                        "Booked By: " + event.getBookedBy() + "\n" +
                        "Company: " + event.getCompanyName(),
                "Event Details",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to display the New Event form
    private void showNewEventForm() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Create New Event", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 600);
        dialog.setLayout(new GridLayout(0, 2, 10, 10));

        // Fields to collect event information
        JTextField eventIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField startDateField = new JTextField("2025-03-01");
        JTextField endDateField = new JTextField("2025-03-01");
        JTextField startTimeField = new JTextField("10:00");
        JTextField endTimeField = new JTextField("12:00");
        JCheckBox heldCheck = new JCheckBox();
        JTextField holdExpiryField = new JTextField("");
        JTextField venueIdField = new JTextField("1");
        JTextField bookedByField = new JTextField("admin");
        JTextField companyField = new JTextField("Company Inc");

        dialog.add(new JLabel("Event ID:"));
        dialog.add(eventIdField);
        dialog.add(new JLabel("Event Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
        dialog.add(startDateField);
        dialog.add(new JLabel("End Date (yyyy-MM-dd):"));
        dialog.add(endDateField);
        dialog.add(new JLabel("Start Time (HH:mm):"));
        dialog.add(startTimeField);
        dialog.add(new JLabel("End Time (HH:mm):"));
        dialog.add(endTimeField);
        dialog.add(new JLabel("Held:"));
        dialog.add(heldCheck);
        dialog.add(new JLabel("Hold Expiry (yyyy-MM-dd):"));
        dialog.add(holdExpiryField);
        dialog.add(new JLabel("Venue ID:"));
        dialog.add(venueIdField);
        dialog.add(new JLabel("Booked By:"));
        dialog.add(bookedByField);
        dialog.add(new JLabel("Company Name:"));
        dialog.add(companyField);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(ev -> {
            try {
                // Create Venue object based on ID
                Venue venue;
                int venueId = Integer.parseInt(venueIdField.getText().trim());
                switch(venueId) {
                    case 1: venue = new Venue(1, "Main Hall", "Conference", 500); break;
                    case 2: venue = new Venue(2, "Room 101", "Meeting", 30); break;
                    case 3: venue = new Venue(3, "Auditorium", "Performance", 1000); break;
                    default: venue = new Venue(venueId, "Room " + venueId, "Generic", 50);
                }

                Event newEvent = new Event(
                        Integer.parseInt(eventIdField.getText().trim()),
                        nameField.getText().trim(),
                        LocalDate.parse(startDateField.getText().trim()),
                        LocalDate.parse(endDateField.getText().trim()),
                        LocalTime.parse(startTimeField.getText().trim()),
                        LocalTime.parse(endTimeField.getText().trim()),
                        heldCheck.isSelected(),
                        holdExpiryField.getText().trim(),
                        venue,
                        new ArrayList<>(), // Empty seats list
                        bookedByField.getText().trim(),
                        venue.getName(), // Use venue name as room
                        companyField.getText().trim(),
                        null // No contact details
                );

                events.add(newEvent);
                JOptionPane.showMessageDialog(dialog, "Event created successfully!");
                dialog.dispose();
                refreshCalendar();
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