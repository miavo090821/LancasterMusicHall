package GUI;

import Database.SQLConnection;
import GUI.MenuPanels.*;
import GUI.MenuPanels.Booking.BookingPanel;
import GUI.MenuPanels.Calendar.CalendarPanel;
import GUI.MenuPanels.Event.EventPanel;
import GUI.MenuPanels.Reports.ReportPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class MainMenuGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuGUI::new);
    }
    public MainMenuGUI() {
        // Use 0 to represent a guest or not-logged-in staff.
        this(0, new SQLConnection());
    }

    private int staffId;
    private SQLConnection sqlConnection;

    // Track the currently selected navigation button.
    private JButton activeButton = null;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int fontSize;
//    private SQLConnection sqlConnection = new SQLConnection();

    public MainMenuGUI(int staffId, SQLConnection sqlConnection) {
        this.staffId = staffId;
        this.sqlConnection = sqlConnection;

        JFrame frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Create navigation bar.
        JPanel navBar = createNavBar();

        // Create main content area using CardLayout.
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create panels for different sections.
        CalendarPanel calendar = new CalendarPanel(this);
        EventPanel event = new EventPanel(this, cardLayout, cardPanel);
        ReportPanel report = new ReportPanel(this, cardLayout, cardPanel);
        HomePanel home = new HomePanel(this);
        BookingPanel booking = new BookingPanel(this);
        // Instantiate the diary panel using the updated constructor.
        DiaryPanel diary = new DiaryPanel(LocalDate.now(), new ArrayList<>(), sqlConnection);
        SettingsPanel settings = new SettingsPanel(this);

        // Add panels (cards) to the cardPanel.
        cardPanel.add(home, "Home");
        cardPanel.add(calendar, "Calendar");
        cardPanel.add(diary, "Diary");
        cardPanel.add(event, "VenueDetails");
        cardPanel.add(booking, "Booking");
        cardPanel.add(report, "Reports");
        cardPanel.add(settings, "Settings");

        // Add components to frame.
        frame.add(getTopPanel());
        frame.add(navBar);
        frame.add(cardPanel);

        cardLayout.show(cardPanel, "Home");

        frame.setVisible(true);
    }

    private JPanel createNavBar() {
        JPanel navigationTab = new JPanel();
        navigationTab.setPreferredSize(new Dimension(600, 70)); // Ensure consistent height.
        navigationTab.setBackground(Color.WHITE);

        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 6, 0, 0)); // 1 row, 6 columns.
        navBar.setPreferredSize(new Dimension(550, 50)); // Fixed width and height.
        navBar.setBackground(new Color(200, 170, 230)); // Purple background.
        navBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Full black border.

        String[] tabs = {"Home", "Calendar", "Diary", "Booking", "Reports", "Settings"};

        for (String tab : tabs) {
            JButton button = new JButton(tab);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setPreferredSize(new Dimension(100, 70)); // Match navBar height.

            // Default button styling.
            button.setBackground(new Color(170, 136, 200));
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setBorder(new EmptyBorder(0, 0, 0, 0)); // Remove extra padding.
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover and selection colors.
            Color defaultColor = new Color(170, 136, 200);
            Color hoverColor = new Color(210, 180, 255); // Light purple hover.
            Color borderColor = new Color(128, 0, 128); // Purple outline.

            // Hover effect.
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

            // Click action to switch tabs and highlight the active button.
            button.addActionListener(_ -> {
                if (activeButton != null) {
                    // Reset previous active button.
                    activeButton.setBackground(defaultColor);
                    activeButton.setBorder(BorderFactory.createEmptyBorder());
                }

                // Set new active button.
                activeButton = button;
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createLineBorder(borderColor, 2));

                // Switch to the selected panel.
                cardLayout.show(cardPanel, tab);
            });

            navBar.add(button);
        }

        navigationTab.add(navBar);
        return navigationTab;
    }

    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(600, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    public void saveTextToFile(String text, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
            System.out.println("Saved to: " + new File(fileName).getAbsolutePath()); // Debugging.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadTextFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        File file = new File(fileName);
        System.out.println("Trying to load from: " + file.getAbsolutePath()); // Debugging.
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

    public void stylizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Method to stylize dropdown menus.
    public void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font size.
        dropdown.setBackground(Color.WHITE); // Set background color.
        dropdown.setForeground(Color.BLACK); // Set text color.
        dropdown.setBorder(new LineBorder(Color.BLACK, 1)); // Add border.
    }

    public SQLConnection getSqlConnection() {
        return sqlConnection;
    }

    public void logout() {
        // Implement logout functionality if needed.
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int newFontSize) {
        fontSize = newFontSize;
    }
}
