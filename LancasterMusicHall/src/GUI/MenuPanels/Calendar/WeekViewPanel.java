package GUI.MenuPanels.Calendar;

import Database.SQLConnection;
import GUI.MenuPanels.Event.EventDetailForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * WeekViewPanel is a concrete implementation of CalendarViewPanel that provides a week view calendar.
 * <p>
 * It displays events for a week with days as columns and time slots as rows. The panel
 * retrieves events from a database, organizes them in time slots, and displays details on mouse events.
 * </p>
 */
public class WeekViewPanel extends CalendarViewPanel {
    /** The currently selected view date. */
    private LocalDate viewDate;
    /** The start date of the week (Monday) currently in view. */
    private LocalDate startOfWeek;
    /** The starting hour (inclusive) for the time grid. */
    private final int startHour = 10;
    /** The ending hour (inclusive) for the time grid. */
    private final int endHour = 24;
    /** Total number of slots in the time grid. */
    private final int numberOfSlots = endHour - startHour + 1;
    /** SQL connection used for database operations. */
    private SQLConnection sqlCon;
    /** Map to track event colors based on event IDs. */
    private Map<Integer, Color> eventColors = new HashMap<>();
    /** Map to store display information for events by their event IDs. */
    private Map<Integer, EventDisplayInfo> eventDisplayMap = new HashMap<>();

    // Header for days (Monday to Sunday)
    /** Array of labels representing the days of the week header. */
    private JLabel[] dayLabels = new JLabel[7];
    /** The panel containing day header labels. */
    private JPanel daysHeaderPanel;

    // Time grid components
    /** Main panel for the grid layout. */
    private JPanel gridPanel;
    /** Panel displaying time labels (e.g., "10:00", "11:00", etc.). */
    private JPanel timeLabelPanel;
    /** Panel containing the event grid for each day. */
    private JPanel daysGridPanel;
    /**
     * 2D array of panels for time slots where events are rendered.
     * Rows represent time slots, and columns represent days.
     */
    private JPanel[][] timeSlotPanels;

    /**
     * Constructs a WeekViewPanel for a given date, events, and SQL connection.
     *
     * @param date         the view date.
     * @param events       a list of events.
     * @param sqlConnection the SQL connection object.
     */
    public WeekViewPanel(LocalDate date, java.util.List events, Object sqlConnection) {
        super(date, events, sqlConnection);
        this.sqlCon = (SQLConnection) sqlConnection;
        this.viewDate = date;
        computeStartOfWeek();
        initializeUI();
        renderEvents(null);
    }

    /**
     * Computes the start of the week (Monday) based on the current view date.
     */
    private void computeStartOfWeek() {
        startOfWeek = viewDate.with(DayOfWeek.MONDAY);
    }

    /**
     * Initializes the user interface of the week view panel.
     * <p>
     * The UI consists of a header with day names and a center panel with a time grid.
     * </p>
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // --- Header Panel with Day Names ---
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Create a container for the days header and an empty space (for time column)
        JPanel headerContentPanel = new JPanel(new BorderLayout());

        // Main dates panel (Monday to Sunday)
        daysHeaderPanel = new JPanel(new GridLayout(1, 7));
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("EEEE dd/MM");
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayLabels[i] = new JLabel(day.format(headerFormatter), SwingConstants.CENTER);
            dayLabels[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            daysHeaderPanel.add(dayLabels[i]);
        }

        // Add a small empty panel as left column (to match time column)
        JPanel emptySpacePanel = new JPanel();
        emptySpacePanel.setPreferredSize(new Dimension(50, 10)); // Width matches time column
        emptySpacePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Add components to header content panel
        headerContentPanel.add(emptySpacePanel, BorderLayout.WEST);
        headerContentPanel.add(daysHeaderPanel, BorderLayout.CENTER);

        headerPanel.add(headerContentPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Center Panel with Time Grid ---
        gridPanel = new JPanel(new BorderLayout());
        gridPanel.setPreferredSize(new Dimension(750, 800));

        // Left column: Time labels (from startHour to endHour)
        timeLabelPanel = new JPanel(new GridLayout(numberOfSlots, 1));
        timeLabelPanel.setPreferredSize(new Dimension(50, timeLabelPanel.getHeight())); // Fixed width

        for (int hour = startHour; hour <= endHour; hour++) {
            JLabel timeLabel = new JLabel(String.format("%02d:00", hour), SwingConstants.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            timeLabelPanel.add(timeLabel);
        }
        gridPanel.add(timeLabelPanel, BorderLayout.WEST);

        // Right side: Days grid for events
        daysGridPanel = new JPanel(new GridLayout(numberOfSlots, 7));
        timeSlotPanels = new JPanel[numberOfSlots][7];

        // Calculate equal width for each day column given total grid width minus time column width
        int dayWidth = (750 - 50) / 7;

        for (int row = 0; row < numberOfSlots; row++) {
            for (int col = 0; col < 7; col++) {
                JPanel timeSlotPanel = new JPanel(new BorderLayout());
                timeSlotPanel.setPreferredSize(new Dimension(dayWidth, timeSlotPanel.getHeight()));
                timeSlotPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                timeSlotPanel.setBackground(Color.WHITE);
                timeSlotPanels[row][col] = timeSlotPanel;
                daysGridPanel.add(timeSlotPanel);
            }
        }

        // Ensure the days header cells match the grid columns
        for (int i = 0; i < 7; i++) {
            dayLabels[i].setPreferredSize(new Dimension(dayWidth, dayLabels[i].getHeight()));
        }

        gridPanel.add(daysGridPanel, BorderLayout.CENTER);

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
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
     * Sets a new view date and refreshes the week view.
     *
     * @param date the new view date.
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewDate = date;
        computeStartOfWeek();
        refreshView();
    }

    /**
     * Refreshes the week view display.
     * <p>
     * It updates the header labels, clears the time slot panels, and re-renders the events.
     * </p>
     */
    @Override
    public void refreshView() {
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("EEEE dd/MM");
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayLabels[i].setText(day.format(headerFormatter));
        }
        clearTimeSlots();
        renderEvents(null);
        revalidate();
        repaint();
    }

    /**
     * Clears all events and resets the time slot panels to their initial state.
     */
    private void clearTimeSlots() {
        eventDisplayMap.clear();
        for (int row = 0; row < numberOfSlots; row++) {
            for (int col = 0; col < 7; col++) {
                timeSlotPanels[row][col].removeAll();
                timeSlotPanels[row][col].setLayout(new BorderLayout());
                timeSlotPanels[row][col].setBackground(Color.WHITE);
            }
        }
    }

    /**
     * Navigates to a different week.
     *
     * @param direction an integer representing the number of weeks to move forward (positive)
     *                  or backward (negative).
     */
    @Override
    public void navigate(int direction) {
        viewDate = viewDate.plusWeeks(direction);
        computeStartOfWeek();
        refreshView();
    }

    /**
     * Renders events onto the week view by querying the database and mapping events to time slots.
     * <p>
     * It organizes events by day, calculates concurrent events to assign columns,
     * and then adds event panels to the appropriate time slot panels.
     * </p>
     *
     * @param ignored a placeholder parameter that is not used in this implementation.
     */
    @Override
    public void renderEvents(java.util.List ignored) {
        LocalDate weekStart = startOfWeek;
        LocalDate weekEnd = startOfWeek.plusDays(6);

        // Adjusted SQL query to join with Booking, filter confirmed bookings, and select booking_id.
        String query = "SELECT e.event_id, e.booking_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date BETWEEN ? AND ? AND b.booking_status = 'confirmed'";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setDate(2, java.sql.Date.valueOf(weekEnd));
            ResultSet rs = ps.executeQuery();

            // Organize events by day (0 for Monday, ..., 6 for Sunday)
            Map<Integer, List<EventInfo>> eventsByDay = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                eventsByDay.put(i, new ArrayList<>());
            }

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                int bookingId = rs.getInt("booking_id");
                String eventName = rs.getString("name");
                // Append booking id to the event name.
                eventName = eventName + " (Booking: " + bookingId + ")";
                String venueName = rs.getString("venue_name");
                String bookedBy = rs.getString("booked_by");
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                int dayIndex = (int) (eventDate.toEpochDay() - weekStart.toEpochDay());
                if (dayIndex < 0 || dayIndex > 6) continue;

                // +1 is added to account for an empty row at the top.
                int startSlot = startTime.getHour() - startHour + 1;
                int endSlot = endTime.getHour() - startHour + 1;

                if (startSlot >= 0 && endSlot < numberOfSlots) {
                    EventInfo event = new EventInfo(
                            eventId, eventName, venueName, bookedBy,
                            startTime, endTime, startSlot, endSlot, dayIndex
                    );
                    eventsByDay.get(dayIndex).add(event);
                    eventColors.put(eventId, determineEventColor(bookedBy));
                }
            }
            rs.close();
            ps.close();

            // Process events for each day.
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                List<EventInfo> dayEvents = eventsByDay.get(dayIndex);
                Collections.sort(dayEvents, Comparator.comparing(e -> e.startTime));

                // Calculate maximum concurrent events for the day.
                int maxConcurrent = calculateMaxConcurrentEvents(dayEvents);

                // Assign columns to overlapping events.
                assignEventColumns(dayEvents, maxConcurrent);

                // Store display info for each event.
                for (EventInfo event : dayEvents) {
                    eventDisplayMap.put(event.eventId, new EventDisplayInfo(event.column, maxConcurrent));
                }

                // First pass: create container panels for time slots.
                for (EventInfo event : dayEvents) {
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        JPanel timeSlotPanel = timeSlotPanels[slot][dayIndex];

                        if (timeSlotPanel.getComponentCount() == 0) {
                            // Create container panel with correct column layout.
                            JPanel containerPanel = new JPanel(new GridLayout(1, maxConcurrent, 1, 0));
                            containerPanel.setBackground(Color.WHITE);
                            containerPanel.setBorder(BorderFactory.createEmptyBorder());
                            timeSlotPanel.add(containerPanel, BorderLayout.CENTER);

                            // Add empty panels to reserve columns.
                            for (int i = 0; i < maxConcurrent; i++) {
                                JPanel emptyPanel = new JPanel();
                                emptyPanel.setBackground(Color.WHITE);
                                containerPanel.add(emptyPanel);
                            }
                        }
                    }
                }

                // Second pass: add event panels in appropriate columns.
                for (EventInfo event : dayEvents) {
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        JPanel timeSlotPanel = timeSlotPanels[slot][dayIndex];
                        JPanel containerPanel = (JPanel) timeSlotPanel.getComponent(0);

                        JPanel eventPanel = createEventPanel(event,
                                slot == event.startSlot,
                                slot == event.endSlot,
                                event.column,
                                maxConcurrent);

                        containerPanel.remove(event.column);
                        containerPanel.add(eventPanel, event.column);
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        revalidate();
        repaint();
    }

    /**
     * Determines the event background color based on the bookedBy value.
     *
     * @param bookedBy the booking source identifier.
     * @return a Color to be used as the background for the event.
     */
    private Color determineEventColor(String bookedBy) {
        if (bookedBy != null) {
            if (bookedBy.equalsIgnoreCase("operations")) {
                return new Color(200, 230, 255);  // Light blue.
            } else if (bookedBy.equalsIgnoreCase("marketing")) {
                return new Color(255, 230, 200);  // Light orange.
            }
        }
        return new Color(230, 255, 200);  // Default green.
    }

    /**
     * Creates a JPanel representing an event, for display in the week grid.
     *
     * @param event       the EventInfo object containing event details.
     * @param isFirstSlot true if this slot is the event's first slot.
     * @param isLastSlot  true if this slot is the event's last slot.
     * @param column      the column index assigned to the event.
     * @param maxColumns  the total number of columns allocated for concurrent events.
     * @return a JPanel configured to display the event.
     */
    private JPanel createEventPanel(EventInfo event, boolean isFirstSlot, boolean isLastSlot,
                                    int column, int maxColumns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(eventColors.get(event.eventId));

        // Always show a full border for each event.
        panel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        if (isFirstSlot) {
            JLabel label = new JLabel(
                    String.format("<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                            event.eventName, event.venueName,
                            event.startTime, event.endTime),
                    SwingConstants.CENTER
            );
            label.setFont(new Font("Arial", Font.PLAIN, 9));
            panel.add(label, BorderLayout.CENTER);
        }

        attachEventListeners(panel, event.eventId);
        return panel;
    }

    /**
     * Calculates the maximum number of concurrent (overlapping) events in a list of events.
     *
     * @param events the list of events.
     * @return the maximum number of events overlapping at any time slot.
     */
    private int calculateMaxConcurrentEvents(List<EventInfo> events) {
        if (events.isEmpty()) return 0;

        // Count overlapping events for each time slot.
        int[] concurrentCounts = new int[numberOfSlots];
        for (EventInfo event : events) {
            for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                concurrentCounts[slot]++;
            }
        }

        int maxConcurrent = 0;
        for (int count : concurrentCounts) {
            if (count > maxConcurrent) {
                maxConcurrent = count;
            }
        }

        return maxConcurrent;
    }

    /**
     * Assigns column indices for events so that overlapping events are placed side by side.
     *
     * @param events     a list of EventInfo objects.
     * @param maxColumns the maximum number of concurrent columns.
     */
    private void assignEventColumns(List<EventInfo> events, int maxColumns) {
        // Group events by each time slot.
        Map<Integer, List<EventInfo>> eventsBySlot = new HashMap<>();
        for (EventInfo event : events) {
            for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                eventsBySlot.computeIfAbsent(slot, k -> new ArrayList<>()).add(event);
            }
        }

        // Track available columns for each time slot.
        Map<Integer, Set<Integer>> availableColumnsBySlot = new HashMap<>();
        for (int slot = 0; slot < numberOfSlots; slot++) {
            availableColumnsBySlot.put(slot, new HashSet<>());
            for (int col = 0; col < maxColumns; col++) {
                availableColumnsBySlot.get(slot).add(col);
            }
        }

        // Assign columns to events.
        for (EventInfo event : events) {
            for (int col = 0; col < maxColumns; col++) {
                boolean columnAvailable = true;
                for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                    if (!availableColumnsBySlot.get(slot).contains(col)) {
                        columnAvailable = false;
                        break;
                    }
                }

                if (columnAvailable) {
                    event.column = col;
                    // Mark the column as occupied in all relevant slots.
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        availableColumnsBySlot.get(slot).remove(col);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Attaches mouse listeners to an event panel for interactivity.
     * <p>
     * The listeners highlight the panel on hover and open the EventDetailForm when clicked.
     * </p>
     *
     * @param panel   the event panel to attach listeners to.
     * @param eventId the unique identifier of the event.
     */
    private void attachEventListeners(JPanel panel, int eventId) {
        Color baseColor = panel.getBackground();
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(baseColor);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                EventDetailForm eventDetailForm = new EventDetailForm(
                        (Frame) SwingUtilities.getWindowAncestor(panel),
                        getSQLConnection(),
                        String.valueOf(eventId)
                );
                eventDetailForm.setVisible(true);
            }
        });
    }

    /**
     * Returns the start date of the current week in view.
     *
     * @return the LocalDate representing Monday of the current week.
     */
    @Override
    public LocalDate getViewStartDate() {
        return startOfWeek;
    }

    /**
     * Returns the end date of the current week in view.
     *
     * @return the LocalDate representing Sunday of the current week.
     */
    @Override
    public LocalDate getViewEndDate() {
        return startOfWeek.plusDays(6);
    }

    /**
     * TimePoint represents a specific point in time and whether it marks the start or end of an event.
     * <p>
     * This is used internally for calculating overlapping events.
     * </p>
     */
    private static class TimePoint implements Comparable<TimePoint> {
        /** The time value. */
        LocalTime time;
        /** True if this point represents the start of an event; false if it represents the end. */
        boolean isStart;

        /**
         * Constructs a TimePoint with the given time and flag.
         *
         * @param time    the LocalTime of the point.
         * @param isStart true if this time is the start of an event.
         */
        TimePoint(LocalTime time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        /**
         * Compares two TimePoint instances.
         *
         * @param other the other TimePoint.
         * @return an integer result of the comparison.
         */
        @Override
        public int compareTo(TimePoint other) {
            int timeCompare = this.time.compareTo(other.time);
            if (timeCompare != 0) return timeCompare;
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

    /**
     * EventInfo holds all relevant information for a single event.
     */
    private static class EventInfo {
        /** The unique event identifier. */
        int eventId;
        /** The event name. */
        String eventName;
        /** The venue name. */
        String venueName;
        /** Identifier indicating who booked the event. */
        String bookedBy;
        /** The event start time. */
        LocalTime startTime;
        /** The event end time. */
        LocalTime endTime;
        /** The starting time slot index for the event. */
        int startSlot;
        /** The ending time slot index for the event. */
        int endSlot;
        /** The day index (0 for Monday, ..., 6 for Sunday) where the event occurs. */
        int dayIndex;
        /** The assigned column index for displaying overlapping events. */
        int column;

        /**
         * Constructs an EventInfo object with the given event details.
         *
         * @param eventId   the event identifier.
         * @param eventName the event name.
         * @param venueName the venue name.
         * @param bookedBy  who booked the event.
         * @param startTime the start time.
         * @param endTime   the end time.
         * @param startSlot the starting slot index.
         * @param endSlot   the ending slot index.
         * @param dayIndex  the day index in the week.
         */
        public EventInfo(int eventId, String eventName, String venueName,
                         String bookedBy, LocalTime startTime, LocalTime endTime,
                         int startSlot, int endSlot, int dayIndex) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.venueName = venueName;
            this.bookedBy = bookedBy;
            this.startTime = startTime;
            this.endTime = endTime;
            this.startSlot = startSlot;
            this.endSlot = endSlot;
            this.dayIndex = dayIndex;
        }
    }

    /**
     * EventDisplayInfo stores display attributes for an event.
     */
    private static class EventDisplayInfo {
        /** The column index assigned to this event. */
        int column;
        /** The total number of columns allocated for concurrent events. */
        int totalColumns;

        /**
         * Constructs an EventDisplayInfo object.
         *
         * @param column       the column index of the event.
         * @param totalColumns the total number of columns.
         */
        public EventDisplayInfo(int column, int totalColumns) {
            this.column = column;
            this.totalColumns = totalColumns;
        }
    }
}
