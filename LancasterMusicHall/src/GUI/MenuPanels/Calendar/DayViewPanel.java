package GUI.MenuPanels.Calendar;

import GUI.MenuPanels.Event.EventDetailForm;
import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DayViewPanel is a concrete implementation of CalendarViewPanel that displays
 * events for a single day in a timeline view.
 *
 * <p>This panel organizes events according to time slots (hours) and allows
 * navigation between days. It retrieves event data from a database, processes it,
 * and renders event information on the UI.
 * </p>
 */
public class DayViewPanel extends CalendarViewPanel {
    /** Array of time labels to be displayed on the timeline. */
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};

    /** Array of JPanel components acting as time slots where events are rendered. */
    private JPanel[] eventSlots;

    /** The SQLConnection used to retrieve event data from the database. */
    private SQLConnection sqlCon;

    /** Map to track assigned colors for events, keyed by event ID. */
    private Map<Integer, Color> eventColors = new HashMap<>();

    /**
     * Constructs a DayViewPanel using the provided start date, list of events, and SQL connection.
     *
     * @param startDate the date for which to display the events.
     * @param events    the list of events to be displayed.
     * @param sqlCon    the SQLConnection used to access the event database.
     */
    public DayViewPanel(LocalDate startDate, java.util.List events, SQLConnection sqlCon) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.viewStartDate = startDate;
        this.viewEndDate = startDate;
        initializeUI();
        renderEvents(null);
    }

    /**
     * Sets the current view date for the panel.
     *
     * @param date the LocalDate representing the new day to be viewed.
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date;
        this.viewEndDate = date;
        refreshView();
    }

    /**
     * Initializes the user interface components for the day view.
     * <p>
     * This method sets up the layout, header with formatted date information, timeline
     * with time labels, and event slots where event panels will be rendered.
     * </p>
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Format the date as "Monday 24th April 2025"
        String dayName = viewStartDate.getDayOfWeek().toString();
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();

        int dayOfMonth = viewStartDate.getDayOfMonth();
        String suffix = getDayOfMonthSuffix(dayOfMonth);

        String monthName = viewStartDate.getMonth().toString();
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        int year = viewStartDate.getYear();

        String formattedDate = String.format("Calendar for %s %d%s %s %d",
                dayName, dayOfMonth, suffix, monthName, year);

        JLabel diaryHeader = new JLabel(formattedDate, SwingConstants.CENTER);
        diaryHeader.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(diaryHeader);
        add(headerPanel, BorderLayout.NORTH);

        // Timeline panel setup
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Time labels panel
        JPanel timeLabelsPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        timeLabelsPanel.setBackground(Color.WHITE);
        timeLabelsPanel.setPreferredSize(new Dimension(50, 0));

        // Event slots panel
        JPanel eventSlotsPanel = new JPanel(new GridLayout(times.length, 1, 5, 0));
        eventSlotsPanel.setBackground(Color.WHITE);

        // Initialize event slots with reduced height
        eventSlots = new JPanel[times.length];
        for (int i = 0; i < times.length; i++) {
            JLabel timeLabel = new JLabel(times[i] + ":00", SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            timeLabelsPanel.add(timeLabel);

            JPanel eventSlot = new JPanel(new BorderLayout());
            eventSlot.setBackground(Color.WHITE);
            eventSlot.setPreferredSize(new Dimension(0, 40));
            eventSlots[i] = eventSlot;
            eventSlotsPanel.add(eventSlot);
        }

        timelinePanel.add(timeLabelsPanel, BorderLayout.WEST);
        timelinePanel.add(eventSlotsPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Navigates the view by moving the current day by the given direction.
     *
     * @param direction an integer representing the number of days to move forward (positive)
     *                  or backward (negative).
     */
    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        refreshView();
    }

    /**
     * Retrieves and renders events for the current day view.
     * <p>
     * This method clears existing events and uses a SQL query to fetch confirmed events
     * for the current day. It then processes these events, sorting them and assigning columns
     * based on overlapping times before rendering each event on the appropriate time slot.
     * </p>
     *
     * @param ignored a placeholder parameter that is not used in this implementation.
     */
    @Override
    public void renderEvents(java.util.List ignored) {
        // Clear all event slots
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setLayout(new BorderLayout());
            slot.setBackground(Color.WHITE);
        }

        // Query to select events joining with Venue and Booking to get required details
        String query = "SELECT e.event_id, e.booking_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date = ? " +
                "AND b.booking_status = 'confirmed' " +
                "ORDER BY e.start_time";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ResultSet rs = ps.executeQuery();

            List<EventInfo> events = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                int bookingId = rs.getInt("booking_id");
                String eventName = rs.getString("name");
                // Append booking id to event name
                eventName = eventName + " (Booking: " + bookingId + ")";
                String venueName = rs.getString("venue_name");
                String bookedBy = rs.getString("booked_by");
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                int startSlot = startTime.getHour() - Integer.parseInt(times[0]);
                int endSlot = endTime.getHour() - Integer.parseInt(times[0]);

                if (startSlot >= 0 && endSlot < times.length) {
                    EventInfo event = new EventInfo(
                            eventId, eventName, venueName, bookedBy,
                            startTime, endTime, startSlot, endSlot
                    );
                    events.add(event);

                    // Store the color in the eventColors map
                    eventColors.put(eventId, determineEventColor(bookedBy));
                }
            }
            rs.close();
            ps.close();

            // Sort events by start time
            Collections.sort(events, Comparator.comparing(e -> e.startTime));

            // Calculate maximum concurrent events across time slots
            int maxConcurrent = calculateMaxConcurrentEvents(events);

            // Assign columns to events for proper layout in the UI
            assignEventColumns(events, maxConcurrent);

            // Render each event in its assigned time slots and columns
            for (EventInfo event : events) {
                for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                    JPanel timeSlotPanel = eventSlots[slot];

                    if (timeSlotPanel.getComponentCount() == 0) {
                        // Create container panel with a GridLayout for event columns if not already present
                        JPanel containerPanel = new JPanel(new GridLayout(1, maxConcurrent, 2, 0));
                        containerPanel.setBackground(Color.WHITE);
                        timeSlotPanel.add(containerPanel, BorderLayout.CENTER);

                        // Add empty panels for all columns
                        for (int i = 0; i < maxConcurrent; i++) {
                            JPanel emptyPanel = new JPanel();
                            emptyPanel.setBackground(Color.WHITE);
                            containerPanel.add(emptyPanel);
                        }
                    }

                    // Get the container panel that holds the columns for the current time slot
                    JPanel containerPanel = (JPanel) timeSlotPanel.getComponent(0);

                    // Replace the panel at the assigned column with our event panel
                    JPanel eventPanel = createEventPanel(event, slot == event.startSlot, slot == event.endSlot);
                    containerPanel.remove(event.column);
                    containerPanel.add(eventPanel, event.column);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        revalidate();
        repaint();
    }

    /**
     * Returns the appropriate suffix ("st", "nd", "rd", "th") for a given day number.
     *
     * @param n the day of the month.
     * @return a String representing the suffix for the day.
     */
    private String getDayOfMonthSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    /**
     * Creates a JPanel representing an event for display in the timeline.
     *
     * @param event        the EventInfo object containing event details.
     * @param isFirstSlot  true if this panel represents the first time slot of the event.
     * @param isLastSlot   true if this panel represents the last time slot of the event.
     * @return a JPanel configured to display event information.
     */
    private JPanel createEventPanel(EventInfo event, boolean isFirstSlot, boolean isLastSlot) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(eventColors.get(event.eventId));

        // Set custom borders based on event's slot position
        if (isFirstSlot && isLastSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        } else if (isFirstSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.DARK_GRAY));
        } else if (isLastSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.DARK_GRAY));
        } else {
            panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.DARK_GRAY));
        }

        // Add event details label only to the first slot of the event
        if (isFirstSlot) {
            JLabel label = new JLabel(
                    String.format("<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                            event.eventName, event.venueName,
                            event.startTime, event.endTime),
                    SwingConstants.CENTER
            );
            label.setFont(new Font("Arial", Font.PLAIN, 9)); // Reduced font size
            panel.add(label, BorderLayout.CENTER);
        }

        // Attach mouse listeners for interactivity (e.g., highlight and click)
        attachEventListeners(panel, event.eventId);
        return panel;
    }

    /**
     * Determines the background color for an event based on the user who booked it.
     *
     * @param bookedBy the String identifier for who booked the event.
     * @return a Color object representing the event's background color.
     */
    private Color determineEventColor(String bookedBy) {
        if (bookedBy != null) {
            if (bookedBy.equalsIgnoreCase("operations")) {
                return new Color(200, 230, 255);  // Light blue.
            } else if (bookedBy.equalsIgnoreCase("marketing")) {
                return new Color(255, 230, 200);  // Light orange.
            }
        }
        return new Color(230, 255, 200);  // Default green color.
    }

    /**
     * Calculates the maximum number of concurrent events (overlapping events) across all time slots.
     *
     * @param events a List of EventInfo objects to consider.
     * @return an integer representing the maximum number of concurrent events.
     */
    private int calculateMaxConcurrentEvents(List<EventInfo> events) {
        if (events.isEmpty()) return 0;

        int[] concurrentCounts = new int[times.length];
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
     * Assigns columns to events so that overlapping events are displayed side by side.
     *
     * @param events     a List of EventInfo objects representing events.
     * @param maxColumns the maximum number of columns (i.e., concurrent events) available.
     */
    private void assignEventColumns(List<EventInfo> events, int maxColumns) {
        // Group events by each time slot
        Map<Integer, List<EventInfo>> eventsBySlot = new HashMap<>();
        for (EventInfo event : events) {
            for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                eventsBySlot.computeIfAbsent(slot, k -> new ArrayList<>()).add(event);
            }
        }

        // Track available columns for every time slot
        Map<Integer, Set<Integer>> availableColumnsBySlot = new HashMap<>();
        for (int slot = 0; slot < times.length; slot++) {
            availableColumnsBySlot.put(slot, new HashSet<>());
            for (int col = 0; col < maxColumns; col++) {
                availableColumnsBySlot.get(slot).add(col);
            }
        }

        // Assign the first available column to each event over the slots it occupies
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
                    // Mark column as occupied in all time slots the event spans
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        availableColumnsBySlot.get(slot).remove(col);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Attaches mouse listeners to an event panel to provide interactivity.
     * <p>
     * Mouse events include highlighting the panel when hovered and opening an event detail form
     * when clicked.
     * </p>
     *
     * @param panel   the JPanel to which listeners are attached.
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
     * Refreshes the current view by re-rendering the events.
     */
    @Override
    public void refreshView() {
        renderEvents(null);
    }

    /**
     * Provides the SQLConnection used by the panel.
     *
     * @return the SQLConnection associated with this panel.
     */
    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    /**
     * TimePoint represents a specific point in time (LocalTime) and indicates whether it
     * is the start or end of an event.
     *
     * <p>Used internally for calculating overlapping events.</p>
     */
    private static class TimePoint implements Comparable<TimePoint> {
        /** The time value. */
        LocalTime time;

        /** true if this point represents the start of an event; false if it represents the end. */
        boolean isStart;

        /**
         * Constructs a TimePoint with a specified time and type indicator.
         *
         * @param time    the LocalTime value.
         * @param isStart true if this is a start point, false otherwise.
         */
        TimePoint(LocalTime time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        /**
         * Compares this TimePoint with another for ordering.
         *
         * @param other the other TimePoint.
         * @return a negative integer, zero, or a positive integer as this TimePoint is less than,
         *         equal to, or greater than the specified TimePoint.
         */
        @Override
        public int compareTo(TimePoint other) {
            int timeCompare = this.time.compareTo(other.time);
            if (timeCompare != 0) return timeCompare;
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

    /**
     * EventInfo holds information related to an event including its time slots, booking details,
     * and layout column assignment.
     */
    private static class EventInfo {
        /** The unique event identifier. */
        int eventId;
        /** The name of the event. */
        String eventName;
        /** The venue name where the event is held. */
        String venueName;
        /** Identifier for who booked the event. */
        String bookedBy;
        /** The starting time of the event. */
        LocalTime startTime;
        /** The ending time of the event. */
        LocalTime endTime;
        /** The starting time slot index for the event. */
        int startSlot;
        /** The ending time slot index for the event. */
        int endSlot;
        /** The assigned column index for layout purposes. */
        int column;

        /**
         * Constructs an EventInfo object with the given event details.
         *
         * @param eventId   the unique event identifier.
         * @param eventName the name of the event.
         * @param venueName the venue where the event is held.
         * @param bookedBy  the identifier for the entity/person who booked the event.
         * @param startTime the starting time of the event.
         * @param endTime   the ending time of the event.
         * @param startSlot the index of the starting time slot.
         * @param endSlot   the index of the ending time slot.
         */
        public EventInfo(int eventId, String eventName, String venueName,
                         String bookedBy, LocalTime startTime, LocalTime endTime,
                         int startSlot, int endSlot) {
            this.eventId = eventId;
            this.eventName = eventName;
            this.venueName = venueName;
            this.bookedBy = bookedBy;
            this.startTime = startTime;
            this.endTime = endTime;
            this.startSlot = startSlot;
            this.endSlot = endSlot;
        }
    }
}
