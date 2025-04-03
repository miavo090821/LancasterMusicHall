package GUI.MenuPanels.Calendar;

import operations.module.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WeekViewPanel extends CalendarViewPanel {
    private JLabel[][] calendarCells;
    private String[] days;
    private final String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"};
    private final DateTimeFormatter dayHeaderFormatter = DateTimeFormatter.ofPattern("EEEE d");

    public WeekViewPanel(LocalDate startDate, List<Event> events) {
        super(startDate, events);
        this.viewStartDate = startDate.with(DayOfWeek.MONDAY);
        this.viewEndDate = viewStartDate.plusDays(6);
        initializeUI();
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.with(DayOfWeek.MONDAY);
        this.viewEndDate = viewStartDate.plusDays(6);
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Main grid panel
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setPreferredSize(new Dimension(550, 400));
        gridPanel.setBackground(Color.WHITE);

        initializeWeekGrid(gridPanel);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    private void initializeWeekGrid(JPanel gridPanel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Update day headers with current dates
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = viewStartDate.plusDays(i).format(dayHeaderFormatter);
        }

        // Top-left corner spacer
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.1; gbc.weighty = 0;
        gridPanel.add(new JLabel(""), gbc);

        // Day headers
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1; gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = createDayHeaderLabel(days[i]);
            gridPanel.add(dayLabel, gbc);
        }

        // Initialize calendar cells
        calendarCells = new JLabel[times.length][days.length];

        // Time slots and event cells
        for (int row = 0; row < times.length; row++) {
            // Time label column
            gbc.gridx = 0; gbc.gridy = row + 1;
            gbc.weightx = 0.1; gbc.ipady = 40;
            gridPanel.add(createTimeLabel(times[row]), gbc);

            // Day cells
            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1; gbc.gridy = row + 1;
                gbc.weightx = 0.5; gbc.weighty = 0.5; gbc.ipady = 40;
                calendarCells[row][col] = createCalendarCell();
                gridPanel.add(calendarCells[row][col], gbc);
            }
        }
    }

    private JLabel createDayHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(80, 30));
        label.setBackground(new Color(220, 200, 255));
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return label;
    }

    private JLabel createTimeLabel(String time) {
        JLabel label = new JLabel(time, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        label.setOpaque(true);
        label.setPreferredSize(new Dimension(30, 60));
        label.setBackground(Color.WHITE);
        label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return label;
    }

    private JLabel createCalendarCell() {
        JLabel cell = new JLabel("", SwingConstants.CENTER);
        cell.setPreferredSize(new Dimension(80, 30));
        cell.setFont(new Font("Arial", Font.BOLD, 10));
        cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        cell.setOpaque(true);
        cell.setBackground(Color.WHITE);
        return cell;
    }

    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusWeeks(direction);
        viewEndDate = viewStartDate.plusDays(6);
        refreshView();
    }

    @Override
    public void renderEvents(List<Event> events) {
        resetWeekGrid();

        for (Event event : events) {
            int dayColumn = (int)(event.getStartDate().toEpochDay() - viewStartDate.toEpochDay());
            int startRow = findTimeSlot(event.getStartTime().getHour());
            int endRow = findTimeSlot(event.getEndTime().getHour());

            if (dayColumn >= 0 && dayColumn < 7 && startRow != -1 && endRow != -1) {
                Color eventColor = determineEventColor(event.getBookedBy());

                for (int row = startRow; row <= endRow; row++) {
                    JLabel cell = calendarCells[row][dayColumn];
                    if (row == startRow) {
                        String displayText = String.format("<html><center>%s<br/><small>%s</small></center></html>",
                                event.getName(), event.getBookedBy());
                        cell.setText(displayText);
                    }
                    styleEventCell(cell, eventColor, row == startRow, row == endRow);
                    attachEventListeners(cell, event, eventColor);
                }
            }
        }
    }

    private void resetWeekGrid() {
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                JLabel cell = calendarCells[row][col];
                cell.setText("");
                cell.setBackground(Color.WHITE);
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                // Remove existing listeners
                for (MouseListener l : cell.getMouseListeners()) {
                    cell.removeMouseListener(l);
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
            return new Color(230, 255, 200);  // Default green for other departments
        }
    }

    private void styleEventCell(JLabel cell, Color color, boolean isFirstRow, boolean isLastRow) {
        cell.setBackground(color);
        cell.setOpaque(true);
        cell.setBorder(BorderFactory.createMatteBorder(
                isFirstRow ? 1 : 0, 1, isLastRow ? 1 : 0, 1, Color.BLACK
        ));
    }

    private void attachEventListeners(JLabel cell, Event event, Color baseColor) {
        cell.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                cell.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                cell.setBackground(baseColor);
            }
        });
    }

    private int findTimeSlot(int hour) {
        for (int i = 0; i < times.length; i++) {
            if (Integer.parseInt(times[i]) == hour) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void refreshView() {
        renderEvents(filterEvents(viewStartDate, viewEndDate));
    }
}