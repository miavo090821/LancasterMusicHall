package GUI.MenuPanels;

import GUI.MainMenuGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(MainMenuGUI mainMenu) {
        setPreferredSize(new Dimension(600, 350));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Main container panel
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        mainPanel.setPreferredSize(new Dimension(600, 350));
        mainPanel.setBackground(Color.white); // Changed to white
        add(mainPanel);

        // Panel for text elements
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new LineBorder(Color.black));
        textPanel.setPreferredSize(new Dimension(550, 360));
        textPanel.setBackground(Color.white); // Changed to white
        mainPanel.add(textPanel);

        // Accessibility title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setPreferredSize(new Dimension(600, 30));
        titlePanel.setBackground(Color.white); // Changed to white
        textPanel.add(titlePanel);

        JLabel titleLabel = new JLabel("Accessibility");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        titlePanel.add(Box.createHorizontalStrut(280));
        JButton logoutButton = new JButton("Log Out");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titlePanel.add(logoutButton);

        // Font size panel
        JPanel fontPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        fontPanel.setPreferredSize(new Dimension(600, 30));
        fontPanel.add(Box.createHorizontalStrut(10));
        fontPanel.setBackground(Color.white); // Changed to white
        textPanel.add(fontPanel);

        JLabel fontLabel = new JLabel("Font Size:");
        fontLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        String[] fontSizes = {"10", "12", "14", "16", "18", "20"};
        JComboBox<String> fontSizeDropdown = new JComboBox<>(fontSizes);
        mainMenu.styleDropdown(fontSizeDropdown);
        fontSizeDropdown.setSelectedItem("12"); // Default selection
        fontPanel.add(fontLabel);
        fontPanel.add(fontSizeDropdown);

        // Colour blind filter panel
        JPanel colourPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        colourPanel.add(Box.createHorizontalStrut(10));
        colourPanel.setPreferredSize(new Dimension(600, 30));
        colourPanel.setBackground(Color.white); // Changed to white
        textPanel.add(colourPanel);

        JLabel colourLabel = new JLabel("Colour Blind Filters:");
        colourLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] colourFilters = {"Off", "Protanopia", "Deuteranopia", "Tritanopia"};
        JComboBox<String> colourDropdown = new JComboBox<>(colourFilters);
        mainMenu.styleDropdown(colourDropdown);
        colourPanel.add(colourLabel);
        colourPanel.add(colourDropdown);

        // General section panel
        JPanel generalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        generalPanel.setPreferredSize(new Dimension(600, 30));
        generalPanel.setBackground(Color.white); // Changed to white
        textPanel.add(generalPanel);

        JLabel generalLabel = new JLabel("General");
        generalLabel.setFont(new Font("Arial", Font.BOLD, 24));
        generalLabel.setBorder(new EmptyBorder(0, 5, 20, 0));
        generalPanel.add(generalLabel);

        // Auto logout panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,0));
        logoutPanel.setPreferredSize(new Dimension(600, 30));
        logoutPanel.setBackground(Color.white); // Changed to white
        logoutPanel.add(Box.createHorizontalStrut(10));
        textPanel.add(logoutPanel);

        JLabel logoutLabel = new JLabel("Auto Logout:");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        String[] logoutTimes = {"5 minutes", "10 minutes", "30 minutes", "1 hour"};
        JComboBox<String> logoutDropdown = new JComboBox<>(logoutTimes);
        logoutDropdown.setSelectedItem("10 minutes");
        mainMenu.styleDropdown(logoutDropdown);
        logoutPanel.add(logoutLabel);
        logoutPanel.add(logoutDropdown);

        // Bottom panel for buttons or additional elements
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.white); // Changed to white
        add(bottomPanel);

        String[] actions = {"Revert", "Save Changes"};

        for (String action : actions) {
            JButton button = new JButton(action);
            mainMenu.stylizeButton(button);

            bottomPanel.add(button);

//            if (action.equals("Revert")) {
//            } else if (action.equals("Save Changes")) {
//
//                });
//            }
        }
        mainPanel.add(bottomPanel);
    }


}
