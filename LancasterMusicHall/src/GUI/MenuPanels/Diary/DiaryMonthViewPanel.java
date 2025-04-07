package GUI.MenuPanels.Diary;

import Database.SQLConnection;
import GUI.MenuPanels.Calendar.CalendarViewPanel;
import GUI.MenuPanels.Calendar.MonthViewListener;
import operations.entities.Event;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DiaryMonthViewPanel extends CalendarViewPanel {
    private SQLConnection sqlCon;
    private DayCellPanel[][] dayCells;
    private LocalDate viewStartDate;
    private LocalDate viewEndDate;
    private final DateTimeFormatter monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    private List<Event> events;

    // The listener to notify when a day is clicked.
    private MonthViewListener listener;

    // Constructor now accepts MonthViewListener as well.
    public DiaryMonthViewPanel(LocalDate startDate, List<Event> events, SQLConnection sqlCon, MonthViewListener listener) {
        super(startDate, events, sqlCon);
        this.sqlCon = sqlCon;
        this.events = events;
        this.listener = listener;
        // Set view to the first day of the month.
        this.viewStartDate = startDate.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        initializeUI();
        renderEvents(null);
    }

    @Override
    protected SQLConnection getSQLConnection() {
        return sqlCon;
    }

    @Override
    public void setViewDate(LocalDate date) {
        this.viewStartDate = date.withDayOfMonth(1);
        this.viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        // Header panel displays the month name and a note.
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Month header label.
        JLabel monthHeader = new JLabel(viewStartDate.format(monthYearFormatter), SwingConstants.CENTER);
        monthHeader.setFont(new Font("Arial", Font.BOLD, 16));
        // Store the header label for later updates.
        this.putClientProperty("monthHeader", monthHeader);
        headerPanel.add(monthHeader, BorderLayout.NORTH);

        // Note label with instructions.
        JLabel noteLabel = new JLabel("Select any date to view that week's schedule", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        noteLabel.setForeground(Color.DARK_GRAY);
        headerPanel.add(noteLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Calendar grid in a scroll pane.
        JPanel gridPanel = new JPanel(new GridLayout(0, 7));
        gridPanel.setBackground(Color.WHITE);
        initializeMonthGrid(gridPanel);
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
    }

    private void initializeMonthGrid(JPanel gridPanel) {
        gridPanel.removeAll();

        // Day headers (week starts on Monday).
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            gridPanel.add(dayLabel);
        }

        YearMonth yearMonth = YearMonth.from(viewStartDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1; // Monday=0

        dayCells = new DayCellPanel[6][7];
        int totalCells = 42;
        int dayCounter = 1;

        for (int i = 0; i < totalCells; i++) {
            if (i < dayOfWeek || dayCounter > daysInMonth) {
                JPanel empty = new JPanel();
                empty.setBackground(Color.WHITE);
                gridPanel.add(empty);
            } else {
                LocalDate cellDate = viewStartDate.withDayOfMonth(dayCounter);
                DayCellPanel cell = new DayCellPanel(dayCounter, cellDate);
                // When the cell is clicked, notify the listener.
                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        onDayCellClicked(cell.getDate());
                    }
                });
                dayCells[i / 7][i % 7] = cell;
                gridPanel.add(cell);
                dayCounter++;
            }
        }
    }

    /**
     * Called when a day cell is clicked. It calls the listener's method.
     */
//    private void onDayCellClicked(LocalDate date) {
//        if (listener != null) {
//            listener.onDayCellClicked(date);
//        }
//    }

    @Override
    public void navigate(int direction) {
        viewStartDate = viewStartDate.plusMonths(direction).withDayOfMonth(1);
        viewEndDate = viewStartDate.plusMonths(1).minusDays(1);
        refreshView();
    }

    @Override
    public void renderEvents(List<Event> ignored) {
        if (dayCells == null) return;
        for (int row = 0; row < dayCells.length; row++) {
            for (int col = 0; col < dayCells[row].length; col++) {
                if (dayCells[row][col] != null) {
                    dayCells[row][col].clearEvents();
                }
            }
        }

        String query = "SELECT e.event_id, e.name, e.start_date, e.end_date, e.start_time, e.end_time, " +
                "e.`event_type`, e.description, e.booked_by, v.venue_name, b.booking_id " +
                "FROM Event e " +
                "LEFT JOIN Venue v ON e.venue_id = v.venue_id " +
                "JOIN Booking b ON e.booking_id = b.booking_id " +
                "WHERE e.start_date BETWEEN ? AND ? " +
                "AND b.booking_status = 'held' " +
                "ORDER BY e.start_date, e.start_time";

        try {
            PreparedStatement ps = getSQLConnection().getConnection().prepareStatement(query);
            ps.setDate(1, java.sql.Date.valueOf(viewStartDate));
            ps.setDate(2, java.sql.Date.valueOf(viewEndDate));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String eventName = rs.getString("name");
                String venueName = rs.getString("venue_name");
                LocalDate eventDate = rs.getDate("start_date").toLocalDate();

                int dayOfMonth = eventDate.getDayOfMonth();
                LocalDate firstOfMonth = viewStartDate.withDayOfMonth(1);
                int startDayOfWeek = firstOfMonth.getDayOfWeek().getValue() - 1;
                int cellIndex = startDayOfWeek + dayOfMonth - 1;
                int row = cellIndex / 7;
                int col = cellIndex % 7;
                if (dayCells[row][col] != null) {
                    dayCells[row][col].addEvent(String.format("%s (%s)", eventName, venueName));
                    dayCells[row][col].refresh();
                }
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        revalidate();
        repaint();
    }

    @Override
    public void refreshView() {
        JLabel monthHeader = (JLabel) this.getClientProperty("monthHeader");
        if (monthHeader != null) {
            monthHeader.setText(viewStartDate.format(monthYearFormatter));
        }
        JPanel gridPanel = (JPanel) ((JScrollPane) getComponent(1)).getViewport().getView();
        initializeMonthGrid(gridPanel);
        renderEvents(null);
        revalidate();
        repaint();
    }

    // ---------------------------------------------------------
    // Inner class: DayCellPanel
    // ---------------------------------------------------------
    /**
     * Called when a day cell is clicked.
     * It notifies the listener.
     */
    private void onDayCellClicked(LocalDate date) {
        if (listener != null) {
            listener.onDayCellClicked(date);
        }
    }

    // Inner class: DayCellPanel (unchanged except that it calls MonthViewPanel.this.onDayCellClicked)
    public class DayCellPanel extends JPanel {
        private JLabel dayLabel;
        private JPanel eventsPanel;
        private int dayNumber;
        private LocalDate date;

        public DayCellPanel(int dayNumber, LocalDate date) {
            this.dayNumber = dayNumber;
            this.date = date;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            setBackground(Color.WHITE);

            dayLabel = new JLabel(String.valueOf(dayNumber));
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            dayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            add(dayLabel, BorderLayout.NORTH);

            eventsPanel = new JPanel();
            eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
            eventsPanel.setBackground(Color.WHITE);
            JScrollPane eventsScroll = new JScrollPane(eventsPanel);
            eventsScroll.setBorder(null);
            add(eventsScroll, BorderLayout.CENTER);

            // Add recursive mouse listener so that clicks anywhere trigger navigation.
            addMouseListenerRecursively(this, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    DiaryMonthViewPanel.this.onDayCellClicked(getDate());
                }
            });
        }

        public LocalDate getDate() {
            return date;
        }

        public void addEvent(String eventText) {
            JLabel eventLabel = new JLabel(eventText);
            eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            eventLabel.setOpaque(true);
            eventLabel.setBackground(new Color(230, 255, 200));
            eventLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            eventsPanel.add(eventLabel);
        }

        public void clearEvents() {
            eventsPanel.removeAll();
        }

        public void refresh() {
            revalidate();
            repaint();
        }

        private void addMouseListenerRecursively(Component comp, java.awt.event.MouseListener listener) {
            comp.addMouseListener(listener);
            if (comp instanceof Container) {
                for (Component child : ((Container) comp).getComponents()) {
                    addMouseListenerRecursively(child, listener);
                }
            }
        }
    }
}
