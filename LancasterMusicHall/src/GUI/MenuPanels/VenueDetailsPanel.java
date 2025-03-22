package GUI.MenuPanels;

import GUI.MainMenuGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VenueDetailsPanel extends JPanel {
    private static final String FILE_NAME = "calender.txt";

    public VenueDetailsPanel(MainMenuGUI mainMenu) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 20, 10, 20));
        setBackground(Color.WHITE);

        // === Section 1: Text Area ===
        JTextArea textReminder = new JTextArea(mainMenu.loadTextFromFile(FILE_NAME));
        textReminder.setFont(new Font("Arial", Font.PLAIN, 16));
        textReminder.setLineWrap(true);
        textReminder.setWrapStyleWord(true);
        textReminder.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        textReminder.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textReminder);
        scrollPane.setPreferredSize(new Dimension(480, 270));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        JPanel textRemPanel = new JPanel(new BorderLayout());
        textRemPanel.setBackground(Color.WHITE);
        textRemPanel.add(scrollPane, BorderLayout.CENTER);

        // === Section 2: Bottom Panel ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        bottomPanel.setBackground(Color.WHITE);

        JPanel actionPanel = createActionPanel(mainMenu, textReminder);
        bottomPanel.add(actionPanel);

        // === Add to Main Panel ===
        add(textRemPanel);
        add(bottomPanel);
    }

    private static JPanel createActionPanel(MainMenuGUI mainMenu, JTextArea textReminder) {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton saveButton = new JButton("Save");
        JButton undoButton = new JButton("Undo");
        undoButton.setVisible(false);

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
