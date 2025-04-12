package GUI.MenuPanels.Diary;

import GUI.MainMenuGUI;
import GUI.MenuPanels.Calendar.*;
import GUI.MenuPanels.Event.EventDetailForm;
import GUI.MenuPanels.Event.NewEventForm;
import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DiaryPanel is a user interface panel for a diary application that supports multiple calendar views:
 * day, week, and month. It allows the user to navigate between dates, switch between views, and create new
 * event drafts.
 * <p>
 * The panel communicates with the main menu and an SQL database to retrieve and display event information.
 * </p>
 */
public class DiaryPanel extends JPanel {
    /**
     * Enumeration representing the available diary views.
     */
    private enum DiaryView {DAY, WEEK, MONTH}

    /** The currently selected diary view. */
    private DiaryView currentView = DiaryView.DAY;
    /** The currently active CalendarViewPanel displayed in the diary. */
    private CalendarViewPanel currentViewPanel;
    /** The SQL connection used to interact with the database. */
    private SQLConnection sqlCon;
    /** Reference to the main menu GUI. */
    private MainMenuGUI mainMenu;
    /** The current date used as the basis for the view. */
    private LocalDate currentDate = LocalDate.now();
    /**
     * A list of events to display.
     * <p>
     * Initially, this list is empty.
     * </p>
     */
    private List<operations.entities.Event> events = new ArrayList<>();

    // UI Components
    /** Combo box to select the diary view (Day, Week, Month). */
    private JComboBox<String> viewCombo;
    /** Label to display the current view range (e.g. date range for week view). */
    private JLabel viewRangeLabel = new JLabel();
    /** Formatter for displaying dates in the format "dd/MM/yyyy". */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    /** Formatter for displaying month and year in the format "MMMM yyyy". */
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    /**
     * Constructs a DiaryPanel with the specified main menu and SQL connection.
     *
     * @param mainMenu the MainMenuGUI instance associated with the diary.
     * @param sqlCon   the SQLConnection used for database operations.
     */
    public DiaryPanel(MainMenuGUI mainMenu, SQLConnection sqlCon) {
        this.mainMenu = mainMenu;
        this.sqlCon = sqlCon;
        setLayout(new BorderLayout());
        switchToView(DiaryView.DAY);
        setupBottomPanel();
    }

    /**
     * Switches the diary view to the specified view type.
     *
     * @param view the DiaryView to switch to.
     */
    private void switchToView(DiaryView view) {
        currentView = view;

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case DAY:
                currentViewPanel = new DiaryDayViewPanel(currentDate, events, sqlCon);
                break;
            case WEEK:
                currentViewPanel = new DiaryWeekViewPanel(currentDate, events, sqlCon);
                break;
            case MONTH:
                currentViewPanel = new DiaryMonthViewPanel(LocalDate.now(), events, sqlCon, new MonthViewListener() {
                    @Override
                    public void onDayCellClicked(LocalDate date) {
                        switchToView(DiaryView.WEEK, date);
                    }
                });
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Sets up the bottom panel which includes view controls, a "New Draft" button,
     * and navigation buttons.
     */
    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(Box.createHorizontalGlue());

        // View controls panel.
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        viewPanel.setBackground(Color.WHITE);
        viewPanel.add(new JLabel("View:"));
        viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewCombo.setSelectedItem("Day");
        viewCombo.setPreferredSize(new Dimension(100, 25));
        viewCombo.addActionListener(e -> {
            String selected = (String) viewCombo.getSelectedItem();
            switch (selected) {
                case "Day": switchToView(DiaryView.DAY); break;
                case "Week": switchToView(DiaryView.WEEK); break;
                case "Month": switchToView(DiaryView.MONTH); break;
            }
        });
        viewPanel.add(viewCombo);
        bottomPanel.add(viewPanel);
        bottomPanel.add(Box.createHorizontalGlue());

        // New Draft button.
        JButton newEventButton = new JButton("New Draft");
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 30));
        newEventButton.addActionListener(e -> {
            NewEventForm newEventForm = new NewEventForm(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    sqlCon
            );
            newEventForm.setVisible(true);
            newEventForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    currentViewPanel.refreshView();
                }
            });
        });
        bottomPanel.add(newEventButton);
        bottomPanel.add(Box.createHorizontalGlue());

        // Navigation panel.
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        navPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigate(-1));

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigate(1));

        mainMenu.stylizeButton(prevButton);
        mainMenu.stylizeButton(nextButton);

        navPanel.add(prevButton);
        navPanel.add(nextButton);
        bottomPanel.add(navPanel);
        bottomPanel.add(Box.createHorizontalGlue());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Navigates the current view by moving forward or backward.
     *
     * @param direction an integer representing the navigation direction; negative values move backward,
     *                  positive values move forward.
     */
    private void navigate(int direction) {
        currentViewPanel.navigate(direction);
        updateView();
    }

    /**
     * Switches the diary view to the specified view type with a specific date.
     *
     * @param view the DiaryView to switch to.
     * @param date the LocalDate that becomes the new current date.
     */
    private void switchToView(DiaryView view, LocalDate date) {
        currentView = view;
        currentDate = date; // Update the current date.

        if (currentViewPanel != null) {
            remove(currentViewPanel);
        }

        switch (view) {
            case DAY:
                currentViewPanel = new DiaryDayViewPanel(date, events, sqlCon);
                viewCombo.setSelectedItem("Day");
                break;
            case WEEK:
                currentViewPanel = new DiaryWeekViewPanel(date, events, sqlCon);
                viewCombo.setSelectedItem("Week");
                break;
            case MONTH:
                currentViewPanel = new DiaryMonthViewPanel(date, events, sqlCon, new MonthViewListener() {
                    @Override
                    public void onDayCellClicked(LocalDate clickedDate) {
                        switchToView(DiaryView.WEEK, clickedDate);
                    }
                });
                viewCombo.setSelectedItem("Month");
                break;
        }

        add(currentViewPanel, BorderLayout.CENTER);
        updateView();
    }

    /**
     * Updates the diary view by refreshing the current view panel and updating the header text.
     */
    private void updateView() {
        currentViewPanel.refreshView();
        updateHeaderText();
    }

    /**
     * Updates the header text of the diary view to reflect the current date range or date.
     * <p>
     * For week views, the header displays the start and end dates; for day view, it displays a single date;
     * for month view, it displays the month and year.
     * </p>
     */
    private void updateHeaderText() {
        String headerText = switch (currentView) {
            case WEEK -> currentViewPanel.getViewStartDate().format(dateFormatter) + " to " +
                    currentViewPanel.getViewEndDate().format(dateFormatter);
            case DAY -> currentViewPanel.getViewStartDate().format(dateFormatter);
            case MONTH -> currentViewPanel.getViewStartDate().format(monthYearFormatter);
        };
        viewRangeLabel.setText(headerText);
    }
}
