package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VenueDetailsPanel extends JPanel {
    private static final String FILE_NAME = "calender.txt";

    public VenueDetailsPanel(MainMenuGUI mainMenu) {
        setLayout(new BorderLayout()); // Changed to BorderLayout for better organization
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setBackground(Color.WHITE);

        // === Section 1: Title Panel (NORTH) ===
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(Color.WHITE);

        JLabel venueDetailsLabel = new JLabel("Venue Details & Calendar:");
        venueDetailsLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titlePanel.add(venueDetailsLabel);

        add(titlePanel, BorderLayout.NORTH);

        // === Section 2: Main Content Panel (CENTER) ===
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Text Area with Scroll
        JTextArea textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setLineWrap(true);
        textReminder.setWrapStyleWord(true);
        textReminder.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(550, 250));

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // === Section 3: Action Panel (SOUTH) ===
        JPanel actionPanel = createActionPanel(mainMenu, textReminder);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private JPanel createActionPanel(MainMenuGUI mainMenu, JTextArea textReminder) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(10, 0, 0, 0)); // Add some top padding

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton undoButton = new JButton("Undo");
        undoButton.setVisible(false);

        // Style buttons
        mainMenu.stylizeButton(editButton);
        mainMenu.stylizeButton(deleteButton);
        mainMenu.stylizeButton(saveButton);
        mainMenu.stylizeButton(undoButton);

        // === Edit Action ===
        editButton.addActionListener(e -> {
            textReminder.setEditable(true);
            textReminder.setBackground(Color.YELLOW);
            undoButton.setVisible(true);
        });

        // === Delete Action ===
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to delete this text?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                textReminder.setText("");
                mainMenu.saveTextToFile("", FILE_NAME);
            }
        });

        // === Save Action ===
        saveButton.addActionListener(e -> {
            if (textReminder.isEditable()) {
                mainMenu.saveTextToFile(textReminder.getText(), FILE_NAME);
                textReminder.setEditable(false);
                textReminder.setBackground(Color.WHITE);
                undoButton.setVisible(false);
                JOptionPane.showMessageDialog(null, "Text saved successfully!");
            }
        });

        // === Undo Action ===
        undoButton.addActionListener(e -> {
            textReminder.setText(mainMenu.loadTextFromFile(FILE_NAME));
            textReminder.setEditable(false);
            textReminder.setBackground(Color.WHITE);
            undoButton.setVisible(false);
        });

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(saveButton);
        actionPanel.add(undoButton);

        return actionPanel;
    }
}