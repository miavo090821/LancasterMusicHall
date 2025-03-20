import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import operations.entities.*;

// Main class to run both Part 1 and Part 2
public class MainMenuGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuGUI::new);
    }
    private JPanel cardPanel;
    private CardLayout cardLayout;

    public MainMenuGUI() {
        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Create navigation bar
        JPanel navBar = createNavBar();

        // Create main content area using CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Add different sections (cards) to the panel
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(getCalendarPanel(), "Calendar");
        cardPanel.add(getCalenderDetailPanel(), "Diary");
        cardPanel.add(getReportsPanel(), "Reports");
        cardPanel.add(getSettingsPanel(), "Settings");

        // Add components to frame
        frame.add(getTopPanel());
        frame.add(navBar);
        frame.add(cardPanel);

        frame.setVisible(true);
    }

    private JPanel getSettingsPanel() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout());
        return navBar;
    }

    private JPanel getDiaryPanel() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout());
        return navBar;
    }

    private JPanel getReportsPanel() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout());

        return navBar;
    }

    private JPanel getCalendarPanel() {
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        mainPanel.setPreferredSize(new Dimension(800, 400)); // More width
        mainPanel.setBackground(Color.WHITE);

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH);
        LocalDate weekStart = LocalDate.of(2025, 3, 1); // Start of calendar week
        String[] days = new String[7];

        for (int i = 0; i < 7; i++) {
            days[i] = weekStart.plusDays(i).format(dayFormatter); // e.g., "Sat 1"
        }

        String[] times = {"10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};

        JPanel calendarPanel = new JPanel(new GridBagLayout());
        calendarPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // === Top-left corner (empty)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0;
        JLabel cornerLabel = new JLabel("");
        cornerLabel.setPreferredSize(new Dimension(30, 30)); // Small corner
        cornerLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        calendarPanel.add(cornerLabel, gbc);

        JLabel[][] calendarCells = new JLabel[times.length][days.length]; // rows x columns

        // === Day headers ===
        for (int i = 0; i < days.length; i++) {
            gbc.gridx = i + 1;
            gbc.gridy = 0;
            gbc.weightx = 0.5;
            JLabel dayLabel = new JLabel(days[i], SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            dayLabel.setOpaque(true);
            dayLabel.setPreferredSize(new Dimension(40, 30)); // Smaller header
            dayLabel.setBackground(new Color(220, 200, 255));
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(dayLabel, gbc);
        }

        // === Time labels and calendar cells ===
        for (int row = 0; row < times.length; row++) {
            // Time label (small column)
            gbc.gridx = 0;
            gbc.gridy = row + 1;
            gbc.weightx = 0.1;
            gbc.weighty = 0.5;
            JLabel timeLabel = new JLabel(times[row], SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 10));
            timeLabel.setOpaque(true);
            timeLabel.setPreferredSize(new Dimension(30, 60)); // Small time labels
            timeLabel.setBackground(Color.WHITE);
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            calendarPanel.add(timeLabel, gbc);

            // Calendar cells
            for (int col = 0; col < days.length; col++) {
                gbc.gridx = col + 1;
                gbc.gridy = row + 1;
                gbc.weightx = 0.5;
                gbc.weighty = 0.5;
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setPreferredSize(new Dimension(40, 30));
                cell.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                cell.setOpaque(true);
                cell.setBackground(Color.WHITE);
                calendarCells[row][col] = cell; // Store it!
                calendarPanel.add(cell, gbc);
            }
        }

        // === Booking Display Section ===
        ArrayList<Booking> bookings = getSampleBookings(); // Fetch or generate bookings
        renderBookings(bookings, calendarCells, days, times); // Apply bookings to calendar

        // Scroll if needed
        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setPreferredSize(new Dimension(580, 300)); // Smaller scroll area
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(scrollPane);

        // === Bottom Panel ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.WHITE);

        JButton newEventButton = new JButton("New Event");
        newEventButton.setFont(new Font("Arial", Font.BOLD, 12));
        newEventButton.setBackground(new Color(200, 170, 250));
        newEventButton.setPreferredSize(new Dimension(120, 30)); // Smaller button
        bottomPanel.add(newEventButton);

        mainPanel.add(bottomPanel);

        return mainPanel;
    }


    private void renderBookings(ArrayList<Booking> bookings, JLabel[][] calendarCells, String[] days, String[] times) {
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE d", Locale.ENGLISH); // e.g., "Mon 1"

        for (Booking booking : bookings) {
            LocalDate startDate = booking.getStartDate(); // Now LocalDate
            LocalDate endDate = booking.getEndDate();     // Now LocalDate

            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                String dayLabel = currentDate.format(dayFormatter); // e.g., "Thu 20"

                // === Find matching day column ===
                int colIndex = -1;
                for (int i = 0; i < days.length; i++) {
                    if (days[i].equals(dayLabel)) {
                        colIndex = i;
                        break;
                    }
                }

                if (colIndex != -1) {
                    LocalTime startTime = booking.getStartTime();
                    LocalTime endTime = booking.getEndTime();

                    int startHour = startTime.getHour();
                    int endHour = endTime.getHour();

                    int startRow = -1;
                    int endRow = -1;

                    System.out.println("Booking ID: " + booking.getId() + ", Day: " + dayLabel + ", Col: " + colIndex);
                    System.out.println("StartHour: " + startHour + ", EndHour: " + endHour + ", Rows: " + startRow + " to " + endRow);

                    // === Find matching time rows ===
                    for (int i = 0; i < times.length; i++) {
                        int timeSlotHour = Integer.parseInt(times[i]);
                        if (timeSlotHour == startHour) startRow = i;
                        if (timeSlotHour == endHour) endRow = i;
                    }

                    // === Render cells ===
                    if (startRow != -1 && endRow != -1 && startRow <= endRow) {
                        for (int row = startRow; row <= endRow; row++) {
                            JLabel cell = calendarCells[row][colIndex];
                            cell.setText("<html><center>" + booking.getActivityName() + "<br>" + booking.getVenue().getName() + "</center></html>");
                            cell.setBackground(new Color(200, 230, 255));
                            cell.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                        }
                    }
                }
                currentDate = currentDate.plusDays(1); // Next day
            }
        }
    }



    // === Sample bookings for demo ===
    private ArrayList<Booking> getSampleBookings() {
        ArrayList<Booking> bookings = new ArrayList<Booking>();

        // Sample Activity and Venue (replace with your real objects)
        Activity movieActivity = new Activity(1, "Movie A");
        Venue hallVenue = new Venue(1, "Main Hall", "Hall", 300);
        java.util.List<Seat> seats = List.of(
                new Seat('A', 1, Seat.Type.REGULAR, Seat.Status.AVAILABLE),
                new Seat('A', 2, Seat.Type.REGULAR, Seat.Status.AVAILABLE)
        );

        bookings.add(new Booking(
                101,
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 3, 3),
                LocalTime.of(10,20),
                LocalTime.of(12,20),
                movieActivity,
                hallVenue,
                false,
                null,
                seats
        ));
        bookings.add(new Booking(
                101,
                LocalDate.of(2025, 3, 2),
                LocalDate.of(2025, 3, 5),
                LocalTime.of(14,20),
                LocalTime.of(16,20),
                movieActivity,
                hallVenue,
                false,
                null,
                seats
        ));
        // Add more bookings as needed

        return bookings;
    }


    private JPanel getCalenderDetailPanel() {
        String fileName = "calender.txt";
        JPanel calenderPanel = new JPanel(new BorderLayout());
        calenderPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        calenderPanel.setLayout(new BoxLayout(calenderPanel, BoxLayout.Y_AXIS));

        // Section 1: Text Area
        JPanel textRemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        textRemPanel.setBackground(Color.WHITE);
        textRemPanel.setPreferredSize(new Dimension(600, 270));
        textRemPanel.setBackground(new Color(255, 255, 255))   ;

        JTextArea textReminder = new JTextArea(loadTextFromFile(fileName));  // Load saved text
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setPreferredSize(new Dimension(480, 270));
        textRemPanel.add(new JScrollPane(textReminder));

        // Section 2: Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(600, 40));

        String[] actions = {"Edit", "Delete", "Save"};

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setPreferredSize(new Dimension(400, 40));
        actionPanel.setBackground(Color.white);

        for (String action : actions) {
            JButton button = new JButton(action);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            actionPanel.add(button);

            if (action.equals("Edit")) {
                // Enable editing in JTextArea
                button.addActionListener(e -> {
                    textReminder.setEditable(true);
                    textReminder.setBackground(Color.YELLOW); // Highlight to show edit mode
                });
            } else if (action.equals("Delete")) {
                // Clear text with confirmation
                button.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(null,
                            "Are you sure you want to delete this text?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        textReminder.setText(""); // Clear text
                        saveTextToFile("", fileName); // Save empty file
                    }
                });
            } else if (action.equals("Save")) {
                // Save text to file
                button.addActionListener(e -> {
                    saveTextToFile(textReminder.getText(), fileName);
                    textReminder.setEditable(false); // Disable editing after saving
                    textReminder.setBackground(Color.WHITE); // Reset background
                    JOptionPane.showMessageDialog(null, "Text saved successfully!");
                });
            }
        }

        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(actionPanel);
        bottomPanel.add(logoutButton);

        // Add to main panel
        calenderPanel.add(textRemPanel);
        calenderPanel.add(bottomPanel);

        return calenderPanel;
    }

    private JPanel createHomePanel() {
        String fileName = "reminders.txt";
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));

        // Section 1: Label
        JPanel remindersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        remindersPanel.setPreferredSize(new Dimension(600, 30));
        remindersPanel.setBackground(Color.WHITE);
        JLabel remindersLabel = new JLabel("Important Reminders:");
        remindersLabel.setFont(new Font("Arial", Font.BOLD, 27));
        remindersPanel.add(remindersLabel);

        // Section 2: Text Area
        JPanel textRemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        textRemPanel.setBackground(Color.WHITE);
        JTextArea textReminder = new JTextArea(loadTextFromFile(fileName));  // Load saved text
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setPreferredSize(new Dimension(350, 200));
        textRemPanel.add(new JScrollPane(textReminder));

        // Section 3: Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 50, 0));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(600, 30));

        JButton button = new JButton("Log Out");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Save text before closing
        button.addActionListener(e -> saveTextToFile(textReminder.getText(), fileName));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255));
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        bottomPanel.add(button);

        // Add to main panel
        homePanel.add(remindersPanel);
        homePanel.add(textRemPanel);
        homePanel.add(bottomPanel);

        return homePanel;
    }

    // Create the navigation bar
    private JButton activeButton = null; // Track the currently selected button

    private JPanel createNavBar() {
        JPanel navigationTab = new JPanel();
        navigationTab.setPreferredSize(new Dimension(600, 70)); // Ensure consistent height
        navigationTab.setBackground(Color.WHITE);

        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 5, 0, 0)); // 1 row, 5 columns
        navBar.setPreferredSize(new Dimension(550, 60)); // Fixed width and height
        navBar.setBackground(new Color(200, 170, 230)); // Purple background
        navBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Full black border

        String[] tabs = {"Home", "Calendar", "Diary", "Reports", "Settings"};

        for (String tab : tabs) {
            JButton button = new JButton(tab);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setPreferredSize(new Dimension(100, 60)); // Match navBar height

            // Default button styling
            button.setBackground(new Color(170, 136, 200));
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove extra padding
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover and selection colors
            Color defaultColor = new Color(170, 136, 200);
            Color hoverColor = new Color(210, 180, 255); // Light purple hover
            Color borderColor = new Color(128, 0, 128); // Purple outline

            // Hover effect
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button != activeButton) {
                        button.setBackground(hoverColor);
                        button.setBorder(BorderFactory.createLineBorder(borderColor, 2));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (button != activeButton) {
                        button.setBackground(defaultColor);
                        button.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
            });

            // Click action to switch tabs and highlight the active button
            button.addActionListener(e -> {
                if (activeButton != null) {
                    // Reset previous active button
                    activeButton.setBackground(defaultColor);
                    activeButton.setBorder(BorderFactory.createEmptyBorder());
                }

                // Set new active button
                activeButton = button;
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createLineBorder(borderColor, 2));

                // Switch to the selected panel
                cardLayout.show(cardPanel, tab);
            });

            navBar.add(button);
        }

        navigationTab.add(navBar);
        return navigationTab;
    }

    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(600, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    private void saveTextToFile(String text, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
            System.out.println("Saved to: " + new File(fileName).getAbsolutePath()); // Debugging
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadTextFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        File file = new File(fileName);
        System.out.println("Trying to load from: " + file.getAbsolutePath()); // Debugging
        if (!file.exists()) {
            System.out.println("No saved file found: " + file.getAbsolutePath());
            return "";
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }
}