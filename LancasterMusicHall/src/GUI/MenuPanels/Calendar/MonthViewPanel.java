package GUI.MenuPanels.Calendar;

import Database.SQLConnection;
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
 * MonthViewPanel is a concrete implementation of CalendarViewPanel that displays a month view calendar.
 * <p>
 * It shows the entire month in a grid layout with day headers, and displays events for each day by querying
 * a database for event details. It also notifies a listener when a day cell is clicked.
 * </p>
 */
public class MonthViewPanel extends CalendarViewPanel {
    /** SQL connection used to access the database. */
    private SQLConnection sqlCon;
    /** 2D array of DayCellPanel representing the day cells of the month view. */
    private DayCellPanel[][] dayCells;
    /** The start date of the view. */
    private LocalDate viewStartDate;
    /** The end date of the view. */
    private LocalDate viewEndDate;
    /** Formatter for displaying the month and year in the header. */
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    /** The list of events to be displayed in the month view. */
    private List<Event> events;
    /** The listener to notify when a day cell is clicked. */
    private MonthViewListener listener;

    /**
     * Constructs a MonthViewPanel with the specified date, events, database connection, and listener.
     *
     * @param startDate the starting date for the view; the panel sets the view to the first day of the month.
     * @param events    the list of events to be displayed.
     * @param sqlCon    the SQLConnection used to retrieve event data.
     * @param listener  the MonthViewListener to be notified when a day cell is clicked.
     */
    public MonthViewPanel(LocalDate startDate, List<Event> events, SQLConnection sqlCon, MonthViewListener listener) {
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
     * Returns the SQL connection used by this panel.
     *
     * @return the SQLConnection instance.
     */
    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    /**
     * Sets the view date to the specified date by adjusting to the first day of that month.
     *
     * @param date the LocalDate to set as the view's base date.
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    /**
     * Initializes the user interface components of the month view panel.
     * <p>
     * The layout consists of a header with the month name and a note, and a calendar grid
     * showing day cells and day headers in a scrollable pane.
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
     * Initializes the grid for the month view.
     * <p>
     * This method creates day headers for the week and then populates a 6x7 grid of day cells.
     * Empty cells are added where necessary (e.g., days before the start of the month).
     * </p>
     *
     * @param gridPanel the JPanel to populate with the grid.
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
     * Navigates the calendar view by a specified number of months.
     *
     * @param direction an integer representing the direction to navigate; positive to move forward and negative to move backward.
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
     * This method clears current events from all day cells, then queries the database for events occurring
     * between the view's start and end dates. It then adds event information to the corresponding day cell.
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

        // Query now joins the Booking table and filters for confirmed bookings.
        String query = "SELECT e.event_id, e.booking_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date BETWEEN ? AND ? " +
                "AND b.booking_status = 'confirmed'";

        try {
            PreparedStatement ps = getSQLConnection().getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ps.setDate(2, java.sql.Date.valueOf(viewEndDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String eventName = rs.getString("name");
                int bookingId = rs.getInt("booking_id");
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                int dayOfMonth = eventDate.getDayOfMonth();
                LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
                int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;
                int cellIndex = startDayOfWeek + dayOfMonth - 1;
                int row = cellIndex / 7;
                int col = cellIndex % 7;
                if (dayCells[row][col] != null) {
                    dayCells[row][col].addEvent(String.format("%s (Booking: %d)", eventName, bookingId));
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

    /**
     * Called when a day cell is clicked.
     * <p>
     * This method notifies the MonthViewListener if one is registered.
     * </p>
     *
     * @param date the LocalDate of the clicked day cell.
     */
    private void onDayCellClicked(LocalDate date) {
        if (listener != null) {
            listener.onDayCellClicked(date);
        }
    }

    // ---------------------------------------------------------
    // Inner class: DayCellPanel
    // ---------------------------------------------------------
    /**
     * DayCellPanel represents an individual day cell in the month view calendar.
     * <p>
     * It displays the day number and a scrollable area to show events for that day.
     * Clicking anywhere in the cell notifies the parent MonthViewPanel that the cell was clicked.
     * </p>
     */
    public class DayCellPanel extends JPanel {
        /** Label displaying the day number. */
        private JLabel dayLabel;
        /** Panel to display event information for the day. */
        private JPanel eventsPanel;
        /** The day number of this cell. */
        private int dayNumber;
        /** The date represented by this cell. */
        private LocalDate date;

        /**
         * Constructs a DayCellPanel with the specified day number and date.
         *
         * @param dayNumber the day number to be displayed.
         * @param date      the LocalDate represented by this cell.
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

            // Add recursive mouse listener so that clicks anywhere trigger navigation.
            addMouseListenerRecursively(this, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    MonthViewPanel.this.onDayCellClicked(getDate());
                }
            });
        }

        /**
         * Gets the date represented by this cell.
         *
         * @return the LocalDate for this cell.
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Adds an event description to the cell.
         *
         * @param eventText the String describing the event.
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
         * Clears all events from the cell.
         */
        public void clearEvents() {
            eventsPanel.removeAll();
        }

        /**
         * Refreshes the cell UI by revalidating and repainting the component.
         */
        public void refresh() {
            revalidate();
            repaint();
        }

        /**
         * Adds a mouse listener to a component and all its child components recursively.
         *
         * @param comp     the component to which the listener is added.
         * @param listener the MouseListener to attach.
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
