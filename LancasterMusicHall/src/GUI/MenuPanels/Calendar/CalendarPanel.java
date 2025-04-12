package GUI.MenuPanels.Calendar;

import GUI.MainMenuGUI;
import GUI.MenuPanels.Booking.NewBookingForm;
import com.toedter.calendar.JDateChooser;
import operations.entities.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The CalendarPanel class provides an interactive calendar interface for viewing and managing events.
 * It supports three different views: Day, Week, and Month, with navigation controls and event management.
 *
 * <p>Key features include:
 * <ul>
 *   <li>Interactive calendar with Day, Week, and Month views</li>
 *   <li>Navigation through time periods</li>
 *   <li>Integration with the booking system</li>
 *   <li>Visual representation of events</li>
 * </ul>
 *
 * @author Mysarah
 * @version 1.0
 * @see MainMenuGUI
 * @see Event
 */
public class CalendarPanel extends JPanel {
    /** Enum representing the available calendar views */
    private enum CalendarView {WEEK, DAY, MONTH}

    /** The currently displayed date */
    private LocalDate currentDate = LocalDate.now();

    /** The current view mode (Day/Week/Month) */
    private CalendarView currentView = CalendarView.WEEK;

    /** The panel displaying the current view */
    private CalendarViewPanel currentViewPanel;

    /** List of events to display */
    private List<Event> events = new ArrayList<>();

    /** Reference to the main menu for configuration and database access */
    private MainMenuGUI mainMenu;

    // UI Components
    /** Combo box for view selection */
    private JComboBox<String> viewCombo;

    /** Label showing the current view range */
    private JLabel viewRangeLabel;

    /** Date picker for navigation */
    private JDateChooser datePicker;

    /**
     * Constructs a new CalendarPanel with reference to the main menu.
     *
     * @param mainMenu The parent MainMenuGUI that contains this panel
     */
    public CalendarPanel(MainMenuGUI mainMenu) {
        this.mainMenu = mainMenu;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initializeUI();
        switchToView(CalendarView.WEEK);
        setupBottomPanel();
    }

    /**
     * Initializes the UI components for the calendar header.
     */
    private void initializeUI() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        viewRangeLabel = new JLabel("", SwingConstants.CENTER);
        viewRangeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(viewRangeLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Switches to the specified view using the current date.
     *
     * @param view The view to switch to (DAY, WEEK, or MONTH)
     */
    private void switchToView(CalendarView view) {
        switchToView(view, currentDate);
    }

    /**
     * Switches to the specified view at the specified date.
     *
     * @param view The view to switch to (DAY, WEEK, or MONTH)
     * @param date The date to display in the view
     */
    private void switchToView(CalendarView view, LocalDate date) {
        currentView = view;
        currentDate = date;

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case WEEK:
                currentViewPanel = new WeekViewPanel(date, events, mainMenu.getSqlConnection());
                if (viewCombo != null) viewCombo.setSelectedItem("Week");
                break;
            case DAY:
                currentViewPanel = new DayViewPanel(date, events, mainMenu.getSqlConnection());
                if (viewCombo != null) viewCombo.setSelectedItem("Day");
                break;
            case MONTH:
                currentViewPanel = new MonthViewPanel(date, events, mainMenu.getSqlConnection(),
                        d -> switchToView(CalendarView.WEEK, d));
                if (viewCombo != null) viewCombo.setSelectedItem("Month");
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
        updateHeaderText();
        revalidate();
        repaint();

        if (datePicker != null) {
            datePicker.setDate(java.sql.Date.valueOf(currentDate));
        }
    }

    /**
     * Sets up the bottom panel with view controls and navigation.
     */
    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel viewPanel = createViewSelectionPanel();
        JPanel buttonPanel = createNewEventButtonPanel();
        JPanel dateNavPanel = createDateNavigationPanel();

        bottomPanel.add(viewPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(dateNavPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the view selection panel with combo box.
     *
     * @return Configured view selection panel
     */
    private JPanel createViewSelectionPanel() {
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        viewPanel.setBackground(Color.WHITE);

        JLabel viewLabel = new JLabel("View:");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewCombo.setSelectedItem("Week");
        viewCombo.addActionListener(e -> switchView());

        viewPanel.add(viewLabel);
        viewPanel.add(viewCombo);

        return viewPanel;
    }

    /**
     * Creates the new event button panel.
     *
     * @return Configured button panel
     */
    private JPanel createNewEventButtonPanel() {
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> showNewBookingForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(newEventButton);

        return buttonPanel;
    }

    /**
     * Creates the date navigation panel with controls.
     *
     * @return Configured navigation panel
     */
    private JPanel createDateNavigationPanel() {
        JPanel dateNavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        dateNavPanel.setBackground(Color.WHITE);

        // Date picker
        datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setPreferredSize(new Dimension(100, 25));
        datePicker.setDate(java.sql.Date.valueOf(currentDate));
        datePicker.addPropertyChangeListener("date", e -> {
            java.util.Date selectedDate = datePicker.getDate();
            if (selectedDate != null) {
                currentDate = selectedDate.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                switchToView(currentView, currentDate);
            }
        });

        // Navigation buttons
        JButton prevButton = createNavigationButton("<", e -> navigate(-1));
        JButton todayButton = createNavigationButton("Today", e -> navigateToToday());
        JButton nextButton = createNavigationButton(">", e -> navigate(1));

        // Style buttons
        mainMenu.stylizeButton(prevButton);
        mainMenu.stylizeButton(todayButton);
        mainMenu.stylizeButton(nextButton);

        dateNavPanel.add(prevButton);
        dateNavPanel.add(todayButton);
        dateNavPanel.add(nextButton);
        dateNavPanel.add(datePicker);

        return dateNavPanel;
    }

    /**
     * Creates a styled navigation button.
     *
     * @param text The button text
     * @param listener The action listener
     * @return Configured navigation button
     */
    private JButton createNavigationButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(
                text.equals("Today") ? 80 : 50,
                text.equals("Today") ? 25 : 30
        ));
        button.addActionListener(listener);
        return button;
    }

    /**
     * Handles view switching based on combo box selection.
     */
    private void switchView() {
        String selected = (String) viewCombo.getSelectedItem();
        if (selected != null) {
            CalendarView view = CalendarView.valueOf(selected.toUpperCase());
            switchToView(view);
        }
    }

    /**
     * Navigates the calendar view by the specified direction.
     *
     * @param direction The number of periods to navigate (-1 for previous, 1 for next)
     */
    private void navigate(int direction) {
        switch (currentView) {
            case DAY:
                currentDate = currentDate.plusDays(direction);
                break;
            case WEEK:
                currentDate = currentDate.plusWeeks(direction);
                break;
            case MONTH:
                currentDate = currentDate.plusMonths(direction);
                break;
        }
        switchToView(currentView, currentDate);
    }

    /**
     * Navigates the calendar view to today's date.
     */
    private void navigateToToday() {
        currentDate = LocalDate.now();
        switchToView(currentView, currentDate);
    }

    /**
     * Updates the header text based on the current view.
     */
    private void updateHeaderText() {
        String headerText = "";
        if (currentViewPanel != null) {
            headerText = switch (currentView) {
                case WEEK -> formatWeekHeader(
                        currentViewPanel.getViewStartDate(),
                        currentViewPanel.getViewEndDate()
                );
                case MONTH -> currentViewPanel.getViewStartDate()
                        .format(DateTimeFormatter.ofPattern("MMMM yyyy"));
                default -> headerText;
            };
        }
        viewRangeLabel.setText(headerText);
    }

    /**
     * Formats a day header with proper day name and suffix.
     *
     * @param date The date to format
     * @return Formatted day string
     */
    private String formatDayHeader(LocalDate date) {
        String dayName = date.getDayOfWeek().toString();
        dayName = dayName.substring(0, 1) + dayName.substring(1).toLowerCase();

        int day = date.getDayOfMonth();
        String suffix = getDaySuffix(day);

        String month = date.getMonth().toString();
        month = month.substring(0, 1) + month.substring(1).toLowerCase();

        return String.format("%s %d%s %s %d",
                dayName, day, suffix, month, date.getYear());
    }

    /**
     * Formats a week range header.
     *
     * @param start The start date of the week
     * @param end The end date of the week
     * @return Formatted week range string
     */
    private String formatWeekHeader(LocalDate start, LocalDate end) {
        if (start.getMonth() == end.getMonth()) {
            return String.format("%s %d - %d %s %d",
                    start.getDayOfWeek().toString().charAt(0) +
                            start.getDayOfWeek().toString().substring(1).toLowerCase(),
                    start.getDayOfMonth(),
                    end.getDayOfMonth(),
                    start.getMonth().toString().charAt(0) +
                            start.getMonth().toString().substring(1).toLowerCase(),
                    start.getYear());
        }
        return formatDayHeader(start) + " to " + formatDayHeader(end);
    }

    /**
     * Gets the ordinal suffix for a day number.
     *
     * @param day The day of month
     * @return The appropriate suffix ("st", "nd", "rd", or "th")
     */
    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    /**
     * Shows the new booking form dialog.
     */
    private void showNewBookingForm() {
        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
        NewBookingForm newBookingDialog = new NewBookingForm(ownerFrame, mainMenu.getSqlConnection());
        newBookingDialog.setVisible(true);
    }
}