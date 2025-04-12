package GUI.MenuPanels.Diary;

import Database.SQLConnection;
import GUI.MenuPanels.Calendar.CalendarViewPanel;
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
 * The DiaryWeekViewPanel class represents the week view panel in the diary section.
 * It extends the CalendarViewPanel and visualizes events on a weekly grid with time slots.
 */
public class DiaryWeekViewPanel extends CalendarViewPanel {
    private LocalDate viewDate;
    private LocalDate startOfWeek;
    private final int startHour = 10;
    private final int endHour = 24;  // inclusive
    private final int numberOfSlots = endHour - startHour + 1;
    private SQLConnection sqlCon;
    private Map<Integer, Color> eventColors = new HashMap<>();
    private Map<Integer, EventDisplayInfo> eventDisplayMap = new HashMap<>();

    // Header for days (Monday to Sunday)
    private JLabel[] dayLabels = new JLabel[7];
    private JPanel daysHeaderPanel;

    // Time grid components
    private JPanel gridPanel;
    private JPanel timeLabelPanel;
    private JPanel daysGridPanel;
    private JPanel[][] timeSlotPanels; // rows: time slots, cols: days

    /**
     * Constructs a new DiaryWeekViewPanel for a given date with the provided events and SQL connection.
     *
     * @param date          the reference date for the diary view
     * @param events        a list of events (currently not used in this implementation)
     * @param sqlConnection the SQLConnection object to the database
     */
    public DiaryWeekViewPanel(LocalDate date, List events, Object sqlConnection) {
        super(date, events, sqlConnection);
        this.sqlCon = (SQLConnection) sqlConnection;
        this.viewDate = date;
        computeStartOfWeek();
        initializeUI();
        renderEvents(null);
    }

    /**
     * Computes the start of the week based on the view date, setting Monday as the first day.
     */
    private void computeStartOfWeek() {
        startOfWeek = viewDate.with(DayOfWeek.MONDAY);
    }

    /**
     * Initializes the user interface components of the week view including headers and time grid.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // --- Header Panel with Day Names ---
        JPanel headerPanel = new JPanel(new BorderLayout());

        // Create a container for the days header and empty space
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

        // Add the small empty space panel (fixed width to match time column)
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

        // Left column: Time labels (10:00 to 24:00)
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

        // Calculate equal width for each day column
        int dayWidth = (750 - 50) / 7; // Total width minus time column width, divided by 7 days

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

        // Ensure the days header matches the grid columns
        for (int i = 0; i < 7; i++) {
            dayLabels[i].setPreferredSize(new Dimension(dayWidth, dayLabels[i].getHeight()));
        }

        gridPanel.add(daysGridPanel, BorderLayout.CENTER);

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    /**
     * Retrieves the SQLConnection used by this panel.
     *
     * @return the SQLConnection object
     */
    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    /**
     * Sets the view date and updates the panel accordingly.
     *
     * @param date the new view date
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewDate = date;
        computeStartOfWeek();
        refreshView();
    }

    /**
     * Refreshes the view by updating header texts, clearing time slots, re-rendering events,
     * and repainting the panel.
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
     * Clears the content of all time slot panels and resets event display information.
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
     * Navigates the view by shifting the current view date by a number of weeks.
     *
     * @param direction the number of weeks to shift (positive for future, negative for past)
     */
    @Override
    public void navigate(int direction) {
        viewDate = viewDate.plusWeeks(direction);
        computeStartOfWeek();
        refreshView();
    }

    /**
     * Renders events on the grid between the start and end of the current week view.
     * Queries the database to get events, processes overlaps and sets event panels.
     *
     * @param ignored an ignored parameter (not used)
     */
    @Override
    public void renderEvents(List ignored) {
        LocalDate weekStart = startOfWeek;
        LocalDate weekEnd = startOfWeek.plusDays(6);

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name, b.booking_id " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date BETWEEN ? AND ? " +
                "AND b.booking_status = 'held' " +  // Only show unconfirmed bookings
                "ORDER BY e.start_date, e.start_time";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setDate(2, java.sql.Date.valueOf(weekEnd));
            ResultSet rs = ps.executeQuery();

            // Organize events by day
            Map<Integer, List<EventInfo>> eventsByDay = new HashMap<>();
            for (int i = 0; i < 7; i++) {
                eventsByDay.put(i, new ArrayList<>());
            }

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("name");
                String venueName = rs.getString("venue_name");
                String bookedBy = rs.getString("booked_by");
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                int dayIndex = (int) (eventDate.toEpochDay() - weekStart.toEpochDay());
                if (dayIndex < 0 || dayIndex > 6) continue;

                int startSlot = startTime.getHour() - startHour + 1; // +1 to account for empty row
                int endSlot = endTime.getHour() - startHour + 1;     // +1 to account for empty row

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

            // Process each day separately
            for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
                List<EventInfo> dayEvents = eventsByDay.get(dayIndex);
                Collections.sort(dayEvents, Comparator.comparing(e -> e.startTime));

                // Calculate maximum concurrent events for this day
                int maxConcurrent = calculateMaxConcurrentEvents(dayEvents);

                // Assign columns to events for this day
                assignEventColumns(dayEvents, maxConcurrent);

                // Store display information for each event
                for (EventInfo event : dayEvents) {
                    eventDisplayMap.put(event.eventId, new EventDisplayInfo(event.column, maxConcurrent));
                }

                // First pass: create container panels for each time slot
                for (EventInfo event : dayEvents) {
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        JPanel timeSlotPanel = timeSlotPanels[slot][dayIndex];

                        if (timeSlotPanel.getComponentCount() == 0) {
                            // Create container panel with the correct number of columns
                            JPanel containerPanel = new JPanel(new GridLayout(1, maxConcurrent, 1, 0)); // Added horizontal gap
                            containerPanel.setBackground(Color.WHITE);
                            containerPanel.setBorder(BorderFactory.createEmptyBorder());
                            timeSlotPanel.add(containerPanel, BorderLayout.CENTER);
                            timeSlotPanel.add(containerPanel, BorderLayout.CENTER);

                            // Add empty panels for all columns
                            for (int i = 0; i < maxConcurrent; i++) {
                                JPanel emptyPanel = new JPanel();
                                emptyPanel.setBackground(Color.WHITE);
                                containerPanel.add(emptyPanel);
                            }
                        }
                    }
                }

                // Second pass: add event panels to the containers
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
     * Determines the background color for an event based on the booking department.
     *
     * @param bookedBy the department or person who booked the event
     * @return the Color associated with the event booking
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
     * Creates a panel representing an individual event to be displayed in the grid.
     *
     * @param event       the EventInfo object containing event details
     * @param isFirstSlot true if this panel is in the first slot of the event duration
     * @param isLastSlot  true if this panel is in the last slot of the event duration
     * @param column      the column index assigned to the event
     * @param maxColumns  the total number of columns in the time slot container
     * @return a JPanel representing the event
     */
    private JPanel createEventPanel(EventInfo event, boolean isFirstSlot, boolean isLastSlot,
                                    int column, int maxColumns) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(eventColors.get(event.eventId));

        // Always show full border for each event
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
     * Calculates the maximum number of concurrent events in the given list.
     *
     * @param events a list of events occurring in a specific day
     * @return the maximum number of overlapping events for any time slot
     */
    private int calculateMaxConcurrentEvents(List<EventInfo> events) {
        if (events.isEmpty()) return 0;

        // Find the maximum number of overlapping events at any time slot
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
     * Assigns a column index to each event such that overlapping events are shown side by side.
     *
     * @param events     the list of events for a given day
     * @param maxColumns the maximum number of overlapping event columns in that day
     */
    private void assignEventColumns(List<EventInfo> events, int maxColumns) {
        // Group events by their time slots
        Map<Integer, List<EventInfo>> eventsBySlot = new HashMap<>();
        for (EventInfo event : events) {
            for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                eventsBySlot.computeIfAbsent(slot, k -> new ArrayList<>()).add(event);
            }
        }

        // Track which columns are available at each time slot
        Map<Integer, Set<Integer>> availableColumnsBySlot = new HashMap<>();
        for (int slot = 0; slot < numberOfSlots; slot++) {
            availableColumnsBySlot.put(slot, new HashSet<>());
            for (int col = 0; col < maxColumns; col++) {
                availableColumnsBySlot.get(slot).add(col);
            }
        }

        // Assign columns to events
        for (EventInfo event : events) {
            // Find the first column that's available in all slots this event occupies
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
                    // Mark this column as occupied for all slots this event covers
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        availableColumnsBySlot.get(slot).remove(col);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Attaches mouse event listeners to the given panel for interactive behavior
     * such as hover effects and clicking to show event details.
     *
     * @param panel   the JPanel representing the event
     * @param eventId the unique identifier of the event
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
     * Retrieves the start date of the current week view.
     *
     * @return the LocalDate representing the Monday of the week
     */
    @Override
    public LocalDate getViewStartDate() {
        return startOfWeek;
    }

    /**
     * Retrieves the end date of the current week view.
     *
     * @return the LocalDate representing the Sunday of the week
     */
    @Override
    public LocalDate getViewEndDate() {
        return startOfWeek.plusDays(6);
    }

    /**
     * Private helper class representing a time point in an event with a flag indicating
     * whether it is the start or end of an event.
     */
    private static class TimePoint implements Comparable<TimePoint> {
        LocalTime time;
        boolean isStart;

        /**
         * Constructs a TimePoint with the specified time and start flag.
         *
         * @param time    the LocalTime of the time point
         * @param isStart true if this time point is the start of an event; false otherwise
         */
        TimePoint(LocalTime time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        /**
         * Compares this TimePoint with another based on time and start/end flag.
         *
         * @param other the other TimePoint to compare
         * @return a negative integer, zero, or a positive integer as this object is less than,
         * equal to, or greater than the specified object
         */
        @Override
        public int compareTo(TimePoint other) {
            int timeCompare = this.time.compareTo(other.time);
            if (timeCompare != 0) return timeCompare;
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

    /**
     * Private helper class representing an event's information necessary for display.
     */
    private static class EventInfo {
        int eventId;
        String eventName;
        String venueName;
        String bookedBy;
        LocalTime startTime;
        LocalTime endTime;
        int startSlot;
        int endSlot;
        int dayIndex;
        int column;

        /**
         * Constructs an EventInfo object with the provided event details.
         *
         * @param eventId   the unique identifier of the event
         * @param eventName the event's name
         * @param venueName the name of the venue hosting the event
         * @param bookedBy  the department or person who booked the event
         * @param startTime the starting time of the event
         * @param endTime   the ending time of the event
         * @param startSlot the starting time slot index in the grid
         * @param endSlot   the ending time slot index in the grid
         * @param dayIndex  the index of the day in the week (0 for Monday, etc.)
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
     * Private helper class containing display information for an event, such as its
     * assigned column and the total number of columns in the time slot container.
     */
    private static class EventDisplayInfo {
        int column;
        int totalColumns;

        /**
         * Constructs an EventDisplayInfo object with the specified column and total columns.
         *
         * @param column       the column index assigned to the event
         * @param totalColumns the total number of columns in the container for the event's time slot
         */
        public EventDisplayInfo(int column, int totalColumns) {
            this.column = column;
            this.totalColumns = totalColumns;
        }
    }
}
