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
import java.time.DayOfWeek;
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

    // Define the diary/calendar view date range
    private LocalDate weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
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

    // Formatters as instance variables
    private final DateTimeFormatter dayHeaderFormatter = DateTimeFormatter.ofPattern("EEEE d", Locale.ENGLISH);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CalendarPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;

        setLayout(new BorderLayout());
        setBackground(new Color(200, 170, 230));

        // Initialize with sample events
        initializeSampleEvents();

        // Prepare day headers with day name and date
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayHeaderFormatter);
        }

        // Create week range panel
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

        // Day headers with day name and date
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(80, 30));
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
                cell.setPreferredSize(new Dimension(80, 30));
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
        scrollPane.setPreferredSize(new Dimension(720, 480));
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

        // Left side: New panel with controls
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);

        // Upper panel: View options
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        viewPanel.setBackground(Color.WHITE);

        JLabel viewLabel = new JLabel("View:");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JComboBox<String> viewCombo = new JComboBox<>(new String[]{"Week", "Day", "Month"});
        viewCombo.setPreferredSize(new Dimension(100, 25));
        viewPanel.add(viewLabel);
        viewPanel.add(viewCombo);

        // Lower panel: Print button
        JPanel printPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        printPanel.setBackground(Color.WHITE);

        JButton printButton = new JButton("Print Week");
        printButton.setFont(new Font("Arial", Font.PLAIN, 12));
        printButton.setPreferredSize(new Dimension(120, 30));
        printPanel.add(printButton);

        leftColumn.add(viewPanel);
        leftColumn.add(printPanel);

        // Center: New Event button
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        centerPanel.setBackground(Color.white);
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
                // Adjust to start of week (Monday)
                while (weekStart.getDayOfWeek().getValue() != 1) { // 1 is Monday
                    weekStart = weekStart.minusDays(1);
                }
                weekEnd = weekStart.plusDays(6);
                updateDayHeaders();
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

        // Left arrow button (previous week)
        JButton leftArrow = new JButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 16));
        leftArrow.setPreferredSize(new Dimension(50, 30));
        leftArrow.addActionListener(e -> {
            weekStart = weekStart.minusDays(7);
            weekEnd = weekStart.plusDays(6);  // Fixed: Should be plusDays(6) not minusDays(6)
            updateDayHeaders();
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        // Today button
        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> {
            weekStart = LocalDate.now();
            // Adjust to start of week (Monday)
            while (weekStart.getDayOfWeek().getValue() != 1) { // 1 is Monday
                weekStart = weekStart.minusDays(1);
            }
            weekEnd = weekStart.plusDays(6);
            updateDayHeaders();
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        // Right arrow button
        JButton rightArrow = new JButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 16));
        rightArrow.setPreferredSize(new Dimension(50, 30));
        rightArrow.addActionListener(e -> {
            weekStart = weekStart.plusDays(7);
            weekEnd = weekStart.plusDays(6);
            updateDayHeaders();
            weekRangeLabel.setText(weekStart.format(dateFormatter) + " to " + weekEnd.format(dateFormatter));
            refreshCalendar();
        });

        leftColumn.setPreferredSize(new Dimension(300,120));
        rightColumn.setPreferredSize(new Dimension(300,120));
        centerPanel.setPreferredSize(new Dimension(300,120));

        mainMenu.stylizeButton(leftArrow);
        mainMenu.stylizeButton(rightArrow);
        mainMenu.stylizeButton(todayButton);

        navButtonPanel.add(leftArrow);
        navButtonPanel.add(todayButton);
        navButtonPanel.add(rightArrow);
        rightColumn.add(navButtonPanel);

        bottomPanel.add(leftColumn, BorderLayout.WEST);
        bottomPanel.add(rightColumn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateDayHeaders() {
        // Update the days array
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayHeaderFormatter);
        }

        // Update the day labels in the calendar grid
        Component[] components = calendarCells[0][0].getParent().getComponents();
        for (Component comp : components) {
            GridBagConstraints gbc = ((GridBagLayout)comp.getParent().getLayout()).getConstraints(comp);
            if (gbc.gridy == 0 && gbc.gridx >= 1 && gbc.gridx <= 7 && comp instanceof JLabel) {
                ((JLabel)comp).setText(days[gbc.gridx - 1]);
            }
        }
    }

    // Initialize with some sample events
    private void initializeSampleEvents() {
        Venue mainHall = new Venue(1, "Main Hall", "Conference", 500);
        Venue room101 = new Venue(2, "Room 101", "Meeting", 30);

        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);

        // This week's events
        events.add(new Event(
                1,
                "Weekly Team Meeting",
                thisMonday.plusDays(2), // Wednesday
                thisMonday.plusDays(2),
                LocalTime.of(10, 0),
                LocalTime.of(11, 30),
                false,
                "",
                room101,
                new ArrayList<>(),
                "manager",
                "Room 101",
                "Team",
                null
        ));

        // Next week's events
        events.add(new Event(
                2,
                "Project Deadline",
                LocalDate.of(2025, 4, 3),
                LocalDate.of(2025, 4, 3),
                LocalTime.of(15, 0),
                LocalTime.of(17, 0),
                false,
                "",
                mainHall,
                new ArrayList<>(),
                "director",
                "Main Hall",
                "Company",
                null
        ));
    }

    // Refresh the calendar
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

    private void renderEvents(ArrayList<Event> events) {
        // Clear all cells
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                JLabel cell = calendarCells[row][col];
                cell.setText("");
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                // Remove existing mouse listeners
                for (MouseListener listener : cell.getMouseListeners()) {
                    cell.removeMouseListener(listener);
                }
            }
        }

        if (events.isEmpty()) return;

        for (Event event : events) {
            LocalDate eventDate = event.getStartDate();
            LocalTime startTime = event.getStartTime();
            LocalTime endTime = event.getEndTime();

            // Calculate day offset from week start (0=Monday, 6=Sunday)
            long daysOffset = eventDate.toEpochDay() - weekStart.toEpochDay();
            int col = (int)daysOffset;

            // Only proceed if event is in current week view
            if (col < 0 || col >= days.length) continue;

            // Find time slot rows
            int startRow = findTimeSlot(startTime.getHour());
            int endRow = findTimeSlot(endTime.getHour());

            // If we couldn't find matching slots, skip this event
            if (startRow == -1 || endRow == -1) continue;

            Color eventColor = eventColors[events.indexOf(event) % eventColors.length];

            // Render the event block
            for (int row = startRow; row <= endRow; row++) {
                JLabel cell = calendarCells[row][col];
                if (row == startRow) { // Only show text in first cell
                    cell.setText("<html><center>" + event.getName() + "</center></html>");
                }
                cell.setBackground(eventColor);
                cell.setOpaque(true);

                // Custom border to show merged cells
                cell.setBorder(BorderFactory.createMatteBorder(
                        (row == startRow) ? 1 : 0, // top
                        1, // left
                        (row == endRow) ? 1 : 0, // bottom
                        1, // right
                        Color.BLACK
                ));

                // Add hover and click effects
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

    // Helper method to find time slot row
    private int findTimeSlot(int hour) {
        for (int i = 0; i < times.length; i++) {
            if (Integer.parseInt(times[i]) == hour) {
                return i;
            }
        }
        return -1;
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