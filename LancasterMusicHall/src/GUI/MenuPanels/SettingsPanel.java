package GUI.MenuPanels;

import GUI.MainMenuGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsPanel extends JPanel {
    private int fontSize;
    private MainMenuGUI mainMenu;
    private JLabel titleLabel, fontLabel, colourLabel, generalLabel, logoutLabel;

    public SettingsPanel(MainMenuGUI mainMenu) {
        this.mainMenu = mainMenu;
        fontSize = mainMenu.getFontSize();
        setPreferredSize(new Dimension(700, 350));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Main container panel
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mainPanel.setPreferredSize(new Dimension(700, 500));
        mainPanel.setBackground(Color.white);
        add(mainPanel);

        // Panel for text elements
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new LineBorder(Color.black));
        textPanel.setPreferredSize(new Dimension(750, 550));
        textPanel.setBackground(Color.white);
        mainPanel.add(textPanel);

        // Accessibility title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setPreferredSize(new Dimension(700, 30));
        titlePanel.setBackground(Color.white);
        textPanel.add(titlePanel);

        titleLabel = new JLabel("Accessibility");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        titlePanel.add(Box.createHorizontalStrut(480));
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titlePanel.add(logoutButton);

        // Font size panel
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        fontPanel.setPreferredSize(new Dimension(700, 30));
        fontPanel.add(Box.createHorizontalStrut(10));
        fontPanel.setBackground(Color.white);
        textPanel.add(fontPanel);

        fontLabel = new JLabel("Font Size:");
        fontLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        String[] fontSizes = {"10", "12", "14", "16", "18", "20"};
        JComboBox<String> fontSizeDropdown = new JComboBox<>(fontSizes);
        mainMenu.styleDropdown(fontSizeDropdown);
        fontSizeDropdown.setSelectedItem(String.valueOf(fontSize)); // Set current font size

        // Add ActionListener to the dropdown
        fontSizeDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int newFontSize = Integer.parseInt((String) fontSizeDropdown.getSelectedItem());
                updateFontSizes(newFontSize);
            }
        });

        fontPanel.add(fontLabel);
        fontPanel.add(fontSizeDropdown);

        // Colour blind filter panel
        JPanel colourPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        colourPanel.add(Box.createHorizontalStrut(10));
        colourPanel.setPreferredSize(new Dimension(700, 30));
        colourPanel.setBackground(Color.white);
        textPanel.add(colourPanel);

        colourLabel = new JLabel("Colour Blind Filters:");
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] colourFilters = {"Off", "Protanopia", "Deuteranopia", "Tritanopia"};
        JComboBox<String> colourDropdown = new JComboBox<>(colourFilters);
        mainMenu.styleDropdown(colourDropdown);
        colourPanel.add(colourLabel);
        colourPanel.add(colourDropdown);

        // General section panel
        JPanel generalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalPanel.setPreferredSize(new Dimension(700, 30));
        generalPanel.setBackground(Color.white);
        textPanel.add(generalPanel);

        generalLabel = new JLabel("General");
        generalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        generalLabel.setBorder(new EmptyBorder(0, 5, 20, 0));
        generalPanel.add(generalLabel);

        // Auto logout panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        logoutPanel.setPreferredSize(new Dimension(700, 30));
        logoutPanel.setBackground(Color.white);
        logoutPanel.add(Box.createHorizontalStrut(10));
        textPanel.add(logoutPanel);

        logoutLabel = new JLabel("Auto Logout:");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] logoutTimes = {"5 minutes", "10 minutes", "30 minutes", "1 hour"};
        JComboBox<String> logoutDropdown = new JComboBox<>(logoutTimes);
        logoutDropdown.setSelectedItem("10 minutes");
        mainMenu.styleDropdown(logoutDropdown);
        logoutPanel.add(logoutLabel);
        logoutPanel.add(logoutDropdown);

        //empty panel
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(700, 260));
        panel.setBackground(Color.white);
        textPanel.add(panel);

        // === Bottom Panel ===
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);

        // Add glue to both sides for centering
        bottomPanel.add(Box.createHorizontalGlue());

        // Left column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new FlowLayout(FlowLayout.CENTER));
        leftColumn.setBackground(Color.WHITE);
        leftColumn.setPreferredSize(new Dimension(260, 100));

        bottomPanel.add(leftColumn);

        // Middle column
        JPanel middle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
        middle.setBackground(Color.white);
        middle.setPreferredSize(new Dimension(260, 100));

        JButton saveButton = new JButton("Save Changes");
        mainMenu.stylizeButton(saveButton);
        middle.add(saveButton);
        bottomPanel.add(middle);

        // Right column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new FlowLayout(FlowLayout.RIGHT, 60, 40));
        rightColumn.setBackground(Color.white);
        rightColumn.setPreferredSize(new Dimension(260, 100));

        JButton revertButton = new JButton("Revert");
        mainMenu.stylizeButton(revertButton);
        rightColumn.add(revertButton);
        bottomPanel.add(rightColumn);

        bottomPanel.add(Box.createHorizontalGlue()); // Add glue to the other side

        mainPanel.add(bottomPanel);
    }

    private void updateFontSizes(int newFontSize) {
        // Update the font size of all relevant labels
        titleLabel.setFont(new Font("Arial", Font.BOLD, newFontSize + 6)); // Title is slightly larger
        fontLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));
        colourLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));
        generalLabel.setFont(new Font("Arial", Font.BOLD, newFontSize + 6)); // Title is slightly larger
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, newFontSize));

        // You may also want to update the font size of buttons and other components
        // For example:
        // logoutButton.setFont(new Font("Arial", Font.BOLD, newFontSize));

        // Store the new font size
        this.fontSize = newFontSize;
        mainMenu.setFontSize(newFontSize); // Update in MainMenuGUI if needed
    }

    private JPanel createFixedHeightPanel(int height) {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(700, height));
        panel.setMaximumSize(new Dimension(700, height));
        return panel;
    }
}