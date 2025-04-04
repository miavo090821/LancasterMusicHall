package GUI.MenuPanels.Calendar;

import GUI.EventDetailForm;
import operations.module.Event;
import Database.SQLConnection; // make sure this import is correct

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class DayViewPanel extends CalendarViewPanel {
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    private JLabel[] eventSlots;

    // For accessing SQLConnection – you must supply your own method.
    private SQLConnection sqlCon;

    public DayViewPanel(LocalDate startDate, List<Event> events, SQLConnection sqlCon) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.viewStartDate = startDate;
        this.viewEndDate = startDate;
        initializeUI();
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date;
        this.viewEndDate = date;
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Timeline panel
        JPanel timelinePanel = new JPanel(new GridBagLayout());
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setPreferredSize(new Dimension(550, 400));

        initializeDayGrid(timelinePanel);
        add(new JScrollPane(timelinePanel), BorderLayout.CENTER);
    }

    private void initializeDayGrid(JPanel timelinePanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        eventSlots = new JLabel[times.length];

        for (int i = 0; i < times.length; i++) {
            // Time label
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weighty = 0;
            gbc.insets = new Insets(0, 5, 0, 5);
            JLabel timeLabel = new JLabel(times[i] + ":00", SwingConstants.RIGHT);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timelinePanel.add(timeLabel, gbc);

            // Event slot
            gbc.gridx = 1;
            gbc.gridy = i;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(0, 0, 0, 0);
            JLabel eventSlot = new JLabel("", SwingConstants.CENTER);
            eventSlot.setName("eventSlot");
            eventSlot.setPreferredSize(new Dimension(300, 60));
            eventSlot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            eventSlot.setOpaque(true);
            eventSlot.setBackground(Color.WHITE);
            timelinePanel.add(eventSlot, gbc);
            eventSlots[i] = eventSlot;
        }
    }

    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusDays(direction);
        viewEndDate = viewStartDate;
        refreshView();
    }

    @Override
    public void renderEvents(List<Event> events) {
        // Clear existing events
        for (JLabel slot : eventSlots) {
            slot.setText("");
            slot.setBackground(Color.WHITE);
            for (MouseAdapter listener : slot.getListeners(MouseAdapter.class)) {
                slot.removeMouseListener(listener);
            }
        }

        // Render new events (only for those on the current day)
        for (Event event : events) {
            if (event.getStartDate().isEqual(viewStartDate)) {
                // Fetch detailed event info from the database using the provided SQL method.
                try {
                    ResultSet rs = getSQLConnection().getEventDetailsByEventId(event.getId());
                    if (rs != null && rs.next()) {
                        String eventName = rs.getString("name");
                        String venueName = rs.getString("venue_name");
                        // Optionally, you can fetch start and end times from the result set if needed:
                        // Time startTime = rs.getTime("start_time");
                        // Time endTime = rs.getTime("end_time");

                        // Determine the time slot index based on the event's start time.
                        int startHour = event.getStartTime().getHour();
                        int timeSlotIndex = -1;
                        for (int i = 0; i < times.length; i++) {
                            if (Integer.parseInt(times[i]) == startHour) {
                                timeSlotIndex = i;
                                break;
                            }
                        }
                        if (timeSlotIndex == -1) continue;

                        JLabel eventSlot = eventSlots[timeSlotIndex];
                        // Display only the event name and venue name.
                        eventSlot.setText(String.format("<html><center>%s<br/>%s</center></html>",
                                eventName, venueName));
                        eventSlot.setBackground(determineEventColor(event.getBookedBy()));
                        attachEventListeners(eventSlot, event);
                    }
                    if(rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Color determineEventColor(String bookedBy) {
        if (bookedBy.equalsIgnoreCase("operations")) {
            return new Color(200, 230, 255);  // Light blue for operations
        } else if (bookedBy.equalsIgnoreCase("marketing")) {
            return new Color(255, 230, 200);  // Light orange for marketing
        } else {
            return new Color(230, 255, 200);  // Default green for others
        }
    }

    /**
     * Attaches mouse listeners to the given event slot label.
     * On click, opens the EventDetailForm for the corresponding event.
     */
    private void attachEventListeners(JLabel cell, Event event) {
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
                        String.valueOf(event.getId())
                );
                eventDetailForm.setVisible(true);
            }
        });
    }

    @Override
    public void refreshView() {
        renderEvents(filterEvents(viewStartDate, viewEndDate));
    }

    /**
     * Override to provide the SQLConnection to use.
     * Adjust this method to return your actual SQLConnection instance.
     */
    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }
}
