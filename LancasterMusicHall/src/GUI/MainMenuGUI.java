package GUI;

import Database.SQLConnection;
import GUI.MenuPanels.*;
import GUI.MenuPanels.Booking.BookingPanel;
import GUI.MenuPanels.Calendar.CalendarPanel;
import GUI.MenuPanels.Diary.DiaryPanel;
import GUI.MenuPanels.Event.EventPanel;
import GUI.MenuPanels.Reports.ReportPanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


/**
 * The MainMenuGUI class represents the main navigation interface for the Lancaster Music Hall application.
 * It provides access to all major features through a tabbed navigation system.
 *
 * <p>This class manages the main application window and coordinates between different panels
 * (Home, Calendar, Diary, Booking, Reports, Reviews, and Settings) using a CardLayout.</p>
 *
 * @author Mysarah
 * @version 1.2
 * @see SQLConnection
 * @see CardLayout
 */

public class MainMenuGUI {
    /**
     * Entry point for standalone testing of the MainMenuGUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenuGUI::new);
    }

    /** The SQL connection used throughout the application */
    private SQLConnection sqlConnection;

    /** Currently selected navigation button */
    private JButton activeButton = null;

    /** Panel that holds all the cards for different views */
    private JPanel cardPanel;

    /** Layout manager for switching between views */
    private CardLayout cardLayout;

    /** Base font size for the application (can be adjusted in settings) */
    private int fontSize = 18;

    /** The main application frame */
    private JFrame frame;

    /**
     * Default constructor for guest access (staffId = 0).
     * Initializes the GUI with a new SQL connection.
     */
    public MainMenuGUI() {
        // Use 0 to represent a guest or not-logged-in staff.
        this(0, new SQLConnection());
    }

    /**
     * Primary constructor for the MainMenuGUI.
     *
     * @param staffId The ID of the logged-in staff member (0 for guest)
     * @param sqlConnection The SQL connection to use for database operations
     */
    public MainMenuGUI(int staffId, SQLConnection sqlConnection) {
        this.sqlConnection = sqlConnection;

        frame = new JFrame("Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
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
        DiaryPanel diary = new DiaryPanel(this, sqlConnection);
        SettingsPanel settings = new SettingsPanel(this);
        ReviewsPanel reviews = new ReviewsPanel(sqlConnection);

        // Add panels (cards) to the cardPanel.
        cardPanel.add(home, "Home");
        cardPanel.add(calendar, "Calendar");
        cardPanel.add(diary, "Diary");
        cardPanel.add(event, "VenueDetails");
        cardPanel.add(reviews, "Reviews");
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

    /**
     * Creates the navigation bar with tabs for different application sections.
     *
     * @return JPanel containing the navigation buttons
     */
    private JPanel createNavBar() {
        JPanel navigationTab = new JPanel();
        navigationTab.setPreferredSize(new Dimension(600, 100));
        navigationTab.setBackground(Color.white);

        JPanel navBar = new JPanel();
        navBar.setLayout(new GridLayout(1, 6, 0, 0));
        navBar.setPreferredSize(new Dimension(550, 50));
        navBar.setBackground(new Color(200, 170, 230));
        navBar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        String[] tabs = {"Home", "Calendar", "Diary", "Booking" ,"Reports", "Reviews", "Settings"};

        for (String tab : tabs) {
            JButton button = new JButton(tab);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setPreferredSize(new Dimension(100, 70));

            // Default button styling.
            button.setBackground(new Color(170, 136, 200));
            button.setFocusPainted(false);
            button.setContentAreaFilled(true);
            button.setBorder(new EmptyBorder(0, 0, 0, 0));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Hover and selection colors.
            Color defaultColor = new Color(170, 136, 200);
            Color hoverColor = new Color(210, 180, 255);
            Color borderColor = new Color(128, 0, 128);

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
                    activeButton.setBackground(defaultColor);
                    activeButton.setBorder(BorderFactory.createEmptyBorder());
                }

                activeButton = button;
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createLineBorder(borderColor, 2));

                cardLayout.show(cardPanel, tab);
            });

            navBar.add(button);
        }

        navigationTab.add(navBar);
        return navigationTab;
    }

    /**
     * Creates the top panel with the application title.
     *
     * @return JPanel containing the title label
     */
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(600, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    /**
     * Saves text content to a file.
     *
     * @param text The content to save
     * @param fileName The name/path of the file to save to
     */
    public void saveTextToFile(String text, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(text);
            System.out.println("Saved to: " + new File(fileName).getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Loads text content from a file.
     *
     * @param fileName The name/path of the file to load
     * @return The content of the file, or empty string if file doesn't exist
     */
    public String loadTextFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        File file = new File(fileName);
        System.out.println("Trying to load from: " + file.getAbsolutePath());
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

    /**
     * Applies standard styling to a button.
     *
     * @param button The button to stylize
     */
    public void stylizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    /**
     * Applies standard styling to a dropdown menu.
     *
     * @param dropdown The JComboBox to stylize
     */
    public void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        dropdown.setBackground(Color.WHITE);
        dropdown.setForeground(Color.BLACK);
        dropdown.setBorder(new LineBorder(Color.BLACK, 1));
    }


    /**
     * Gets the SQL connection used by this application.
     *
     * @return The SQLConnection instance
     */
    public SQLConnection getSqlConnection() {
        return sqlConnection;
    }

    /**
     * Logs out the current user and returns to the login screen.
     */
    public void logout() {
        new StaffLoginGUI();
        frame.dispose();
    }

    /**
     * Gets the current base font size for the application.
     *
     * @return The current font size
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Sets a new base font size for the application.
     *
     * @param newFontSize The new font size to use
     */
    public void setFontSize(int newFontSize) {
        fontSize = newFontSize;
    }
}