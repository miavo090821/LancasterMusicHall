package GUI;

import GUI.MenuPanels.Event.EventDetailForm;
import Database.SQLConnection;
import GUI.MenuPanels.Calendar.CalendarViewPanel;
import GUI.MenuPanels.Event.NewEventForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DiaryPanel is a day-view panel that mimics the look of DayViewPanel.
 * It displays a timeline from 10:00 to 24:00 with event slots and allows users
 * to click an event to view its details.
 */
public class DiaryPanel extends CalendarViewPanel {
    // Define the time slots (from 10:00 to 24:00).
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    // Array of panels for each time slot.
    private JPanel[] eventSlots;
    // SQL connection instance.
    private SQLConnection sqlCon;

    /**
     * Constructs a DiaryPanel for a specific day.
     *
     * @param date   The day to display.
     * @param events A list of events (can be empty).
     * @param sqlCon The SQL connection instance.
     */
    public DiaryPanel(LocalDate date, java.util.List events, SQLConnection sqlCon) {
        super(date, events, sqlCon);
        this.sqlCon = sqlCon;
        // For a day view, both start and end dates are the same.
        this.viewStartDate = date;
        this.viewEndDate = date;
        initializeUI();
        renderEvents(null);
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date;
        this.viewEndDate = date;
        refreshView();
    }

    /**
     * Initializes the UI components with a header and a timeline panel.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header label for the diary view.
        JLabel diaryHeader = new JLabel("Diary for " + viewStartDate, SwingConstants.CENTER);
        diaryHeader.setFont(new Font("Arial", Font.BOLD, 16));
        diaryHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(diaryHeader, BorderLayout.NORTH);

        // Timeline panel: left side for time labels, right side for event slots.
        JPanel timelinePanel = new JPanel(new BorderLayout());
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setPreferredSize(new Dimension(550, 350));
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        // Left panel with time labels.
        JPanel leftPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        leftPanel.setBackground(Color.WHITE);
        // Right panel with event slots.
        JPanel rightPanel = new JPanel(new GridLayout(times.length, 1, 5, 5));
        rightPanel.setBackground(Color.WHITE);

        // Initialize event slot panels.
        eventSlots = new JPanel[times.length];
        for (int i = 0; i < times.length; i++) {
            JLabel timeLabel = new JLabel(times[i] + ":00", SwingConstants.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            leftPanel.add(timeLabel);

            JPanel eventSlotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            eventSlotPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            eventSlotPanel.setBackground(Color.WHITE);
            eventSlotPanel.setPreferredSize(new Dimension(300, 60));
            rightPanel.add(eventSlotPanel);

            eventSlots[i] = eventSlotPanel;
        }
        timelinePanel.add(leftPanel, BorderLayout.WEST);
        timelinePanel.add(rightPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(timelinePanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // Add bottom panel with controls
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    /**
     * Creates the bottom panel with navigation and view controls.
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(Box.createHorizontalGlue());

        // Left column - View controls
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);

        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        viewPanel.setBackground(Color.WHITE);
        viewPanel.add(new JLabel("View:"));
        JComboBox<String> viewCombo = new JComboBox<>(new String[]{"Day", "Week", "Month"});
        viewPanel.add(viewCombo);
        leftColumn.add(viewPanel);

        bottomPanel.add(leftColumn);
        bottomPanel.add(Box.createHorizontalGlue());

        // Middle column - New Draft button (changed from New Event)
        JButton newEventButton = new JButton("New Draft");
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 30));
        newEventButton.addActionListener(e -> {
            // Create and show the NewEventForm dialog
            NewEventForm newEventForm = new NewEventForm(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    sqlCon
            );
            newEventForm.setVisible(true);

            // Refresh the calendar after the dialog closes
            newEventForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshView();
                }
            });
        });
        bottomPanel.add(newEventButton);
        bottomPanel.add(Box.createHorizontalGlue());


        // Right column - Date navigation
        JPanel rightColumn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        rightColumn.setBackground(Color.WHITE);

        JButton prevButton = new JButton("<");
        prevButton.addActionListener(e -> navigate(-1));

        JButton nextButton = new JButton(">");
        nextButton.addActionListener(e -> navigate(1));

        rightColumn.add(prevButton);
        rightColumn.add(nextButton);
        bottomPanel.add(rightColumn);
        bottomPanel.add(Box.createHorizontalGlue());

        return bottomPanel;
    }

    @Override
    public void navigate(int direction) {
        // Navigate to the next or previous day.
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        refreshView();
    }

    /**
     * Queries and renders events for the currently selected day.
     */
    @Override
    public void renderEvents(java.util.List ignored) {
        // Clear existing event boxes.
        for (JPanel slot : eventSlots) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
        }

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name " +
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
                LocalTime startTime = rs.getTime("start_time").toLocalTime();
                LocalTime endTime = rs.getTime("end_time").toLocalTime();

                int eventStartHour = startTime.getHour();
                int baseHour = Integer.parseInt(times[0]); // 10:00 is the first slot.
                int timeSlotIndex = eventStartHour - baseHour;
                if (timeSlotIndex < 0 || timeSlotIndex >= times.length) {
                    continue; // Skip events outside our defined time grid.
                }

                JLabel eventLabel = new JLabel(String.format("<html><center>%s<br/>%s<br/>%s - %s</center></html>",
                        eventName, venueName, startTime, endTime), SwingConstants.CENTER);
                eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
                eventLabel.setOpaque(true);
                eventLabel.setBackground(determineEventColor(bookedBy));
                eventLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

                attachEventListeners(eventLabel, eventId);
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

    /**
     * Returns a background color based on who booked the event.
     */
    private Color determineEventColor(String bookedBy) {
        if (bookedBy != null) {
            if (bookedBy.equalsIgnoreCase("operations")) {
                return new Color(200, 230, 255); // Light blue.
            } else if (bookedBy.equalsIgnoreCase("marketing")) {
                return new Color(255, 230, 200); // Light orange.
            }
        }
        return new Color(230, 255, 200); // Default light green.
    }

    /**
     * Attaches a mouse listener to an event label to open event details.
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