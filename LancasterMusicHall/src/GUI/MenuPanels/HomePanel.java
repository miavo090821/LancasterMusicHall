package GUI.MenuPanels;

import GUI.MainMenuGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomePanel extends JPanel {
    private static final String FILE_NAME = "reminders.txt";

    public HomePanel(MainMenuGUI mainMenu) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setBackground(Color.WHITE);

        // === Section 1: Title ===
        JLabel remindersLabel = new JLabel("Important Reminders:");
        remindersLabel.setFont(new Font("Arial", Font.BOLD, 27));

        JPanel remindersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        remindersPanel.setBackground(Color.WHITE);
        remindersPanel.add(remindersLabel);

        // === Section 2: Text Area ===
        JTextArea textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        textReminder.setPreferredSize(new Dimension(550, 280));
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setLineWrap(true);
        textReminder.setWrapStyleWord(true);
        textReminder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        textReminder.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(350, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel textRemPanel = new JPanel(new BorderLayout());
        textRemPanel.setBackground(Color.WHITE);
        textRemPanel.add(scrollPane, BorderLayout.CENTER);

        // === Section 3: Action Panel ===
        JPanel actionPanel = createActionPanel(mainMenu, textReminder);

        // === Add Sections ===
        add(remindersPanel);
        add(textRemPanel);
        add(actionPanel);
    }

    private JPanel createActionPanel(MainMenuGUI mainMenu, JTextArea textReminder) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        actionPanel.setPreferredSize(new Dimension(600, 60));
        actionPanel.setBackground(Color.WHITE);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton undoButton = new JButton("Undo");
        undoButton.setVisible(false); // Hidden by default

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
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                textReminder.setText("");
            }
        });

        // === Save Action ===
        saveButton.addActionListener(e -> {
            if (textReminder.isEditable()) {
                mainMenu.saveTextToFile(textReminder.getText(), FILE_NAME);
                textReminder.setEditable(false);
                textReminder.setBackground(Color.WHITE);
                JOptionPane.showMessageDialog(null, "Text saved successfully!");
            }
        });

        // === Undo Action ===
        undoButton.addActionListener(e -> {
            textReminder.setText(mainMenu.loadTextFromFile(FILE_NAME));
            textReminder.setEditable(false);
            textReminder.setBackground(Color.white);

        });
        mainMenu.stylizeButton(editButton);
        mainMenu.stylizeButton(deleteButton);
        mainMenu.stylizeButton(saveButton);
        mainMenu.stylizeButton(undoButton);

        // Add buttons
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(saveButton);
        actionPanel.add(undoButton);

        actionPanel.add(Box.createHorizontalStrut(180));
        JButton logoutButton = new JButton("Log Out");
        mainMenu.stylizeButton(logoutButton);
        actionPanel.add(logoutButton);

        return actionPanel;
    }
}
