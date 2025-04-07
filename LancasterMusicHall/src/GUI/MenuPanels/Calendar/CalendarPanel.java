package GUI.MenuPanels.Calendar;

import GUI.MainMenuGUI;
import GUI.MenuPanels.Booking.NewBookingForm;
import com.toedter.calendar.JDateChooser;
import operations.entities.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CalendarPanel extends JPanel {
    private enum CalendarView {WEEK, DAY, MONTH}
    private LocalDate currentDate = LocalDate.now();

    private CalendarView currentView = CalendarView.WEEK;
    private CalendarViewPanel currentViewPanel;
    private List<Event> events = new ArrayList<>(); // Start empty

    // UI Components
    private JComboBox<String> viewCombo;

    // Date formatting
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    private JLabel viewRangeLabel = new JLabel();

    // Reference to MainMenuGUI (to get SQLConnection)
    private MainMenuGUI mainMenu;

    public CalendarPanel(MainMenuGUI mainMenu) {
        this.mainMenu = mainMenu;
        setLayout(new BorderLayout());

        // Create initial view (Week view by default)
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
                currentViewPanel = new WeekViewPanel(LocalDate.now(), events, mainMenu.getSqlConnection());
                break;
            case DAY:
                currentViewPanel = new DayViewPanel(LocalDate.now(), events, mainMenu.getSqlConnection());
                break;
            case MONTH:
                currentViewPanel = new MonthViewPanel(LocalDate.now(), events, mainMenu.getSqlConnection(), new MonthViewListener() {
                    @Override
                    public void onDayCellClicked(LocalDate date) {
                        switchToView(CalendarView.WEEK, date);
                    }
                });
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
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
        viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewCombo.setSelectedItem("Week");
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
        newEventButton.addActionListener(e -> showNewBookingForm());
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
        datePicker.setDate(new java.util.Date());

        datePicker.addPropertyChangeListener("date", e -> {
            java.util.Date selectedDate = datePicker.getDate();
            if (selectedDate != null) {
                currentDate = LocalDate.of(
                        selectedDate.getYear() + 1900,
                        selectedDate.getMonth() + 1,
                        selectedDate.getDate()
                );
                currentViewPanel.setViewDate(currentDate);
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
        if (selected != null) {
            CalendarView view = CalendarView.valueOf(selected.toUpperCase());
            switchToView(view);
        }
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

    private void showNewBookingForm() {
        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
        NewBookingForm newBookingDialog = new NewBookingForm(ownerFrame, mainMenu.getSqlConnection());
        newBookingDialog.setVisible(true);
    }

    // Overload switchToView to accept a date for Week view:
    private void switchToView(CalendarView view, LocalDate date) {
        currentView = view;
        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }
        if (view == CalendarView.WEEK) {
            currentViewPanel = new WeekViewPanel(date, events, mainMenu.getSqlConnection());
        }
        // Handle other views if needed.
        add(currentViewPanel, BorderLayout.CENTER);
        updateHeaderText();
        revalidate();
        repaint();
    }
}
