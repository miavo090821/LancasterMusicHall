package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The HomePanel class represents the dashboard view of the Lancaster Music Hall management system.
 * It provides two main sections for managing reminders and notifications, with editing capabilities
 * for both sections.
 *
 * <p>Key features include:
 * <ul>
 *   <li>Reminders section with edit/save/undo functionality</li>
 *   <li>Notifications section with edit/save/undo functionality</li>
 *   <li>Dynamic font sizing that responds to system settings</li>
 *   <li>Consistent color scheme and styling</li>
 * </ul>
 *
 * <p>This panel integrates with the MainMenuGUI for font size management and file I/O operations.</p>
 *
 * @author Mysaeah
 * @version 1.0
 * @see MainMenuGUI
 */
public class HomePanel extends JPanel {
    /** File name for storing reminders */
    private static final String FILE_NAME = "reminders.txt";

    /** File name for storing notifications */
    private static final String NOTIFICATIONS_FILE = "notifications.txt";

    /** Current base font size for the panel */
    private int fontSize;

    // Color scheme constants
    /** Primary lavender color used for accents */
    private final Color PRIMARY_COLOR = new Color(200, 170, 250);

    /** Lighter lavender accent color */
    private final Color ACCENT_COLOR = new Color(230, 210, 250);

    /** Dark gray color for text */
    private final Color TEXT_COLOR = new Color(60, 60, 60);

    /** Light gray color for borders */
    private final Color BORDER_COLOR = new Color(220, 220, 220);

    // UI Components
    private JLabel titleLabel;
    private JTextArea textReminder, textNotification;
    private JButton editButton, saveButton, undoButton, notifyEditButton, notifySaveButton, notifyUndoButton, logoutButton;

    /**
     * Constructs a new HomePanel with reference to the main menu for configuration.
     *
     * @param mainMenu The parent MainMenuGUI that contains this panel
     */
    public HomePanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 500));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Section 1: Title Panel (NORTH)
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Section 2: Main Content Panel (CENTER)
        JPanel contentPanel = createContentPanel(mainMenu);
        add(contentPanel, BorderLayout.CENTER);

        // Section 3: Action Panel (SOUTH)
        JPanel actionPanel = createActionPanel(mainMenu);
        add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel with the dashboard heading.
     *
     * @return Configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    /**
     * Creates the main content panel containing reminders and notifications sections.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @return Configured content panel
     */
    private JPanel createContentPanel(MainMenuGUI mainMenu) {
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        contentPanel.add(createRemindersPanel(mainMenu));
        contentPanel.add(createNotificationsPanel(mainMenu));

        return contentPanel;
    }

    /**
     * Creates the reminders panel with editing capabilities.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @return Configured reminders panel
     */
    private JPanel createRemindersPanel(MainMenuGUI mainMenu) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Important Reminders"));

        textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        configureTextArea(textReminder);

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(createReminderButtonPanel(mainMenu), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the notifications panel with editing capabilities.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @return Configured notifications panel
     */
    private JPanel createNotificationsPanel(MainMenuGUI mainMenu) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(createStyledBorder("Notifications"));

        textNotification = new JTextArea(mainMenu.loadTextFromFile(NOTIFICATIONS_FILE));
        configureTextArea(textNotification);

        JScrollPane scrollPane = new JScrollPane(textNotification);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        panel.add(createNotificationButtonPanel(mainMenu), BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates a styled border with title for panels.
     *
     * @param title The title text for the border
     * @return Configured compound border
     */
    private Border createStyledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ),
                BorderFactory.createTitledBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, fontSize),
                        TEXT_COLOR
                )
        );
    }

    /**
     * Configures common properties for text areas.
     *
     * @param textArea The text area to configure
     */
    private void configureTextArea(JTextArea textArea) {
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
    }

    /**
     * Creates the button panel for the reminders section.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @return Configured button panel
     */
    private JPanel createReminderButtonPanel(MainMenuGUI mainMenu) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        editButton = createStyledButton("Edit", PRIMARY_COLOR, fontSize);
        saveButton = createStyledButton("Save", PRIMARY_COLOR, fontSize);
        undoButton = createStyledButton("Undo", BORDER_COLOR, fontSize);
        undoButton.setVisible(false);

        editButton.addActionListener(e -> enableEditing(textReminder, undoButton));
        saveButton.addActionListener(e -> saveContent(mainMenu, textReminder, undoButton, FILE_NAME));
        undoButton.addActionListener(e -> undoChanges(mainMenu, textReminder, undoButton, FILE_NAME));

        buttonPanel.add(undoButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        return buttonPanel;
    }

    /**
     * Creates the button panel for the notifications section.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @return Configured button panel
     */
    private JPanel createNotificationButtonPanel(MainMenuGUI mainMenu) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        notifyEditButton = createStyledButton("Edit", PRIMARY_COLOR, fontSize);
        notifySaveButton = createStyledButton("Save", PRIMARY_COLOR, fontSize);
        notifyUndoButton = createStyledButton("Undo", BORDER_COLOR, fontSize);
        notifyUndoButton.setVisible(false);

        notifyEditButton.addActionListener(e -> enableEditing(textNotification, notifyUndoButton));
        notifySaveButton.addActionListener(e -> saveContent(mainMenu, textNotification, notifyUndoButton, NOTIFICATIONS_FILE));
        notifyUndoButton.addActionListener(e -> undoChanges(mainMenu, textNotification, notifyUndoButton, NOTIFICATIONS_FILE));

        buttonPanel.add(notifyUndoButton);
        buttonPanel.add(notifyEditButton);
        buttonPanel.add(notifySaveButton);
        return buttonPanel;
    }

    /**
     * Enables editing mode for a text area.
     *
     * @param textArea The text area to enable editing for
     * @param undoButton The associated undo button to show
     */
    private void enableEditing(JTextArea textArea, JButton undoButton) {
        textArea.setEditable(true);
        textArea.setBackground(ACCENT_COLOR);
        undoButton.setVisible(true);
    }

    /**
     * Saves content to file and exits editing mode.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @param textArea The text area containing content to save
     * @param undoButton The associated undo button to hide
     * @param fileName The file name to save to
     */
    private void saveContent(MainMenuGUI mainMenu, JTextArea textArea, JButton undoButton, String fileName) {
        if (textArea.isEditable()) {
            mainMenu.saveTextToFile(textArea.getText(), fileName);
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
            undoButton.setVisible(false);
            JOptionPane.showMessageDialog(this, "Content saved successfully!");
        }
    }

    /**
     * Reverts changes and exits editing mode.
     *
     * @param mainMenu Reference to the main menu for file operations
     * @param textArea The text area to revert
     * @param undoButton The associated undo button to hide
     * @param fileName The file name to load original content from
     */
    private void undoChanges(MainMenuGUI mainMenu, JTextArea textArea, JButton undoButton, String fileName) {
        textArea.setText(mainMenu.loadTextFromFile(fileName));
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        undoButton.setVisible(false);
    }

    /**
     * Creates the action panel with logout button.
     *
     * @param mainMenu Reference to the main menu for logout functionality
     * @return Configured action panel
     */
    private JPanel createActionPanel(MainMenuGUI mainMenu) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

        logoutButton = createStyledButton("Log Out", PRIMARY_COLOR, fontSize);
        logoutButton.addActionListener(e -> mainMenu.logout());
        actionPanel.add(logoutButton);

        return actionPanel;
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
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Updates font sizes throughout the panel and its components.
     *
     * @param newFontSize The new font size to apply
     */
    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        // Update title font
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));

        // Update text areas
        textReminder.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        textNotification.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));

        // Update buttons
        Component[] buttons = {editButton, saveButton, undoButton,
                notifyEditButton, notifySaveButton, notifyUndoButton,
                logoutButton};
        for (Component button : buttons) {
            if (button != null) {
                button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            }
        }

        // Update titled borders
        updatePanelBorders(this);

        revalidate();
        repaint();
    }

    /**
     * Recursively updates titled borders in a panel hierarchy.
     *
     * @param panel The panel to update borders for
     */
    private void updatePanelBorders(JPanel panel) {
        Border border = panel.getBorder();
        if (border instanceof TitledBorder titledBorder) {
            titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }

        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                updatePanelBorders((JPanel) component);
            }
        }
    }
}