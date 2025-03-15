import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        frame.setSize(600, 500);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Create navigation bar
        JPanel navBar = createNavBar();

        // Create main content area using CardLayout
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

//        frame.add(getTopPanel());
//        frame.add(getMainContainer());
//        frame.add(getBottomPanel());

        // Add different sections (cards) to the panel
        cardPanel.add(createHomePanel(), "Home");
        cardPanel.add(getCalenderPanel(), "Calendar");
        cardPanel.add(getDiaryPanel(), "Diary");
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

    private JPanel getCalenderPanel() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new FlowLayout());
        return navBar;
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        homePanel.setLayout(new BoxLayout(homePanel, BoxLayout.Y_AXIS));

        // Log Out Button
//        JButton logOutButton = new JButton("Log Out");
//        logOutButton.addActionListener(e -> System.exit(0));
//        homePanel.add(logOutButton, BorderLayout.SOUTH);

        // Important Reminders Section
        homePanel.add(getMainSection1());
        homePanel.add(getMainSection2());
        homePanel.add(getBottomPanel());

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
            Color selectedColor = hoverColor; // Same as hover for selected

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
                button.setBackground(selectedColor);
                button.setBorder(BorderFactory.createLineBorder(borderColor, 2));

                // Switch to the selected panel
                cardLayout.show(cardPanel, tab);
            });

            navBar.add(button);
        }

        navigationTab.add(navBar);
        return navigationTab;
    }



    /**
     * function to write the title
     * */
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(600, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    /**
     * function to write section 1:
     * */
    private JPanel getMainSection1() {
        JPanel remindersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,50,25));
        remindersPanel.setBackground(new Color(255, 255, 255)); // colouring the background
        remindersPanel.setPreferredSize(new Dimension(600, 30));

        JLabel remindersLabel = new JLabel("Important Reminders:");
        remindersLabel.setFont(new Font("Arial", Font.BOLD, 27));

        remindersPanel.add((remindersLabel), BorderLayout.NORTH);

        return remindersPanel;
    }

    /**
     * function to write section 2
     * */
    private JPanel getMainSection2() {
        JPanel textRemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,50,0));
        textRemPanel.setPreferredSize(new Dimension(600, 140));
        textRemPanel.setBackground(new Color(255, 255, 255)); // colouring the background

        JTextArea textReminder = new JTextArea("- TheatreCo contract deadline\n- 11:00 CinemaLTD Booking");
        textReminder.setEditable(true);
        textReminder.setPreferredSize(new Dimension(350, 180));
        textRemPanel.add(new JScrollPane(textReminder), BorderLayout.CENTER);

        return textRemPanel;
    }

    /**
     * Bottom panel has one part:
     *        1. Bottom Section
     * */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,50,25));
        bottomPanel.setPreferredSize(new Dimension(600, 30));
        bottomPanel.setBackground(new Color(255, 255, 255)); // colouring the background

        JButton button = new JButton("Log Out");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.WHITE);
        button.setBorderPainted(true);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(50, 150, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        bottomPanel.add(button);

        return bottomPanel;
    }
}



