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

public class DayViewPanel extends CalendarViewPanel {
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    private JPanel[] eventSlots;
    private SQLConnection sqlCon;
    private Map<Integer, Color> eventColors = new HashMap<>(); // Track colors by event ID

    public DayViewPanel(LocalDate startDate, java.util.List events, SQLConnection sqlCon) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.viewStartDate = startDate;
        this.viewEndDate = startDate;
        initializeUI();
        renderEvents(null);
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date;
        this.viewEndDate = date;
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel (unchanged)
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

        // Timeline panel
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10)); // Reduced padding

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
            JLabel timeLabel = new JLabel(times[i] + ":00", SwingConstants.CENTER); // Changed to CENTER
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

    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        refreshView();
    }

    @Override
    public void renderEvents(java.util.List ignored) {
        // Clear all slots first
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setLayout(new BorderLayout());
            slot.setBackground(Color.WHITE);
        }

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.start_date = ? " +
                "ORDER BY e.start_time";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ResultSet rs = ps.executeQuery();

            List<EventInfo> events = new ArrayList<>();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("name");
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

            // Calculate maximum concurrent events at any time
            int maxConcurrent = calculateMaxConcurrentEvents(events);

            // Assign columns to events
            assignEventColumns(events, maxConcurrent);

            // Render events
            for (EventInfo event : events) {
                for (int slot = event.startSlot; slot <= event.endSlot; slot++) {
                    JPanel timeSlotPanel = eventSlots[slot];

                    if (timeSlotPanel.getComponentCount() == 0) {
                        // If no panel exists for this time slot yet, create one
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

                    // Get the container panel
                    JPanel containerPanel = (JPanel) timeSlotPanel.getComponent(0);

                    // Replace the panel at the assigned column with our event
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

    private JPanel createEventPanel(EventInfo event, boolean isFirstSlot, boolean isLastSlot) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(eventColors.get(event.eventId));

        // Customize borders (unchanged)
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
            label.setFont(new Font("Arial", Font.PLAIN, 9)); // Reduced from 10 to 9
            panel.add(label, BorderLayout.CENTER);
        }

        attachEventListeners(panel, event.eventId);
        return panel;
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

    private int calculateMaxConcurrentEvents(List<EventInfo> events) {
        if (events.isEmpty()) return 0;

        // Use times.length instead of numberOfSlots
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
        for (int slot = 0; slot < times.length; slot++) {  // Use times.length here
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
    public void refreshView() {
        renderEvents(null);
    }

    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
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
        int column;

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