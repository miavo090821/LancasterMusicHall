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

    public DiaryWeekViewPanel(LocalDate date, List events, Object sqlConnection) {
        super(date, events, sqlConnection);
        this.sqlCon = (SQLConnection) sqlConnection;
        this.viewDate = date;
        computeStartOfWeek();
        initializeUI();
        renderEvents(null);
    }

    private void computeStartOfWeek() {
        startOfWeek = viewDate.with(DayOfWeek.MONDAY);
    }

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

    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewDate = date;
        computeStartOfWeek();
        refreshView();
    }

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

    @Override
    public void navigate(int direction) {
        viewDate = viewDate.plusWeeks(direction);
        computeStartOfWeek();
        refreshView();
    }

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

    @Override
    public LocalDate getViewStartDate() {
        return startOfWeek;
    }

    @Override
    public LocalDate getViewEndDate() {
        return startOfWeek.plusDays(6);
    }

    private static class TimePoint implements Comparable<TimePoint> {
        LocalTime time;
        boolean isStart;

        TimePoint(LocalTime time, boolean isStart) {
            this.time = time;
            this.isStart = isStart;
        }

        @Override
        public int compareTo(TimePoint other) {
            int timeCompare = this.time.compareTo(other.time);
            if (timeCompare != 0) return timeCompare;
            return Boolean.compare(!this.isStart, !other.isStart);
        }
    }

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

    private static class EventDisplayInfo {
        int column;
        int totalColumns;

        public EventDisplayInfo(int column, int totalColumns) {
            this.column = column;
            this.totalColumns = totalColumns;
        }
    }
}