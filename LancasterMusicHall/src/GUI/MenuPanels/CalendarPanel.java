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

    private enum CalendarView { WEEK, DAY, MONTH }
    private CalendarView currentView = CalendarView.WEEK;
    private JPanel calendarViewPanel; // This will hold our different views

    // Calendar grid components
    private JLabel[][] calendarCells;
    private String[] days;
    private String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    // Date range tracking - always represents the current view's date range
    private LocalDate viewStartDate = LocalDate.now().with(DayOfWeek.MONDAY);
    private LocalDate viewEndDate = viewStartDate.plusDays(6);

    // Local storage for events
    private List<Event> events = new ArrayList<>();

    private final Color[] eventColors = {
            new Color(200, 230, 255),  // Operations color (light blue)
            new Color(255, 230, 200)   // Marketing color (light orange)
    };

    /**
     * Returns color based on who booked the event
     * Light blue for operations, light orange for marketing
     */
    private Color getEventColor(Event event) {
        if (event.getBookedBy().equalsIgnoreCase("operations")) {
            return eventColors[0]; // First color for operations
        } else {
            return eventColors[1]; // Second color for marketing
        }
    }

    // Date formatting utilities
    private final DateTimeFormatter dayHeaderFormatter = DateTimeFormatter.ofPattern("EEEE d", Locale.ENGLISH);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    // UI Components that need view updates
    private JLabel viewRangeLabel;
    private JComboBox<String> viewCombo;

    /**
     * Constructs the calendar panel with all view modes and navigation controls
     */
    public CalendarPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        setLayout(new BorderLayout());
        setBackground(new Color(200, 170, 230));

        // Initialize days array before creating views
        days = new String[7];
        updateDayHeaders(); // Initialize day labels

        initializeSampleEvents();
        setupCalendarViews();
        setupBottomPanel(mainMenu);

        // Refresh calendar immediately after setup
        refreshCalendar();
    }

    private void updateDayHeaders() {
        // Update the days array with current dates
        for (int i = 0; i < 7; i++) {
            days[i] = viewStartDate.plusDays(i).format(dayHeaderFormatter);
        }
    }

    private void setupBottomPanel(MainMenuGUI mainMenu) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Left side controls
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);

        // View options
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        viewPanel.setBackground(Color.WHITE);

        JLabel viewLabel = new JLabel("View:");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        viewCombo = new JComboBox<>(new String[]{"Week", "Day", "Month"});
        viewCombo.setPreferredSize(new Dimension(100, 25));
        viewCombo.addActionListener(e -> switchView());
        viewPanel.add(viewLabel);
        viewPanel.add(viewCombo);

        leftColumn.add(viewPanel);

        // Center: New Event button
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 35));
        centerPanel.setBackground(Color.white);
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> showNewEventForm());
        centerPanel.add(newEventButton);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);

        // Right side: Date picker and navigation
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);

        // Date picker
        JPanel datePickerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        datePickerPanel.setBackground(Color.WHITE);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(100, 25));
        datePicker.setDate(new Date());
        datePicker.addPropertyChangeListener("date", e -> {
            Date selectedDate = datePicker.getDate();
            if (selectedDate != null) {
                viewStartDate = LocalDate.of(
                        selectedDate.getYear() + 1900,
                        selectedDate.getMonth() + 1,
                        selectedDate.getDate()
                ).with(DayOfWeek.MONDAY);
                viewEndDate = viewStartDate.plusDays(6);
                updateView();
            }
        });

        datePickerPanel.add(dateLabel);
        datePickerPanel.add(datePicker);
        rightColumn.add(datePickerPanel);

        // Navigation buttons
        JPanel navButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navButtonPanel.setBackground(Color.WHITE);

        JButton leftArrow = new JButton("<");
        leftArrow.setFont(new Font("Arial", Font.BOLD, 16));
        leftArrow.setPreferredSize(new Dimension(50, 30));
        leftArrow.addActionListener(e -> navigate(-1));

        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> navigateToToday());

        JButton rightArrow = new JButton(">");
        rightArrow.setFont(new Font("Arial", Font.BOLD, 16));
        rightArrow.setPreferredSize(new Dimension(50, 30));
        rightArrow.addActionListener(e -> navigate(1));

        mainMenu.stylizeButton(leftArrow);
        mainMenu.stylizeButton(rightArrow);
        mainMenu.stylizeButton(todayButton);

        navButtonPanel.add(leftArrow);
        navButtonPanel.add(todayButton);
        navButtonPanel.add(rightArrow);
        rightColumn.add(navButtonPanel);

        // Set preferred sizes for layout balance
        leftColumn.setPreferredSize(new Dimension(300, 120));
        rightColumn.setPreferredSize(new Dimension(300, 120));
        centerPanel.setPreferredSize(new Dimension(300, 120));

        bottomPanel.add(leftColumn, BorderLayout.WEST);
        bottomPanel.add(rightColumn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Initializes the three calendar view modes and their container
     */
    private void setupCalendarViews() {
        // Create container with CardLayout to switch between views
        calendarViewPanel = new JPanel(new CardLayout());

        // Create and add all view panels
        calendarViewPanel.add(createWeekView(), "WEEK");
        calendarViewPanel.add(createDayView(), "DAY");
        calendarViewPanel.add(createMonthView(), "MONTH");

        // Add the view container to the main panel
        add(calendarViewPanel, BorderLayout.CENTER);
    }

    /**
     * Creates and configures the week view panel
     */
    private JPanel createWeekView() {
        JPanel weekPanel = new JPanel(new BorderLayout());

        // Header showing date range
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.white);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        viewRangeLabel = new JLabel(
                viewStartDate.format(dateFormatter) + " to " + viewEndDate.format(dateFormatter),
                SwingConstants.CENTER
        );
        viewRangeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(viewRangeLabel);

        // Main calendar grid
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setPreferredSize(new Dimension(550, 400));
        gridPanel.setBackground(Color.WHITE);

        // Initialize grid cells and headers
        initializeWeekGrid(gridPanel);

        // Add components to week panel
        weekPanel.add(headerPanel, BorderLayout.NORTH);
        weekPanel.add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        return weekPanel;
    }

    /**
     * Initializes the week view grid with time slots and day headers
     */
    private void initializeWeekGrid(JPanel gridPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Update day headers with current dates
        updateDayHeaders();

        // Top-left corner spacer
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.1; gbc.weighty = 0;
        gridPanel.add(new JLabel(""), gbc);

        // Day headers
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1; gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = createDayHeaderLabel(days[i]);
            gridPanel.add(dayLabel, gbc);
        }

        // Initialize calendar cells
        calendarCells = new JLabel[times.length][days.length];

        // Time slots and event cells
        for (int row = 0; row < times.length; row++) {
            // Time label column
            gbc.gridx = 0; gbc.gridy = row + 1;
            gbc.weightx = 0.1; gbc.ipady = 40;
            gridPanel.add(createTimeLabel(times[row]), gbc);

            // Day cells
            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1; gbc.gridy = row + 1;
                gbc.weightx = 0.5; gbc.weighty = 0.5; gbc.ipady = 40;
                calendarCells[row][col] = createCalendarCell();
                gridPanel.add(calendarCells[row][col], gbc);
            }
        }
    }

    /**
     * Creates a styled day header label
     */
    private JLabel createDayHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(80, 30));
        label.setBackground(new Color(220, 200, 255));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return label;
    }

    /**
     * Creates a styled time slot label
     */
    private JLabel createTimeLabel(String time) {
        JLabel label = new JLabel(time, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(30, 60));
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return label;
    }

    /**
     * Creates an empty calendar cell for events
     */
    private JLabel createCalendarCell() {
        JLabel cell = new JLabel("", SwingConstants.CENTER);
        cell.setPreferredSize(new Dimension(80, 30));
        cell.setFont(new Font("Arial", Font.BOLD, 10));
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cell.setOpaque(true);
        cell.setBackground(Color.WHITE);
        return cell;
    }

    /**
     * Creates the day view panel
     */
    private JPanel createDayView() {
        JPanel dayPanel = new JPanel(new BorderLayout());
        // Implementation would go here
        dayPanel.add(new JLabel("Day View - Under Construction"), BorderLayout.CENTER);
        return dayPanel;
    }

    /**
     * Creates the month view panel
     */
    private JPanel createMonthView() {
        JPanel monthPanel = new JPanel(new BorderLayout());
        // Implementation would go here
        monthPanel.add(new JLabel("Month View - Under Construction"), BorderLayout.CENTER);
        return monthPanel;
    }

    /**
     * Handles view switching between Week/Day/Month modes
     */
    private void switchView() {
        String selected = (String)viewCombo.getSelectedItem();
        currentView = CalendarView.valueOf(selected.toUpperCase());

        // Update the CardLayout to show the selected view
        CardLayout cl = (CardLayout)calendarViewPanel.getLayout();
        cl.show(calendarViewPanel, selected.toUpperCase());

        // Adjust date range for the new view
        adjustViewDates();
        updateView();
    }

    /**
     * Adjusts the date range when switching views
     */
    private void adjustViewDates() {
        switch(currentView) {
            case WEEK:
                viewStartDate = viewStartDate.with(DayOfWeek.MONDAY);
                viewEndDate = viewStartDate.plusDays(6);
                break;
            case DAY:
                viewEndDate = viewStartDate; // Single day
                break;
            case MONTH:
                viewStartDate = viewStartDate.withDayOfMonth(1);
                viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
                break;
        }
    }

    /**
     * Updates all view components after navigation or view change
     */
    private void updateView() {
        updateHeaderText();
        refreshCalendar();
    }

    /**
     * Updates the header text based on current view
     */
    private void updateHeaderText() {
        switch(currentView) {
            case WEEK:
                viewRangeLabel.setText(viewStartDate.format(dateFormatter) + " to " + viewEndDate.format(dateFormatter));
                break;
            case DAY:
                viewRangeLabel.setText(viewStartDate.format(dateFormatter));
                break;
            case MONTH:
                viewRangeLabel.setText(viewStartDate.format(monthYearFormatter));
                break;
        }
    }

    /**
     * Resets the view to today's date in current view mode
     */
    private void navigateToToday() {
        viewStartDate = LocalDate.now();
        adjustViewDates();
        updateView();
    }

    /**
     * Filters events for the current view's date range
     */
    private List<Event> filterEventsForCurrentView() {
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (!event.getStartDate().isBefore(viewStartDate) &&
                    !event.getStartDate().isAfter(viewEndDate)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    /**
     * Renders events in week view with color coding based on bookedBy department
     */
    private void renderWeekEvents(List<Event> events) {
        // Clear all cells
        resetWeekGrid();

        // update day headers
        for (int i = 0; i < days.length; i++) {
            days[i] = viewStartDate.plusDays(i).format(dayHeaderFormatter);
        }

        for (Event event : events) {
            // Calculate grid position
            int dayColumn = (int)(event.getStartDate().toEpochDay() - viewStartDate.toEpochDay());
            int startRow = findTimeSlot(event.getStartTime().getHour());
            int endRow = findTimeSlot(event.getEndTime().getHour());

            if (dayColumn >= 0 && dayColumn < 7 && startRow != -1 && endRow != -1) {
                // Determine color based on bookedBy department
                Color eventColor = determineEventColor(event.getBookedBy());

                // Render event block
                for (int row = startRow; row <= endRow; row++) {
                    JLabel cell = calendarCells[row][dayColumn];
                    if (row == startRow) {
                        String displayText = String.format("<html><center>%s<br/><small>%s</small></center></html>",
                                event.getName(),
                                event.getBookedBy());
                        cell.setText(displayText);
                    }
                    styleEventCell(cell, eventColor, row == startRow, row == endRow);
                    attachEventListeners(cell, event, eventColor);
                }
            }
        }
    }

    /**
     * Determines event color based on booking department
     */
    private Color determineEventColor(String bookedBy) {
        if (bookedBy.equalsIgnoreCase("operations")) {
            return new Color(200, 230, 255);  // Light blue for operations
        } else if (bookedBy.equalsIgnoreCase("marketing")) {
            return new Color(255, 230, 200);  // Light orange for marketing
        } else {
            return new Color(230, 255, 200);  // Default green for other departments
        }
    }

    /**
     * Clears all cells in the week view grid
     */
    private void resetWeekGrid() {
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                JLabel cell = calendarCells[row][col];
                cell.setText("");
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                // Remove existing listeners
                for (MouseListener l : cell.getMouseListeners()) {
                    cell.removeMouseListener(l);
                }
            }
        }
    }

    /**
     * Styles an event cell with appropriate colors and borders
     */
    private void styleEventCell(JLabel cell, Color color, boolean isFirstRow, boolean isLastRow) {
        cell.setBackground(color);
        cell.setOpaque(true);
        cell.setBorder(BorderFactory.createMatteBorder(
                isFirstRow ? 1 : 0, 1, isLastRow ? 1 : 0, 1, Color.BLACK
        ));
    }

    /**
     * Attaches mouse listeners to an event cell
     */
    private void attachEventListeners(JLabel cell, Event event, Color baseColor) {
        cell.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showEventDetails(event);
            }
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                cell.setBackground(baseColor);
            }
        });
    }

    /**
     * Finds the row index for a given hour in the time slots
     */
    private int findTimeSlot(int hour) {
        for (int i = 0; i < times.length; i++) {
            if (Integer.parseInt(times[i]) == hour) {
                return i;
            }
        }
        return -1;
    }
    /**
     * Navigates forward or backward in time based on current view
     */
    private void navigate(int direction) {
        switch(currentView) {
            case WEEK:
                viewStartDate = viewStartDate.plusWeeks(direction);
                viewEndDate = viewStartDate.plusDays(6);
                break;
            case DAY:
                viewStartDate = viewStartDate.plusDays(direction);
                viewEndDate = viewStartDate;
                break;
            case MONTH:
                viewStartDate = viewStartDate.plusMonths(direction);
                viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
                break;
        }
        updateView();
    }

    /**
     * Handles date picker changes
     */
    private void onDatePickerChange(JDateChooser datePicker) {
        Date selectedDate = datePicker.getDate();
        if (selectedDate != null) {
            viewStartDate = LocalDate.of(
                    selectedDate.getYear() + 1900,
                    selectedDate.getMonth() + 1,
                    selectedDate.getDate()
            );
            adjustViewDates();
            updateView();
        }
    }

    // Initialize with some sample events
    private void initializeSampleEvents() {
        Venue mainHall = new Venue(1, "Main Hall", "Conference", 500);
        Venue room101 = new Venue(2, "Room 101", "Meeting", 30);

        LocalDate thisMonday = LocalDate.now().with(DayOfWeek.MONDAY);

        // Operations event
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
                "operations", // Changed to operations
                "Room 101",
                "Team",
                null
        ));

        // Marketing event
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
                "marketing", // Changed to marketing
                "Main Hall",
                "Company",
                null
        ));
    }

    // Refresh the calendar
    public void refreshCalendar() {
        List<Event> eventsToShow = filterEventsForCurrentView();

        switch(currentView) {
            case WEEK:
                updateDayHeaders(); // Ensure day labels are up to date
                renderWeekEvents(eventsToShow);
                break;
            case DAY:
                // Day view implementation
                break;
            case MONTH:
                // Month view implementation
                break;
        }

        revalidate();
        repaint();
    }

    /**
     * Renders events in the calendar grid with proper coloring and styling
     */
    private void renderEvents(ArrayList<Event> events) {
        // Clear all cells first
        resetWeekGrid();

        if (events.isEmpty()) return;

        for (Event event : events) {
            LocalDate eventDate = event.getStartDate();
            LocalTime startTime = event.getStartTime();
            LocalTime endTime = event.getEndTime();

            // Calculate day column (0=Monday, 6=Sunday)
            int dayColumn = (int)(eventDate.toEpochDay() - viewStartDate.toEpochDay());

            // Only proceed if event is in current week view
            if (dayColumn < 0 || dayColumn >= days.length) continue;

            // Find time slot rows
            int startRow = findTimeSlot(startTime.getHour());
            int endRow = findTimeSlot(endTime.getHour());

            // Skip if we couldn't find matching time slots
            if (startRow == -1 || endRow == -1) continue;

            // Get consistent color for this event
            Color eventColor = getEventColor(event);

            // Render the event block
            for (int row = startRow; row <= endRow; row++) {
                JLabel cell = calendarCells[row][dayColumn];

                // Only show text in the first cell
                if (row == startRow) {
                    cell.setText("<html><center>" + event.getName() + "</center></html>");
                }

                // Style the cell
                styleEventCell(cell, eventColor, row == startRow, row == endRow);

                // Add interactivity
                attachEventListeners(cell, event, eventColor);
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