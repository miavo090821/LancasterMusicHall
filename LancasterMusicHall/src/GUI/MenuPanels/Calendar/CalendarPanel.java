package GUI.MenuPanels.Calendar;

import GUI.MainMenuGUI;
import GUI.MenuPanels.Booking.NewBookingForm;
import com.toedter.calendar.JDateChooser;
import operations.entities.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarPanel extends JPanel {
    private enum CalendarView {WEEK, DAY, MONTH}
    private LocalDate currentDate = LocalDate.now();
    private CalendarView currentView = CalendarView.WEEK;
    private CalendarViewPanel currentViewPanel;
    private List<Event> events = new ArrayList<>();
    private MainMenuGUI mainMenu;

    // UI Components
    private JComboBox<String> viewCombo;
    private JLabel viewRangeLabel;
    private JDateChooser datePicker;

    public CalendarPanel(MainMenuGUI mainMenu) {
        this.mainMenu = mainMenu;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize UI components
        initializeUI();

        // Create initial view
        switchToView(CalendarView.WEEK);
        setupBottomPanel();
    }

    private void initializeUI() {
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        viewRangeLabel = new JLabel("", SwingConstants.CENTER);
        viewRangeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(viewRangeLabel);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void switchToView(CalendarView view) {
        switchToView(view, currentDate);
    }

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

        // Update date picker if it exists
        if (datePicker != null) {
            datePicker.setDate(java.sql.Date.valueOf(currentDate));
        }
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // View selection panel
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        viewPanel.setBackground(Color.WHITE);

        JLabel viewLabel = new JLabel("View:");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewCombo.setSelectedItem("Week");
        viewCombo.addActionListener(e -> switchView());

        viewPanel.add(viewLabel);
        viewPanel.add(viewCombo);

        // New Event button
        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> showNewBookingForm());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(newEventButton);

        // Date navigation panel
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
        JButton prevButton = new JButton("<");
        prevButton.setFont(new Font("Arial", Font.BOLD, 16));
        prevButton.setPreferredSize(new Dimension(50, 30));
        prevButton.addActionListener(e -> navigate(-1));

        JButton todayButton = new JButton("Today");
        todayButton.addActionListener(e -> navigateToToday());

        JButton nextButton = new JButton(">");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setPreferredSize(new Dimension(50, 30));
        nextButton.addActionListener(e -> navigate(1));

        // Style buttons
        mainMenu.stylizeButton(prevButton);
        mainMenu.stylizeButton(nextButton);
        mainMenu.stylizeButton(todayButton);

        // Add components to navigation panel
        dateNavPanel.add(prevButton);
        dateNavPanel.add(todayButton);
        dateNavPanel.add(nextButton);
        dateNavPanel.add(datePicker);

        // Add components to bottom panel
        bottomPanel.add(viewPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(dateNavPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void switchView() {
        String selected = (String) viewCombo.getSelectedItem();
        if (selected != null) {
            CalendarView view = CalendarView.valueOf(selected.toUpperCase());
            switchToView(view);
        }
    }

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

    private void navigateToToday() {
        currentDate = LocalDate.now();
        switchToView(currentView, currentDate);
    }

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

    private String getDaySuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    private void showNewBookingForm() {
        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
        NewBookingForm newBookingDialog = new NewBookingForm(ownerFrame, mainMenu.getSqlConnection());
        newBookingDialog.setVisible(true);
    }
}