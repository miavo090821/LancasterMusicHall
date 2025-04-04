package GUI.MenuPanels.Calendar;
import GUI.MenuPanels.Calendar.DayViewPanel;
import Database.SQLConnection;
import operations.module.Event;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MonthViewPanel extends CalendarViewPanel {
    // Replace with your actual SQLConnection if needed.
    private static final SQLConnection sqlConnection = null;

    private DayCellPanel[][] dayCells;
    private LocalDate viewStartDate;
    private LocalDate viewEndDate;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private List<Event> events;

    public MonthViewPanel(LocalDate startDate, List<Event> events) {
        super(startDate, events, sqlConnection);
        this.events = events;
        // Set the view to the first day of the selected month
        this.viewStartDate = startDate.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        initializeUI();
    }

    @Override
    protected SQLConnection getSQLConnection() {
        return sqlConnection;
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Month header with formatted month and year
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

        // Add day headers (assuming week starts on Sunday)
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

        // Prepare a grid with a total of 42 cells (6 rows x 7 columns)
        dayCells = new DayCellPanel[6][7];
        int totalCells = 42;
        int dayCounter = 1;

        // Build each cell in the grid
        for (int i = 0; i < totalCells; i++) {
            if (i < dayOfWeek || dayCounter > daysInMonth) {
                // Add an empty cell for days outside the current month
                gridPanel.add(new JPanel());
            } else {
                // Create a DayCellPanel for this day
                DayCellPanel cell = new DayCellPanel(dayCounter);
                dayCells[i / 7][i % 7] = cell;
                gridPanel.add(cell);
                dayCounter++;
            }
        }
    }

    @Override
    public void navigate(int direction) {
        // Navigate to previous/next month
        viewStartDate = viewStartDate.plusMonths(direction).withDayOfMonth(1);
        viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    @Override
    public void renderEvents(List<Event> events) {
        // Clear existing events from each day cell
        if (dayCells == null) return;
        for (int row = 0; row < dayCells.length; row++) {
            for (int col = 0; col < dayCells[row].length; col++) {
                if (dayCells[row][col] != null) {
                    dayCells[row][col].clearEvents();
                }
            }
        }

        // Render events into the corresponding day cell based on the event's start date
        for (Event event : events) {
            LocalDate eventDate = event.getStartDate();
            if (!eventDate.isBefore(viewStartDate) && !eventDate.isAfter(viewEndDate)) {
                int dayOfMonth = eventDate.getDayOfMonth();
                LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
                int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
                int cellIndex = startDayOfWeek + dayOfMonth - 1;
                int row = cellIndex / 7;
                int col = cellIndex % 7;
                if (dayCells[row][col] != null) {
                    // Add only the event's name
                    dayCells[row][col].addEvent(event.getName());
                    dayCells[row][col].refresh();
                }
            }
        }
    }

    @Override
    public void refreshView() {
        // Get the grid panel from the JScrollPane and rebuild the grid
        JPanel gridPanel = (JPanel)((JScrollPane)getComponent(1)).getViewport().getView();
        initializeMonthGrid(gridPanel);
        // Filter and render events that fall within the view's date range
        renderEvents(filterEvents(viewStartDate, viewEndDate));
        revalidate();
        repaint();
    }
}
