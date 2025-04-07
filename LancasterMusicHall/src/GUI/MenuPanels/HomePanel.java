package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private static final String FILE_NAME = "reminders.txt";
    private static final String NOTIFICATIONS_FILE = "notifications.txt";
    private int fontSize;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(200, 170, 250); // Lavender
    private final Color ACCENT_COLOR = new Color(230, 210, 250); // Lighter lavender
    private final Color TEXT_COLOR = new Color(60, 60, 60); // Dark gray for text
    private final Color BORDER_COLOR = new Color(220, 220, 220); // Light gray for borders

    // Components that need font size updates
    private JLabel titleLabel;
    private JTextArea textReminder, textNotification;
    private JButton editButton, saveButton, undoButton, notifyEditButton, notifySaveButton, notifyUndoButton, logoutButton;

    public HomePanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 500));
        setBackground(Color.WHITE);

        // Add subtle padding around the entire panel
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Section 1: Title Panel (NORTH) ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(Color.WHITE);

        titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4)); // Title is larger
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // === Section 2: Main Content Panel (CENTER) ===
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Left Panel - Reminders
        JPanel remindersPanel = createRemindersPanel(mainMenu);
        contentPanel.add(remindersPanel);

        // Right Panel - Notifications
        JPanel notificationsPanel = createNotificationsPanel(mainMenu);
        contentPanel.add(notificationsPanel);

        add(contentPanel, BorderLayout.CENTER);

        // === Section 3: Action Panel (SOUTH) ===
        JPanel actionPanel = createActionPanel(mainMenu);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createRemindersPanel(MainMenuGUI mainMenu) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Create a custom titled border
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                "Important Reminders",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, fontSize),
                TEXT_COLOR
        );
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(),
                titledBorder
        ));

        textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        textReminder.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        textReminder.setLineWrap(true);
        textReminder.setWrapStyleWord(true);
        textReminder.setEditable(false);
        textReminder.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action buttons specific to reminders
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        editButton = createStyledButton("Edit", PRIMARY_COLOR, fontSize);
        saveButton = createStyledButton("Save", PRIMARY_COLOR, fontSize);
        undoButton = createStyledButton("Undo", BORDER_COLOR, fontSize);
        undoButton.setVisible(false);

        editButton.addActionListener(e -> {
            textReminder.setEditable(true);
            textReminder.setBackground(ACCENT_COLOR);
            undoButton.setVisible(true);
        });

        saveButton.addActionListener(e -> {
            if (textReminder.isEditable()) {
                mainMenu.saveTextToFile(textReminder.getText(), FILE_NAME);
                textReminder.setEditable(false);
                textReminder.setBackground(Color.WHITE);
                undoButton.setVisible(false);
                JOptionPane.showMessageDialog(this, "Reminders saved successfully!");
            }
        });

        undoButton.addActionListener(e -> {
            textReminder.setText(mainMenu.loadTextFromFile(FILE_NAME));
            textReminder.setEditable(false);
            textReminder.setBackground(Color.WHITE);
            undoButton.setVisible(false);
        });

        buttonPanel.add(undoButton);
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createNotificationsPanel(MainMenuGUI mainMenu) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Create a custom titled border
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR),
                "Notifications",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, fontSize),
                TEXT_COLOR
        );
        titledBorder.setTitleJustification(TitledBorder.LEFT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                panel.getBorder(),
                titledBorder
        ));

        textNotification = new JTextArea(mainMenu.loadTextFromFile(NOTIFICATIONS_FILE));
        textNotification.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        textNotification.setLineWrap(true);
        textNotification.setWrapStyleWord(true);
        textNotification.setEditable(false);
        textNotification.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textNotification);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action buttons specific to notifications
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        notifyEditButton = createStyledButton("Edit", PRIMARY_COLOR, fontSize);
        notifySaveButton = createStyledButton("Save", PRIMARY_COLOR, fontSize);
        notifyUndoButton = createStyledButton("Undo", BORDER_COLOR, fontSize);
        notifyUndoButton.setVisible(false);

        notifyEditButton.addActionListener(e -> {
            textNotification.setEditable(true);
            textNotification.setBackground(ACCENT_COLOR);
            notifyUndoButton.setVisible(true);
        });

        notifySaveButton.addActionListener(e -> {
            if (textNotification.isEditable()) {
                mainMenu.saveTextToFile(textNotification.getText(), NOTIFICATIONS_FILE);
                textNotification.setEditable(false);
                textNotification.setBackground(Color.WHITE);
                notifyUndoButton.setVisible(false);
                JOptionPane.showMessageDialog(this, "Notifications saved successfully!");
            }
        });

        notifyUndoButton.addActionListener(e -> {
            textNotification.setText(mainMenu.loadTextFromFile(NOTIFICATIONS_FILE));
            textNotification.setEditable(false);
            textNotification.setBackground(Color.WHITE);
            notifyUndoButton.setVisible(false);
        });

        buttonPanel.add(notifyUndoButton);
        buttonPanel.add(notifyEditButton);
        buttonPanel.add(notifySaveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActionPanel(MainMenuGUI mainMenu) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 20, 0, 20));

        logoutButton = createStyledButton("Log Out", PRIMARY_COLOR, fontSize);
        logoutButton.addActionListener(e -> mainMenu.logout());
        actionPanel.add(logoutButton);

        return actionPanel;
    }

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

    // Method to update font sizes throughout the panel
    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        // Update title font
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));

        // Update text areas
        textReminder.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        textNotification.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));

        // Update buttons
        editButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        undoButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        notifyEditButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        notifySaveButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        notifyUndoButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, fontSize));

        // Update titled borders
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                updatePanelBorders((JPanel) component);
            }
        }

        revalidate();
        repaint();
    }

    private void updatePanelBorders(JPanel panel) {
        Border border = panel.getBorder();
        if (border instanceof TitledBorder titledBorder) {
            titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, fontSize));
        }

        // Recursively check child components
        for (Component component : panel.getComponents()) {
            if (component instanceof JPanel) {
                updatePanelBorders((JPanel) component);
            }
        }
    }
}