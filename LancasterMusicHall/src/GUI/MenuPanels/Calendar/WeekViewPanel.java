package GUI.MenuPanels.Calendar;

import Database.SQLConnection;
import GUI.MenuPanels.Event.EventDetailForm;
import operations.entities.Event;

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

public class WeekViewPanel extends CalendarViewPanel {
    private LocalDate viewDate;
    private LocalDate startOfWeek;

    // Header for days (Monday to Sunday)
    private JLabel[] dayLabels = new JLabel[7];
    private JPanel daysHeaderPanel;

    // Time grid components
    private JPanel gridPanel;
    private JPanel timeLabelPanel;
    private JPanel daysGridPanel;
    private JPanel[][] eventCells; // rows: time slots, cols: days

    private final int startHour = 10;
    private final int endHour = 24;  // inclusive
    private final int numberOfSlots = endHour - startHour + 1;

    // SQL connection (passed as Object and casted)
    private SQLConnection sqlCon;

    // Constructor
    public WeekViewPanel(LocalDate date, java.util.List<Event> events, Object sqlConnection) {
        super(date, events, sqlConnection);
        this.sqlCon = (SQLConnection) sqlConnection;
        this.viewDate = date;
        computeStartOfWeek();
        initializeUI();
        renderEvents(null); // we ignore the passed-in events now
    }

    // Calculate the start of the week (Monday)
    private void computeStartOfWeek() {
        startOfWeek = viewDate.with(DayOfWeek.MONDAY);
    }

    // Build the UI for the week view including day headers and time grid.
    private void initializeUI() {
        setLayout(new BorderLayout());

        // --- Header Panel with Day Names ---
        daysHeaderPanel = new JPanel(new GridLayout(1, 7));
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("EEEE dd/MM");
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayLabels[i] = new JLabel(day.format(headerFormatter), SwingConstants.CENTER);
            dayLabels[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            daysHeaderPanel.add(dayLabels[i]);
        }
        add(daysHeaderPanel, BorderLayout.NORTH);

        // --- Center Panel with Time Grid ---
        gridPanel = new JPanel(new BorderLayout());

        // Left column: Time labels (10:00 to 24:00)
        timeLabelPanel = new JPanel(new GridLayout(numberOfSlots, 1));
        for (int hour = startHour; hour <= endHour; hour++) {
            JLabel timeLabel = new JLabel(String.format("%02d:00", hour), SwingConstants.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            timeLabelPanel.add(timeLabel);
        }
        gridPanel.add(timeLabelPanel, BorderLayout.WEST);

        // Right side: Days grid for events
        daysGridPanel = new JPanel(new GridLayout(numberOfSlots, 7));
        eventCells = new JPanel[numberOfSlots][7];
        for (int row = 0; row < numberOfSlots; row++) {
            for (int col = 0; col < 7; col++) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setBackground(Color.WHITE);
                eventCells[row][col] = cell;
                daysGridPanel.add(cell);
            }
        }
        gridPanel.add(daysGridPanel, BorderLayout.CENTER);

        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    // Update the view date, recalculate week start and refresh header and grid.
    @Override
    public void setViewDate(LocalDate date) {
        this.viewDate = date;
        computeStartOfWeek();
        refreshView();
    }

    // Refresh the header and grid, then re-render events.
    @Override
    public void refreshView() {
        DateTimeFormatter headerFormatter = DateTimeFormatter.ofPattern("EEEE dd/MM");
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfWeek.plusDays(i);
            dayLabels[i].setText(day.format(headerFormatter));
        }
        clearEventCells();
        // Query SQL for events in the current week and render them.
        renderEvents(null);
        revalidate();
        repaint();
    }

    // Clear events from all grid cells.
    private void clearEventCells() {
        for (int row = 0; row < numberOfSlots; row++) {
            for (int col = 0; col < 7; col++) {
                eventCells[row][col].removeAll();
                eventCells[row][col].setBackground(Color.WHITE);
            }
        }
    }

    // Navigation: move week forward or backward.
    @Override
    public void navigate(int direction) {
        // direction: -1 for previous week, 1 for next week.
        viewDate = viewDate.plusWeeks(direction);
        computeStartOfWeek();
        refreshView();
    }

    /**
     * Render events into the week view by directly fetching data from SQL.
     * The query selects events where the start_date falls within the current week.
     * Then, for each event, the corresponding grid cell is determined by calculating the
     * day index (relative to startOfWeek) and the time slot row (from event's start_time).
     */
    @Override
    public void renderEvents(java.util.List<Event> ignored) {
        // Clear grid cells first.
        clearEventCells();

        // Define the week range.
        LocalDate weekStart = startOfWeek;
        LocalDate weekEnd = startOfWeek.plusDays(6);

        // Build SQL query to fetch events for the week.
        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "WHERE e.start_date BETWEEN ? AND ?";

        try {
            PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(weekStart));
            ps.setDate(2, java.sql.Date.valueOf(weekEnd));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventName = rs.getString("name");
                String venueName = rs.getString("venue_name");

                // Retrieve start and end times.
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                // Retrieve event start date.
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                // Determine the grid cell indices.
                int dayIndex = (int) (eventDate.toEpochDay() - weekStart.toEpochDay());
                int eventStartHour = startTime.getHour();
                int rowIndex = eventStartHour - startHour;
                if (rowIndex < 0 || rowIndex >= numberOfSlots || dayIndex < 0 || dayIndex > 6) {
                    continue; // Skip events outside the grid range.
                }

                // Create a label for the event.
                JLabel eventLabel = new JLabel(String.format(
                        "<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                        eventName, venueName, startTime, endTime), SwingConstants.CENTER);
                eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                eventLabel.setOpaque(true);
                eventLabel.setBackground(determineEventColor(rs.getString("booked_by")));
                eventLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                // Attach a mouse listener to open event details on click.
                attachEventListeners(eventLabel, eventId);

                // Add the event label into the corresponding cell.
                eventCells[rowIndex][dayIndex].add(eventLabel);
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
                return new Color(200, 230, 255);  // Light blue for operations
            } else if (bookedBy.equalsIgnoreCase("marketing")) {
                return new Color(255, 230, 200);  // Light orange for marketing
            }
        }
        return new Color(230, 255, 200);  // Default green for others or null
    }

    /**
     * Attach a mouse listener to the event label to open the EventDetailForm when clicked.
     */
    private void attachEventListeners(JLabel eventLabel, int eventId) {
        Color baseColor = eventLabel.getBackground();
        eventLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                eventLabel.setBackground(baseColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                eventLabel.setBackground(baseColor);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Open the event detail form using the event's ID.
                EventDetailForm eventDetailForm = new EventDetailForm(
                        (Frame) SwingUtilities.getWindowAncestor(eventLabel),
                        getSQLConnection(),
                        String.valueOf(eventId)
                );
                eventDetailForm.setVisible(true);
            }
        });
    }

    // Returns the start date of the current view (Monday).
    @Override
    public LocalDate getViewStartDate() {
        return startOfWeek;
    }

    // Returns the end date of the current view (Sunday).
    @Override
    public LocalDate getViewEndDate() {
        return startOfWeek.plusDays(6);
    }
}
