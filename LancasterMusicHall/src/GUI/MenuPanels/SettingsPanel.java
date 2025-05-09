package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The {@code SettingsPanel} class represents the settings menu panel of the application.
 * It is a Swing panel used in the application's main menu for configuring user preferences
 * like accessibility options and general settings.
 */
public class SettingsPanel extends JPanel {
    /**
     * Current base font size for UI elements in the application.
     * Value can be modified through settings.
     */
    private int fontSize;

    // Color scheme matching HomePanel
    /**
     * Primary accent color (lavender) used for main UI elements.
     * RGB values: (200, 170, 250)
     */
    private final Color PRIMARY_COLOR = new Color(200, 170, 250);

    /**
     * Text color for all readable content (dark gray).
     * RGB values: (60, 60, 60)
     */
    private final Color TEXT_COLOR = new Color(60, 60, 60);

    /**
     * Border color for UI component outlines (black).
     * RGB values: (0, 0, 0)
     * Note: Originally intended as light gray but currently set to black
     */
    private final Color BORDER_COLOR = new Color(0, 0, 0);

    /**
     * Background color for buttons (light gray).
     * RGB values: (220, 220, 220)
     * Note: Comment incorrectly states this is for borders - actually used for buttons
     */
    private final Color BUTTON_COLOR = new Color(220, 220, 220);

    /**
     * Constructs a new {@code SettingsPanel} with the specified main menu reference.
     *
     * @param mainMenu The instance of {@link MainMenuGUI} that manages the main menu and font size settings.
     */
    public SettingsPanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton logoutButton = createStyledButton("Log Out", PRIMARY_COLOR, fontSize);
        logoutButton.addActionListener(e -> mainMenu.logout());
        titlePanel.add(logoutButton, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Accessibility Section
        contentPanel.add(createSectionPanel("Accessibility", new String[]{
                "Font Size:",
                "Colour Blind Filters:"
        }, new String[][]{
                {"10", "12", "14", "16", "18", "20"},
                {"Off", "Protanopia", "Deuteranopia", "Tritanopia"}
        }, fontSize));

        // General Section
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createSectionPanel("General", new String[]{
                "Auto Logout:"
        }, new String[][]{
                {"5 minutes", "10 minutes", "30 minutes", "1 hour"}
        }, fontSize));

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // === Action Buttons ===
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton revertButton = createStyledButton("Revert", BUTTON_COLOR, fontSize);
        JButton saveButton = createStyledButton("Save Changes", PRIMARY_COLOR, fontSize);

        actionPanel.add(revertButton);
        actionPanel.add(saveButton);
        add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a section panel for the settings that includes a title and rows of label/options pairs.
     *
     * @param title    The title of the section.
     * @param labels   An array of labels for each setting in the section.
     * @param options  A two-dimensional array where each sub-array contains the options for the corresponding setting.
     * @param fontSize The font size to apply to the text components.
     * @return A {@code JPanel} containing the section's title and the settings rows.
     */
    private JPanel createSectionPanel(String title, String[] labels, String[][] options, int fontSize) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Section title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 2));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel);

        // Add each setting row
        for (int i = 0; i < labels.length; i++) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            label.setForeground(TEXT_COLOR);
            rowPanel.add(label);

            JComboBox<String> comboBox = new JComboBox<>(options[i]);
            styleComboBox(comboBox, fontSize);
            rowPanel.add(comboBox);

            panel.add(rowPanel);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    /**
     * Styles the provided {@code JComboBox} with the specified font size and default visual settings.
     *
     * @param comboBox The {@code JComboBox} to be styled.
     * @param fontSize The font size to be applied.
     */
    private void styleComboBox(JComboBox<String> comboBox, int fontSize) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        comboBox.setMaximumSize(new Dimension(200, 30));
    }

    /**
     * Creates a styled {@code JButton} with the given text, base color, and font size.
     * The button includes a hover effect.
     *
     * @param text     The text to display on the button.
     * @param color    The base background color for the button.
     * @param fontSize The font size to be applied to the button text.
     * @return A {@code JButton} with applied style and hover behavior.
     */
    private JButton createStyledButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            /**
             * Invoked when the mouse enters the button area.
             *
             * @param evt The mouse event.
             */
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            /**
             * Invoked when the mouse exits the button area.
             *
             * @param evt The mouse event.
             */
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Updates the font sizes for all components in the panel to match the new font size.
     *
     * @param newFontSize The new font size to be applied.
     */
    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        // Update all components that need font size changes
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Recursively updates the font for all components within the given panel based on the new font size.
     *
     * @param panel The {@code JPanel} whose components will have their fonts updated.
     */
    private void updatePanelFonts(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().equals("Settings")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
                } else if (label.getText().equals("Accessibility") || label.getText().equals("General")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 2));
                } else {
                    label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
                }
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            } else if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }
    }
}
