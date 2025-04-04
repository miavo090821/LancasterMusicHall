package GUI.MenuPanels.Calendar;

import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
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
    private enum CalendarView {WEEK, DAY, MONTH}

    private CalendarView currentView = CalendarView.WEEK;
    private CalendarViewPanel currentViewPanel;
    private List<Event> events = new ArrayList<>();

    // UI Components
    private JComboBox<String> viewCombo;

    // Date formatting
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    private JLabel viewRangeLabel = new JLabel(); // Initialize here

    public CalendarPanel(MainMenuGUI mainMenu) {
        setLayout(new BorderLayout());
        initializeSampleEvents();

        // Create header panel first
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.white);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        viewRangeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(viewRangeLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Then create initial view
        switchToView(CalendarView.WEEK);
        setupBottomPanel(mainMenu);
    }

    private void switchToView(CalendarView view) {
        currentView = view;

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case WEEK:
                currentViewPanel = new WeekViewPanel(LocalDate.now(), events);
                break;
            case DAY:
                currentViewPanel = new DayViewPanel(LocalDate.now(), events);
                break;
            case MONTH:
                currentViewPanel = new MonthViewPanel(LocalDate.now(), events);
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
        updateHeaderText();
        revalidate();
        repaint();
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
                LocalDate newDate = LocalDate.of(
                        selectedDate.getYear() + 1900,
                        selectedDate.getMonth() + 1,
                        selectedDate.getDate()
                );
                currentViewPanel.setViewDate(newDate);
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

    private void switchView() {
        String selected = (String) viewCombo.getSelectedItem();
        currentView = CalendarView.valueOf(selected.toUpperCase());
        switchToView(currentView);
    }

    private void navigate(int direction) {
        currentViewPanel.navigate(direction);
        updateView();
    }

    private void navigateToToday() {
        currentViewPanel.setViewDate(LocalDate.now());
        updateView();
    }

    private void updateView() {
        currentViewPanel.refreshView();
        updateHeaderText();
    }

    private void updateHeaderText() {
        String headerText = "";
        switch (currentView) {
            case WEEK:
                headerText = currentViewPanel.getViewStartDate().format(dateFormatter) + " to " +
                        currentViewPanel.getViewEndDate().format(dateFormatter);
                break;
            case DAY:
                headerText = currentViewPanel.getViewStartDate().format(dateFormatter);
                break;
            case MONTH:
                headerText = currentViewPanel.getViewStartDate().format(monthYearFormatter);
                break;
        }
        viewRangeLabel.setText(headerText);
    }

//    private void showEventDetails(Event event) {
//        JOptionPane.showMessageDialog(this,
//                "Event Details:\n" +
//                        "ID: " + event.getId() + "\n" +
//                        "Name: " + event.getName() + "\n" +
//                        "Date: " + event.getStartDate() + " to " + event.getEndDate() + "\n" +
//                        "Time: " + event.getStartTime() + " - " + event.getEndTime() + "\n" +
//                        "Held: " + (event.isHeld() ? "Yes" : "No") + "\n" +
//                        "Hold Expiry: " + event.getHoldExpiryDate() + "\n" +
//                        "Venue: " + event.getVenue().getName() + "\n" +
//                        "Booked By: " + event.getBookedBy() + "\n" +
//                        "Company: " + event.getCompanyName(),
//                "Event Details",
//                JOptionPane.INFORMATION_MESSAGE);
//    }

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
                venue = switch (venueId) {
                    case 1 -> new Venue(1, "Main Hall", "Conference", 500);
                    case 2 -> new Venue(2, "Room 101", "Meeting", 30);
                    case 3 -> new Venue(3, "Auditorium", "Performance", 1000);
                    default -> new Venue(venueId, "Room " + venueId, "Generic", 50);
                };

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
                currentViewPanel.refreshView();
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
                "operations",
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
                "marketing",
                "Main Hall",
                "Company",
                null
        ));
    }
}