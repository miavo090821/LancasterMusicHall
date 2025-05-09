package GUI.MenuPanels.Diary;

import Database.SQLConnection;
import GUI.MenuPanels.Calendar.CalendarViewPanel;
import GUI.MenuPanels.Calendar.MonthViewListener;
import operations.entities.Event;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * DiaryMonthViewPanel is a concrete implementation of CalendarViewPanel that displays a month view
 * tailored for a diary. It shows the monthly calendar grid and renders events for each day.
 * <p>
 * The panel retrieves events from a database using an SQL connection and notifies a
 * MonthViewListener when a day cell is clicked.
 * </p>
 */
public class DiaryMonthViewPanel extends CalendarViewPanel {
    /** The SQL connection used to retrieve event data. */
    private SQLConnection sqlCon;
    /** 2D array of DayCellPanel representing the day cells of the month view. */
    private DayCellPanel[][] dayCells;
    /** The start date of the view. */
    private LocalDate viewStartDate;
    /** The end date of the view. */
    private LocalDate viewEndDate;
    /** Formatter for displaying the month and year in the header. */
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    /** The list of events to be displayed. */
    private List<Event> events;
    /** The listener to notify when a day cell is clicked. */
    private MonthViewListener listener;

    /**
     * Constructs a DiaryMonthViewPanel with the specified starting date, events, SQL connection, and listener.
     *
     * @param startDate the starting date for the view (the view is set to the first day of the month).
     * @param events    the list of events to be displayed.
     * @param sqlCon    the SQLConnection used to access the database.
     * @param listener  the MonthViewListener that will be notified when a day cell is clicked.
     */
    public DiaryMonthViewPanel(LocalDate startDate, List<Event> events, SQLConnection sqlCon, MonthViewListener listener) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.events = events;
        this.listener = listener;
        // Set view to the first day of the month.
        this.viewStartDate = startDate.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        initializeUI();
        renderEvents(null);
    }

    /**
     * Returns the SQLConnection used by this panel.
     *
     * @return the SQLConnection.
     */
    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    /**
     * Sets the view date for the DiaryMonthViewPanel.
     * <p>
     * The view date is adjusted to the first day of the month, and the view end date is calculated
     * to be the last day of the month.
     * </p>
     *
     * @param date the new view date.
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    /**
     * Initializes the user interface components for the month view.
     * <p>
     * The UI consists of a header panel displaying the month name and a note, and a calendar grid
     * placed inside a scroll pane.
     * </p>
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header panel displays the month name and a note.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Month header label.
        JLabel monthHeader = new JLabel(viewStartDate.format(monthYearFormatter), SwingConstants.CENTER);
        monthHeader.setFont(new Font("Arial", Font.BOLD, 16));
        // Store the header label for later updates.
        this.putClientProperty("monthHeader", monthHeader);
        headerPanel.add(monthHeader, BorderLayout.NORTH);

        // Note label with instructions.
        JLabel noteLabel = new JLabel("Select any date to view that week's schedule", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(Color.DARK_GRAY);
        headerPanel.add(noteLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Calendar grid in a scroll pane.
        JPanel gridPanel = new JPanel(new GridLayout(0, 7));
        gridPanel.setBackground(Color.WHITE);
        initializeMonthGrid(gridPanel);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    /**
     * Initializes the calendar grid for the month view.
     * <p>
     * The grid includes day headers for Monday to Sunday and day cells representing each day of the month.
     * Empty cells are added for days before and after the current month.
     * </p>
     *
     * @param gridPanel the panel to be populated with the calendar grid.
     */
    private void initializeMonthGrid(JPanel gridPanel) {
        gridPanel.removeAll();

        // Day headers (week starts on Monday).
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            gridPanel.add(dayLabel);
        }

        YearMonth yearMonth = YearMonth.from(viewStartDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1; // Monday = 0

        dayCells = new DayCellPanel[6][7];
        int totalCells = 42;
        int dayCounter = 1;

        for (int i = 0; i < totalCells; i++) {
            if (i < dayOfWeek || dayCounter > daysInMonth) {
                JPanel empty = new JPanel();
                empty.setBackground(Color.WHITE);
                gridPanel.add(empty);
            } else {
                LocalDate cellDate = viewStartDate.withDayOfMonth(dayCounter);
                DayCellPanel cell = new DayCellPanel(dayCounter, cellDate);
                // When the cell is clicked, notify the listener.
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onDayCellClicked(cell.getDate());
                    }
                });
                dayCells[i / 7][i % 7] = cell;
                gridPanel.add(cell);
                dayCounter++;
            }
        }
    }

    /**
     * Navigates the month view by a specified number of months.
     *
     * @param direction an integer representing the navigation direction; positive to move forward,
     *                  negative to move backward.
     */
    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusMonths(direction).withDayOfMonth(1);
        viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    /**
     * Renders events on the month view.
     * <p>
     * The method clears existing events from each day cell, queries the database for events within the
     * current month's start and end dates, and adds event information to the corresponding day cells.
     * </p>
     *
     * @param ignored a placeholder parameter that is not used in this implementation.
     */
    @Override
    public void renderEvents(List<Event> ignored) {
        if (dayCells == null) return;
        for (int row = 0; row < dayCells.length; row++) {
            for (int col = 0; col < dayCells[row].length; col++) {
                if (dayCells[row][col] != null) {
                    dayCells[row][col].clearEvents();
                }
            }
        }

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name, b.booking_id " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date BETWEEN ? AND ? " +
                "AND b.booking_status = 'held' " +
                "ORDER BY e.start_date, e.start_time";

        try {
            PreparedStatement ps = getSQLConnection().getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ps.setDate(2, java.sql.Date.valueOf(viewEndDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String eventName = rs.getString("name");
                String venueName = rs.getString("venue_name");
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                int dayOfMonth = eventDate.getDayOfMonth();
                LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
                int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;
                int cellIndex = startDayOfWeek + dayOfMonth - 1;
                int row = cellIndex / 7;
                int col = cellIndex % 7;
                if (dayCells[row][col] != null) {
                    dayCells[row][col].addEvent(String.format("%s (%s)", eventName, venueName));
                    dayCells[row][col].refresh();
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        revalidate();
        repaint();
    }

    /**
     * Refreshes the month view by updating the header and reinitializing the calendar grid.
     */
    @Override
    public void refreshView() {
        JLabel monthHeader = (JLabel) this.getClientProperty("monthHeader");
        if (monthHeader != null) {
            monthHeader.setText(viewStartDate.format(monthYearFormatter));
        }
        JPanel gridPanel = (JPanel) ((JScrollPane) getComponent(1)).getViewport().getView();
        initializeMonthGrid(gridPanel);
        renderEvents(null);
        revalidate();
        repaint();
    }

    // ---------------------------------------------------------
    // Inner class: DayCellPanel
    // ---------------------------------------------------------
    /**
     * Called when a day cell is clicked. This method notifies the MonthViewListener.
     *
     * @param date the LocalDate corresponding to the clicked day cell.
     */
    private void onDayCellClicked(LocalDate date) {
        if (listener != null) {
            listener.onDayCellClicked(date);
        }
    }

    /**
     * DayCellPanel represents a single day cell in the diary month view.
     * <p>
     * It displays the day number and a list of events associated with that day.
     * Clicking on the cell will trigger a notification to the MonthViewListener.
     * </p>
     */
    public class DayCellPanel extends JPanel {
        /** The label displaying the day number. */
        private JLabel dayLabel;
        /** Panel to hold event labels for this day. */
        private JPanel eventsPanel;
        /** The day number to display. */
        private int dayNumber;
        /** The LocalDate represented by this cell. */
        private LocalDate date;

        /**
         * Constructs a DayCellPanel with the given day number and date.
         *
         * @param dayNumber the numerical day of the month.
         * @param date      the LocalDate for this cell.
         */
        public DayCellPanel(int dayNumber, LocalDate date) {
            this.dayNumber = dayNumber;
            this.date = date;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            setBackground(Color.WHITE);

            dayLabel = new JLabel(String.valueOf(dayNumber));
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(dayLabel, BorderLayout.NORTH);

            eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(Color.WHITE);
            JScrollPane eventsScroll = new JScrollPane(eventsPanel);
            eventsScroll.setBorder(null);
            add(eventsScroll, BorderLayout.CENTER);

            // Add recursive mouse listener so that clicks anywhere trigger notification.
            addMouseListenerRecursively(this, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DiaryMonthViewPanel.this.onDayCellClicked(getDate());
                }
            });
        }

        /**
         * Returns the LocalDate represented by this day cell.
         *
         * @return the LocalDate for this cell.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Adds an event description to the day cell.
         *
         * @param eventText the text describing the event.
         */
        public void addEvent(String eventText) {
            JLabel eventLabel = new JLabel(eventText);
            eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            eventLabel.setOpaque(true);
            eventLabel.setBackground(new Color(230, 255, 200));
            eventLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            eventsPanel.add(eventLabel);
        }

        /**
         * Clears all event descriptions from the day cell.
         */
        public void clearEvents() {
            eventsPanel.removeAll();
        }

        /**
         * Refreshes the display of the day cell.
         */
        public void refresh() {
            revalidate();
            repaint();
        }

        /**
         * Recursively adds the specified MouseListener to the given component and all its child components.
         *
         * @param comp     the component to attach the listener to.
         * @param listener the MouseListener to add.
         */
        private void addMouseListenerRecursively(Component comp, java.awt.event.MouseListener listener) {
            comp.addMouseListener(listener);
            if (comp instanceof Container) {
                for (Component child : ((Container) comp).getComponents()) {
                    addMouseListenerRecursively(child, listener);
                }
            }
        }
    }
}
