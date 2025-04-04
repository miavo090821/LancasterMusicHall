package GUI.MenuPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
import GUI.NewEventForm;
import com.toedter.calendar.JDateChooser;
import operations.entities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiaryPanel extends JPanel {
    private SQLConnection sqlConnection;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JLabel[][] calendarCells;
    private String[] days;
    private String[] times;

    private final LocalDate weekStart = LocalDate.of(2025, 3, 1);
    private final LocalDate weekEnd = weekStart.plusDays(6);

    public DiaryPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.cardPanel = cardPanel;
        this.cardLayout = cardLayout;
        this.sqlConnection = mainMenu.getSqlConnection();

        setLayout(new BorderLayout());
        setBackground(new Color(200, 170, 230));

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
        days = new String[7];
        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter);
        }

        times = new String[]{"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

        JPanel calendarPanel = new JPanel(new GridBagLayout());
        calendarPanel.setPreferredSize(new Dimension(550, 400));
        calendarPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Top-left corner (empty)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(30, 30));
        cornerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        calendarPanel.add(cornerLabel, gbc);

        calendarCells = new JLabel[times.length][days.length];

        // Day headers
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(40, 30));
            dayLabel.setBackground(new Color(220, 200, 255));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(dayLabel, gbc);
        }

        // Time labels and calendar cells
        for (int row = 0; row < times.length; row++) {
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gbc.weightx = 0.1;
            gbc.ipady = 40;
            JLabel timeLabel = new JLabel(times[row], SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setOpaque(true);
            timeLabel.setPreferredSize(new Dimension(30, 60));
            timeLabel.setBackground(Color.WHITE);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(timeLabel, gbc);

            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gbc.weightx = 0.5;
                gbc.weighty = 0.5;
                gbc.ipady = 40;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                calendarCells[row][col] = cell;
                calendarPanel.add(cell, gbc);
            }
        }

        // Booking Display Section:
        // Comment out temporary sample data. Now we fetch data from SQL.
        // ArrayList<Booking> bookings = getSampleBookings(); // [Commented out]
        ArrayList<Booking> bookings = new ArrayList<>(sqlConnection.fetchDiaryBookings(weekStart, weekEnd));
        renderBookings(bookings, calendarCells, days, times);

        // Scroll pane and centering container
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(new Dimension(580, 360));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel centerContainer = new JPanel(new GridBagLayout());
        centerContainer.setBackground(Color.WHITE);
        centerContainer.add(scrollPane);
        add(centerContainer, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(Box.createHorizontalGlue());

        // Left column (View and Type dropdowns)
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setBackground(Color.WHITE);
        JPanel left1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        left1.setBackground(Color.WHITE);
        left1.setPreferredSize(new Dimension(180, 50));
        JPanel left2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        left2.setBackground(Color.WHITE);
        left2.setPreferredSize(new Dimension(180, 50));
        JLabel viewLabel = new JLabel("View:");
        viewLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] calendarViews = {"Day View", "Week View", "Month View", "Overview"};
        JComboBox<String> viewDropdown = new JComboBox<>(calendarViews);
        mainMenu.styleDropdown(viewDropdown);
        viewDropdown.setSelectedItem("Week View");
        viewDropdown.addActionListener(e -> {
            String selected = (String)viewDropdown.getSelectedItem();
            // Switch calendar view logic...
        });
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] displayModes = {"Standard", "Compact", "Detailed", "Planning"};
        JComboBox<String> displayModeDropdown = new JComboBox<>(displayModes);
        mainMenu.styleDropdown(displayModeDropdown);
        displayModeDropdown.setSelectedItem("Standard");
        left1.add(viewLabel);
        left1.add(viewDropdown);
        left2.add(typeLabel);
        left2.add(displayModeDropdown);
        leftColumn.add(left1);
        leftColumn.add(left2);
        bottomPanel.add(leftColumn);

        // Middle column: New Draft button
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 25));
        middle.setBackground(Color.WHITE);
        middle.setPreferredSize(new Dimension(240, 100));

        JButton newEventButton = new JButton("New Draft");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 16));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 50));
        newEventButton.addActionListener(e -> {
            // Create and show the NewEventForm dialog
            NewEventForm newEventForm = new NewEventForm(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    sqlConnection
            );
            newEventForm.setVisible(true);

            // Refresh the calendar after the dialog closes
            newEventForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    refreshCalendar();
                }
            });
        });

        middle.add(newEventButton);
        bottomPanel.add(middle);

        // Right column: Date Picker
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setBackground(Color.WHITE);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 35));
        right.setBackground(Color.WHITE);
        right.setPreferredSize(new Dimension(180, 50));
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JDateChooser datePicker = new JDateChooser();
        datePicker.setDateFormatString("dd-MM-yyyy");
        datePicker.setPreferredSize(new Dimension(100, 25));
        datePicker.setDate(new Date());
        right.add(dateLabel);
        right.add(datePicker);
        rightColumn.add(right);
        bottomPanel.add(rightColumn);
        bottomPanel.add(Box.createHorizontalGlue());

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // Refresh the calendar by fetching data from SQL.
    public void refreshCalendar() {
        ArrayList<Booking> bookings = new ArrayList<>(sqlConnection.fetchDiaryBookings(weekStart, weekEnd));
        renderBookings(bookings, calendarCells, days, times);
        revalidate();
        repaint();
    }

    // Render bookings into the calendar grid.
    public void renderBookings(ArrayList<Booking> bookings, JLabel[][] calendarCells, String[] days, String[] times) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
        // Clear previous cell content.
        for (int row = 0; row < times.length; row++) {
            for (int col = 0; col < days.length; col++) {
                calendarCells[row][col].setText("");
                calendarCells[row][col].setBackground(Color.WHITE);
                calendarCells[row][col].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }
        // Render each booking.
        for (int bIndex = 0; bIndex < bookings.size(); bIndex++) {
            Booking booking = bookings.get(bIndex);
            LocalDate bStartDate = booking.getStartDate();
            LocalDate bEndDate = booking.getEndDate();
            LocalTime bStartTime = booking.getStartTime();
            LocalTime bEndTime = booking.getEndTime();
            int startHour = bStartTime.getHour();
            int endHour = bEndTime.getHour();
            String startDayLabel = bStartDate.format(dayFormatter);
            String endDayLabel = bEndDate.format(dayFormatter);

            int startCol = -1, endCol = -1;
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(startDayLabel)) startCol = i;
                if (days[i].equals(endDayLabel)) endCol = i;
            }
            if (startCol == -1 || endCol == -1 || startCol > endCol) continue;

            int startRow = -1, endRow = -1;
            for (int i = 0; i < times.length; i++) {
                int timeSlotHour = Integer.parseInt(times[i]);
                if (timeSlotHour == startHour) startRow = i;
                if (timeSlotHour == endHour) endRow = i;
            }
            if (startRow == -1 || endRow == -1 || startRow > endRow) continue;

            Color bookingColor = bookingColors[bIndex % bookingColors.length];
            // Merge cells for the booking.
            for (int row = startRow; row <= endRow; row++) {
                for (int col = startCol; col <= endCol; col++) {
                    JLabel cell = calendarCells[row][col];
                    if (row == startRow && col == startCol) {
                        cell.setText("<html><center>" + booking.getActivityName() + "</center></html>");
                    } else {
                        cell.setText("");
                    }
                    cell.setBackground(bookingColor);
                    cell.setOpaque(true);
                    cell.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    boolean isTop = (row == startRow);
                    boolean isBottom = (row == endRow);
                    boolean isLeft = (col == startCol);
                    boolean isRight = (col == endCol);
                    cell.setBorder(BorderFactory.createMatteBorder(
                            isTop ? 2 : 0,
                            isLeft ? 2 : 0,
                            isBottom ? 2 : 0,
                            isRight ? 2 : 0,
                            Color.black
                    ));
                    cell.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // When clicked, fetch full event details from SQL.
                            Booking fullBooking = sqlConnection.fetchEventDetails(booking.getId());
                            if (fullBooking != null) {
                                JOptionPane.showMessageDialog(DiaryPanel.this, "Event Details:\n" + fullBooking.toString());
                            } else {
                                JOptionPane.showMessageDialog(DiaryPanel.this, "Event details not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            cell.setBackground(bookingColor.darker());
                        }
                        @Override
                        public void mouseExited(MouseEvent e) {
                            cell.setBackground(bookingColor);
                        }
                    });
                }
            }
        }
    }

    // The following getSampleBookings() method is for testing purposes and is now commented out.
    /*
    private ArrayList<Booking> getSampleBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        Activity movieActivity1 = new Activity(1, "Movie A");
        Activity movieActivity2 = new Activity(1, "Movie B");
        Venue hallVenue = new Venue(1, "Main Hall", "Hall", 300);
        List<Seat> seats = List.of(
            new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
            new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
        );
        String bookedBy = "Operations";
        String primaryContact = "phone";
        String telephone = "073323523";
        String email = "CinemaLtd@gmail.com";
        ContactDetails contactDetails = new ContactDetails(primaryContact, telephone, email);
        String room = "Hall";
        String companyName = "Cinema Ltd";
        bookings.add(new Booking(
            101,
            LocalDate.of(2025, 3, 1),
            LocalDate.of(2025, 3, 3),
            LocalTime.of(10,20),
            LocalTime.of(12,20),
            movieActivity1,
            hallVenue,
            true,
            "",
            seats,
            bookedBy,
            room,
            companyName,
            contactDetails
        ));
        bookings.add(new Booking(
            102,
            LocalDate.of(2025, 3, 2),
            LocalDate.of(2025, 3, 5),
            LocalTime.of(14,20),
            LocalTime.of(16,20),
            movieActivity2,
            hallVenue,
            true,
            "",
            seats,
            bookedBy,
            room,
            companyName,
            contactDetails
        ));
        bookings.add(new Booking(
            103,
            LocalDate.of(2025, 3, 4),
            LocalDate.of(2025, 3, 6),
            LocalTime.of(17,2),
            LocalTime.of(20,20),
            movieActivity2,
            hallVenue,
            true,
            "",
            seats,
            bookedBy,
            room,
            companyName,
            contactDetails
        ));
        return bookings;
    }
    */

    private void showBookingDetails(Booking booking) {
        Booking fullBooking = sqlConnection.fetchEventDetails(booking.getId());
        if (fullBooking != null) {
            JOptionPane.showMessageDialog(this, "Event Details:\n" + fullBooking.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Event details not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final Color[] bookingColors = {
            new Color(200, 230, 255),
            new Color(255, 230, 200),
            new Color(230, 255, 200),
            new Color(255, 200, 230),
            new Color(230, 200, 255),
            new Color(200, 255, 230),
            new Color(255, 255, 200)
    };
}
