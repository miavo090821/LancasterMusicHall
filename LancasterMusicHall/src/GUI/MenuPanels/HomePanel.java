package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private static final String FILE_NAME = "reminders.txt";

    public HomePanel(MainMenuGUI mainMenu) {
        setLayout(new BorderLayout()); // Changed to BorderLayout for better control
        setPreferredSize(new Dimension(600, 350));
        setBackground(Color.WHITE);

        // === Section 1: Title Panel (NORTH) ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(Color.WHITE);

        JLabel remindersLabel = new JLabel("Important Reminders:");
        remindersLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titlePanel.add(remindersLabel);

        add(titlePanel, BorderLayout.NORTH);

        // === Section 2: Main Content Panel (CENTER) ===
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(10, 20, 10, 20)); // Add some padding

        // Text Area with Scroll
        JTextArea textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setLineWrap(true);
        textReminder.setWrapStyleWord(true);
        textReminder.setEditable(false);
        textReminder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(550, 200));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // === Section 3: Action Panel (SOUTH) ===
        JPanel actionPanel = createActionPanel(mainMenu, textReminder);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createActionPanel(MainMenuGUI mainMenu, JTextArea textReminder) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(0, 20, 10, 20)); // Add some padding

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton undoButton = new JButton("Undo");
        undoButton.setVisible(false);

        // Button actions (same as before)
        editButton.addActionListener(e -> {
            textReminder.setEditable(true);
            textReminder.setBackground(Color.YELLOW);
            undoButton.setVisible(true);
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this text?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                textReminder.setText("");
            }
        });

        saveButton.addActionListener(e -> {
            if (textReminder.isEditable()) {
                mainMenu.saveTextToFile(textReminder.getText(), FILE_NAME);
                textReminder.setEditable(false);
                textReminder.setBackground(Color.WHITE);
                JOptionPane.showMessageDialog(null, "Text saved successfully!");
            }
        });

        undoButton.addActionListener(e -> {
            textReminder.setText(mainMenu.loadTextFromFile(FILE_NAME));
            textReminder.setEditable(false);
            textReminder.setBackground(Color.WHITE);
            undoButton.setVisible(false);
        });

        // Style buttons
        mainMenu.stylizeButton(editButton);
        mainMenu.stylizeButton(deleteButton);
        mainMenu.stylizeButton(saveButton);
        mainMenu.stylizeButton(undoButton);

        // Add buttons
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(saveButton);
        actionPanel.add(undoButton);

        // Add some space
        actionPanel.add(Box.createHorizontalStrut(100));

        JButton logoutButton = new JButton("Log Out");
        mainMenu.stylizeButton(logoutButton);
        actionPanel.add(logoutButton);

        return actionPanel;
    }
}