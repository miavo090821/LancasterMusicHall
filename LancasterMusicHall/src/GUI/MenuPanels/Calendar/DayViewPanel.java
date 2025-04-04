package GUI.MenuPanels.Calendar;

import GUI.EventDetailForm;
import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class DayViewPanel extends CalendarViewPanel {
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    // Array of panels for each time slot to hold event boxes.
    private JPanel[] eventSlots;

    // SQL connection instance.
    private SQLConnection sqlCon;

    public DayViewPanel(LocalDate startDate, java.util.List events, SQLConnection sqlCon) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.viewStartDate = startDate;
        this.viewEndDate = startDate;
        initializeUI();
        // Fetch and render events for the initially selected day.
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

        // Create a container panel for the timeline (left: time labels, right: event boxes)
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(Color.WHITE);
        // Adjusted preferred size to leave room for the bottom navigation.
        timelinePanel.setPreferredSize(new Dimension(550, 350));
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Create left panel for time labels with GridLayout (rows = number of times, 1 column)
        JPanel leftPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        leftPanel.setBackground(Color.WHITE);

        // Create right panel for event slots with GridLayout (rows = number of times, 1 column)
        JPanel rightPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        rightPanel.setBackground(Color.WHITE);

        eventSlots = new JPanel[times.length];

        for (int i = 0; i < times.length; i++) {
            // Time label
            JLabel timeLabel = new JLabel(times[i] + ":00", SwingConstants.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            leftPanel.add(timeLabel);

            // Event slot panel
            JPanel eventSlotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            eventSlotPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            eventSlotPanel.setBackground(Color.WHITE);
            // Ensure each event slot has a consistent size.
            eventSlotPanel.setPreferredSize(new Dimension(300, 60));
            rightPanel.add(eventSlotPanel);

            eventSlots[i] = eventSlotPanel;
        }

        // Add left and right panels to the timeline panel.
        timelinePanel.add(leftPanel, BorderLayout.WEST);
        timelinePanel.add(rightPanel, BorderLayout.CENTER);

        // Wrap the timelinePanel in a container.
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.add(timelinePanel, BorderLayout.CENTER);

        // Place the container in a scroll pane.
        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public void navigate(int direction) {
        // Move the selected day forward or backward.
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        refreshView();
    }

    /**
     * Render events for the currently selected day by querying the SQL connection.
     * Only events with a start_date equal to viewStartDate are fetched and displayed.
     */
    @Override
    public void renderEvents(java.util.List ignored) {
        // Clear existing event boxes in each time slot panel.
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
        }

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.start_date = ?";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("name");
                String venueName = rs.getString("venue_name");
                String bookedBy = rs.getString("booked_by");

                // Retrieve start and end times.
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                // Determine the time slot index based on the event's start time.
                int eventStartHour = startTime.getHour();
                int baseHour = Integer.parseInt(times[0]);  // Base hour is 10.
                int timeSlotIndex = eventStartHour - baseHour;
                if (timeSlotIndex < 0 || timeSlotIndex >= times.length) {
                    continue; // Skip events outside the time grid.
                }

                // Create a label for the event.
                JLabel eventLabel = new JLabel(String.format("<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                        eventName, venueName, startTime, endTime), SwingConstants.CENTER);
                eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                eventLabel.setOpaque(true);
                eventLabel.setBackground(determineEventColor(bookedBy));
                eventLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                // Attach the mouse listener to open event details.
                attachEventListeners(eventLabel, eventId);

                // Add the event label to the corresponding time slot panel.
                eventSlots[timeSlotIndex].add(eventLabel);
            }
            rs.close();
            ps.close();
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

    /**
     * Attaches a mouse listener to the event label to open the event detail form.
     */
    private void attachEventListeners(JLabel cell, int eventId) {
        Color baseColor = cell.getBackground();
        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(baseColor.darker());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                cell.setBackground(baseColor);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the event detail form using the event's ID.
                EventDetailForm eventDetailForm = new EventDetailForm(
                        (Frame) SwingUtilities.getWindowAncestor(cell),
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
}
