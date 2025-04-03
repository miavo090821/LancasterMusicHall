package GUI.MenuPanels.Calendar;

import operations.module.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MonthViewPanel extends CalendarViewPanel {
    private JLabel[][] dayCells;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

    public MonthViewPanel(LocalDate startDate, List<Event> events) {
        super(startDate, events);
        this.viewStartDate = startDate.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        initializeUI();
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Month header
        JLabel monthHeader = new JLabel(viewStartDate.format(monthYearFormatter), SwingConstants.CENTER);
        monthHeader.setFont(new Font("Arial", Font.BOLD, 16));
        add(monthHeader, BorderLayout.NORTH);

        // Calendar grid
        JPanel gridPanel = new JPanel(new GridLayout(0, 7)); // 7 columns for days of week
        gridPanel.setBackground(Color.WHITE);

        initializeMonthGrid(gridPanel);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    private void initializeMonthGrid(JPanel gridPanel) {
        // Clear existing components
        gridPanel.removeAll();

        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            gridPanel.add(dayLabel);
        }

        // Calculate days in month
        YearMonth yearMonth = YearMonth.from(viewStartDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Convert to 0-6 (Sun-Sat)

        // Initialize day cells
        dayCells = new JLabel[6][7]; // 6 rows x 7 columns

        // Fill leading empty days
        for (int i = 0; i < dayOfWeek; i++) {
            gridPanel.add(new JLabel(""));
        }

        // Fill days of month
        int day = 1;
        for (int row = 0; row < 6 && day <= daysInMonth; row++) {
            for (int col = 0; col < 7 && day <= daysInMonth; col++) {
                if (row == 0 && col < dayOfWeek) {
                    continue; // Skip cells before first day
                }

                JLabel dayCell = new JLabel(String.valueOf(day), SwingConstants.CENTER);
                dayCell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                dayCell.setOpaque(true);
                dayCell.setBackground(Color.WHITE);
                dayCells[row][col] = dayCell;
                gridPanel.add(dayCell);
                day++;
            }
        }
    }

    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusMonths(direction);
        viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    @Override
    public void renderEvents(List<Event> events) {
        // Clear existing events
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                if (dayCells[row][col] != null) {
                    dayCells[row][col].setText("");
                    dayCells[row][col].setBackground(Color.WHITE);
                }
            }
        }

        // Render events
        for (Event event : events) {
            if (!event.getStartDate().isBefore(viewStartDate) &&
                    !event.getStartDate().isAfter(viewEndDate)) {

                int dayOfMonth = event.getStartDate().getDayOfMonth();
                int firstDayOfWeek = viewStartDate.getDayOfWeek().getValue() % 7;
                int cellIndex = firstDayOfWeek + dayOfMonth - 1;
                int row = cellIndex / 7;
                int col = cellIndex % 7;

                if (dayCells[row][col] != null) {
                    JLabel cell = dayCells[row][col];
                    cell.setText(String.valueOf(dayOfMonth));
                    cell.setBackground(determineEventColor(event.getBookedBy()));
                    cell.setToolTipText(event.getName() + " (" + event.getStartTime() + ")");
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

    @Override
    public void refreshView() {
        initializeMonthGrid((JPanel)((JScrollPane)getComponent(1)).getViewport().getView());
        renderEvents(filterEvents(viewStartDate, viewEndDate));
    }
}