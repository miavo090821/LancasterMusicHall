package GUI.MenuPanels.Reports;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

/**
 * The ReportPanel class provides the main interface for accessing different report types
 * in the venue management system. It serves as a navigation hub for:
 * <ul>
 *   <li>Generating new reports</li>
 *   <li>Viewing past reports</li>
 *   <li>Accessing daily event sheets</li>
 * </ul>
 *
 * <p>The panel features a consistent color scheme and responsive layout that matches
 * the application's design system.</p>
 *
 * @see MainMenuGUI
 * @see NewReportPanel
 * @see PastReportPanel
 * @see DailyEventsPanel
 * @since 1.2
 */
public class ReportPanel extends JPanel {
    // Color scheme matching other panels
    /** Primary lavender color used for buttons and accents */
    private final Color PRIMARY_COLOR = new Color(207, 185, 255);

    /** Dark gray color for text and labels */
    private final Color TEXT_COLOR = new Color(60, 60, 60);

    /** Black color for borders and separators */
    private final Color BORDER_COLOR = new Color(0, 0, 0);

    /**
     * Constructs a new ReportPanel with navigation capabilities.
     *
     * @param mainMenu The parent MainMenuGUI that contains this panel
     * @param cardLayout The CardLayout used for panel navigation
     * @param cardPanel The container panel for report sub-panels
     */
    public ReportPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = createContentPanel();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize and register report sub-panels
        initializeReportCards(mainMenu, cardLayout, cardPanel);
    }

    /**
     * Creates the title panel with the main heading.
     *
     * @return Configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    /**
     * Creates the main content panel with report navigation buttons.
     *
     * @return Configured content panel
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Instruction label
        JLabel instructionLabel = new JLabel("Please select from the options below:");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPanel.add(instructionLabel);

        // Add report navigation buttons
        contentPanel.add(createButtonPanel());

        return contentPanel;
    }

    /**
     * Creates the panel containing report navigation buttons.
     *
     * @return Configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        int buttonGap = 15; // Vertical spacing between buttons

        // Past Reports button
        JButton pastReportButton = createNavigationButton("Preview Past Reports", buttonPanel, buttonGap);

        // New Report button
        JButton newReportButton = createNavigationButton("Generate New Report", buttonPanel, buttonGap);

        // Daily Events button
        createNavigationButton("Daily Events Sheet", buttonPanel, 0); // No gap after last button

        return buttonPanel;
    }

    /**
     * Creates and configures a navigation button.
     *
     * @param text The button text
     * @param parentPanel The panel to add the button to
     * @param gap The vertical gap to add after the button
     * @return The created JButton
     */
    private JButton createNavigationButton(String text, JPanel parentPanel, int gap) {
        JButton button = createStyledButton(text, PRIMARY_COLOR, 18);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(300, 40));
        parentPanel.add(button);
        if (gap > 0) {
            parentPanel.add(Box.createVerticalStrut(gap));
        }
        return button;
    }

    /**
     * Initializes and registers all report sub-panels with the card layout.
     *
     * @param mainMenu The parent MainMenuGUI
     * @param cardLayout The CardLayout for navigation
     * @param cardPanel The container panel
     */
    private void initializeReportCards(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        cardPanel.add(new NewReportPanel(mainMenu), "Generate New Report");
        cardPanel.add(new PastReportPanel(mainMenu), "Preview Past Reports");
        cardPanel.add(new DailyEventsPanel(mainMenu.getSqlConnection()), "Daily Events Sheet");
    }

    /**
     * Creates a styled button with consistent appearance and hover effects.
     *
     * @param text The button text
     * @param color The base color for the button
     * @param fontSize The font size for the button text
     * @return Configured JButton
     */
    private JButton createStyledButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Updates font sizes throughout the panel to maintain visual consistency.
     *
     * @param newFontSize The new base font size to apply
     */
    public void updateFontSizes(int newFontSize) {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                updateLabelFont((JLabel) component, newFontSize);
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, newFontSize));
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Updates the font of a label based on its role.
     *
     * @param label The label to update
     * @param fontSize The new base font size
     */
    private void updateLabelFont(JLabel label, int fontSize) {
        if (label.getText().equals("Reports")) {
            label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        } else {
            label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        }
    }
}