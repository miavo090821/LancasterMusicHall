package GUI.MenuPanels.Diary;

import GUI.MenuPanels.Calendar.CalendarViewPanel;
import GUI.MenuPanels.Calendar.DayViewPanel;
import GUI.MenuPanels.Event.EventDetailForm;
import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
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
 * DiaryDayViewPanel is a concrete implementation of CalendarViewPanel tailored to display a single-day diary view.
 * <p>
 * The panel shows a timeline from 10:00 to 24:00 with corresponding event slots. The diary header displays the
 * formatted date, and events are retrieved from a database and rendered within the appropriate time slots.
 * </p>
 */
public class DiaryDayViewPanel extends CalendarViewPanel {
    /** Array of time labels (as String) for the diary timeline. */
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    /** Array of JPanel components representing individual event slots for each time label. */
    private JPanel[] eventSlots;
    /** SQL connection used to access the database. */
    private SQLConnection sqlCon;
    /** JLabel that displays the diary header with the formatted date. */
    private JLabel diaryHeader;
    /** Map that tracks colors for events by their event IDs. */
    private Map<Integer, Color> eventColors = new HashMap<>();

    /**
     * Constructs a DiaryDayViewPanel for the specified date, events, and SQL connection.
     *
     * @param startDate the starting date for the diary view.
     * @param events    a list of events to display (not used directly in rendering events here).
     * @param sqlCon    the SQLConnection used to retrieve event data.
     */
    public DiaryDayViewPanel(LocalDate startDate, java.util.List events, SQLConnection sqlCon) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.viewStartDate = startDate;
        this.viewEndDate = startDate;
        initializeUI();
        renderEvents(null);
    }

    /**
     * Sets the view date for the diary. The diary view always represents a single day.
     *
     * @param date the LocalDate to be set as the view date.
     */
    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date;
        this.viewEndDate = date;
        refreshView();
    }

    /**
     * Initializes the user interface for the diary day view.
     * <p>
     * The UI consists of a header displaying the date, a timeline with time labels, and corresponding event slots.
     * </p>
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel with date.
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        diaryHeader = new JLabel("", SwingConstants.CENTER); // Initialize as empty.
        diaryHeader.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(diaryHeader);
        add(headerPanel, BorderLayout.NORTH);

        updateDateHeader(); // Set initial date header text.

        // Timeline panel for the single day.
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Time labels panel.
        JPanel timeLabelsPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        timeLabelsPanel.setBackground(Color.WHITE);
        timeLabelsPanel.setPreferredSize(new Dimension(50, 0));

        // Event slots panel.
        JPanel eventSlotsPanel = new JPanel(new GridLayout(times.length, 1, 5, 0));
        eventSlotsPanel.setBackground(Color.WHITE);

        // Initialize event slots.
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
     * Navigates the diary view by moving forward or backward one day.
     *
     * @param direction an integer representing the navigation direction; positive for next day,
     *                  negative for previous day.
     */
    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        updateDateHeader();
        refreshView();
    }

    /**
     * Updates the diary header to show the formatted date.
     * <p>
     * The date is formatted as "DayName DayNumberSuffix MonthName Year", for example,
     * "Sunday 6th April 2025".
     * </p>
     */
    private void updateDateHeader() {
        // Format the date as "Sunday 6th April 2025".
        String dayName = viewStartDate.getDayOfWeek().toString();
        dayName = dayName.charAt(0) + dayName.substring(1).toLowerCase();

        int dayOfMonth = viewStartDate.getDayOfMonth();
        String suffix = getDayOfMonthSuffix(dayOfMonth);

        String monthName = viewStartDate.getMonth().toString();
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        int year = viewStartDate.getYear();

        String formattedDate = String.format("Diary for %s %d%s %s %d",
                dayName, dayOfMonth, suffix, monthName, year);

        diaryHeader.setText(formattedDate); // Update the header text.
    }

    /**
     * Renders events in the diary view.
     * <p>
     * This method clears any existing event boxes, queries the database for events on the current day,
     * processes them, sorts them by start time, assigns columns for overlapping events, and renders
     * them in the corresponding time slots.
     * </p>
     *
     * @param ignored a placeholder parameter that is not used in this implementation.
     */
    @Override
    public void renderEvents(java.util.List ignored) {
        // Clear existing event boxes.
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
        }

        // SQL query to select events for the specified day.
        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name, b.booking_id " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date = ? " +
                "AND b.booking_status = 'held' " +  // Only show 'held' (unconfirmed) bookings.
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
                // Append booking id to event name.
                eventName = eventName + " (Booking: " + bookingId + ")";
                String venueName = rs.getString("venue_name");
                String bookedBy = rs.getString("booked_by");
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                int startSlot = startTime.getHour() - Integer.parseInt(times[0]);
                int endSlot = endTime.getHour() - Integer.parseInt(times[0]);

                if (startSlot >= 0 && endSlot < times.length) {
                    DiaryDayViewPanel.EventInfo event = new DiaryDayViewPanel.EventInfo(
                            eventId, eventName, venueName, bookedBy,
                            startTime, endTime, startSlot, endSlot
                    );
                    events.add(event);

                    // Store the color for the event.
                    eventColors.put(eventId, determineEventColor(bookedBy));
                }
            }
            rs.close();
            ps.close();

            // Sort events by start time.
            Collections.sort(events, Comparator.comparing(e -> e.startTime));

            // Calculate the maximum number of concurrent events at any time slot.
            int maxConcurrent = calculateMaxConcurrentEvents(events);

            // Assign columns to overlapping events.
            assignEventColumns(events, maxConcurrent);

            // Render events by placing an event panel in each time slot that the event spans.
            for (EventInfo event : events) {
                for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                    JPanel timeSlotPanel = eventSlots[slot];

                    if (timeSlotPanel.getComponentCount() == 0) {
                        // Create a container panel for event columns if not already created.
                        JPanel containerPanel = new JPanel(new GridLayout(1, maxConcurrent, 2, 0));
                        containerPanel.setBackground(Color.WHITE);
                        timeSlotPanel.add(containerPanel, BorderLayout.CENTER);

                        // Add empty panels for each column.
                        for (int i = 0; i < maxConcurrent; i++) {
                            JPanel emptyPanel = new JPanel();
                            emptyPanel.setBackground(Color.WHITE);
                            containerPanel.add(emptyPanel);
                        }
                    }

                    // Get the container panel.
                    JPanel containerPanel = (JPanel) timeSlotPanel.getComponent(0);

                    // Replace the panel at the assigned column with the event panel.
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
     * Returns the appropriate suffix ("st", "nd", "rd", "th") for the specified day of the month.
     *
     * @param n the day of the month.
     * @return a String representing the day suffix.
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
     * Creates and returns a JPanel representing a single event.
     * <p>
     * The event panel shows event details if this is the first time slot of the event,
     * and applies custom borders depending on whether this time slot is the first or last for the event.
     * </p>
     *
     * @param event       the EventInfo object containing event details.
     * @param isFirstSlot true if this time slot is the start of the event.
     * @param isLastSlot  true if this time slot is the end of the event.
     * @return a JPanel configured to display the event.
     */
    private JPanel createEventPanel(EventInfo event, boolean isFirstSlot, boolean isLastSlot) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(eventColors.get(event.eventId));

        // Customize borders based on the slot position.
        if (isFirstSlot && isLastSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.DARK_GRAY));
        } else if (isFirstSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(1, 1, 0, 1, Color.DARK_GRAY));
        } else if (isLastSlot) {
            panel.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.DARK_GRAY));
        } else {
            panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, Color.DARK_GRAY));
        }

        if (isFirstSlot) {
            JLabel label = new JLabel(
                    String.format("<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                            event.eventName, event.venueName,
                            event.startTime, event.endTime),
                    SwingConstants.CENTER
            );
            label.setFont(new Font("Arial", Font.PLAIN, 9)); // Reduced font size.
            panel.add(label, BorderLayout.CENTER);
        }

        attachEventListeners(panel, event.eventId);
        return panel;
    }

    /**
     * Determines the background color for an event based on the 'bookedBy' value.
     *
     * @param bookedBy the identifier indicating who booked the event.
     * @return a Color object used as the background for the event panel.
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
     * Calculates the maximum number of concurrent (overlapping) events at any time slot.
     *
     * @param events the list of events.
     * @return the maximum number of overlapping events.
     */
    private int calculateMaxConcurrentEvents(List<EventInfo> events) {
        if (events.isEmpty()) return 0;

        // Use times.length instead of view-specific slot count.
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
     * Assigns column indices to events so that overlapping events are displayed side by side.
     *
     * @param events     the list of events.
     * @param maxColumns the maximum number of columns available for concurrent events.
     */
    private void assignEventColumns(List<EventInfo> events, int maxColumns) {
        // Group events by their time slots.
        Map<Integer, List<EventInfo>> eventsBySlot = new HashMap<>();
        for (EventInfo event : events) {
            for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                eventsBySlot.computeIfAbsent(slot, k -> new ArrayList<>()).add(event);
            }
        }

        // Track available columns for each time slot.
        Map<Integer, Set<Integer>> availableColumnsBySlot = new HashMap<>();
        for (int slot = 0; slot < times.length; slot++) {  // Use times.length here.
            availableColumnsBySlot.put(slot, new HashSet<>());
            for (int col = 0; col < maxColumns; col++) {
                availableColumnsBySlot.get(slot).add(col);
            }
        }

        // Assign columns to events.
        for (EventInfo event : events) {
            // Find the first available column present in all time slots of the event.
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
                    // Mark this column as occupied across the event's time slots.
                    for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                        availableColumnsBySlot.get(slot).remove(col);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Attaches mouse listeners to an event panel to enable interactivity.
     * <p>
     * When hovered, the panel background darkens, and when clicked, an EventDetailForm is opened.
     * </p>
     *
     * @param panel   the JPanel representing the event.
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
     * Refreshes the diary view by clearing existing events and re-rendering them.
     */
    @Override
    public void refreshView() {
        // Clear existing events.
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
        }
        renderEvents(null);
        revalidate();
        repaint();
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
     * TimePoint represents a specific time instance and a flag indicating whether this point
     * marks the start or end of an event.
     * <p>
     * This is used internally to assist with overlapping event calculations.
     * </p>
     */
    private static class TimePoint implements Comparable<TimePoint> {
        /** The time associated with this point. */
        LocalTime time;
        /** True if this represents the start of an event; false if it represents the end. */
        boolean isStart;

        /**
         * Constructs a TimePoint with the specified time and type.
         *
         * @param time    the LocalTime value.
         * @param isStart true if this time marks the start of an event.
         */
        TimePoint(LocalTime time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        /**
         * Compares this TimePoint to another TimePoint.
         *
         * @param other the other TimePoint to compare to.
         * @return a negative integer, zero, or a positive integer as this TimePoint is less than,
         * equal to, or greater than the specified TimePoint.
         */
        @Override
        public int compareTo(TimePoint other) {
            int timeCompare = this.time.compareTo(other.time);
            if (timeCompare != 0) return timeCompare;
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

    /**
     * EventInfo holds the essential details for an event.
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
        /** The start slot index in the diary timeline. */
        int startSlot;
        /** The end slot index in the diary timeline. */
        int endSlot;
        /** The assigned column for displaying the event in overlapping scenarios. */
        int column;

        /**
         * Constructs an EventInfo object with the specified event details.
         *
         * @param eventId   the event identifier.
         * @param eventName the event name.
         * @param venueName the venue name.
         * @param bookedBy  who booked the event.
         * @param startTime the event's start time.
         * @param endTime   the event's end time.
         * @param startSlot the starting time slot index.
         * @param endSlot   the ending time slot index.
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
