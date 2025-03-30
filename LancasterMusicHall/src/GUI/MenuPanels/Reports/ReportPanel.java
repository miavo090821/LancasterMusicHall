package GUI.MenuPanels.Reports;

import GUI.MainMenuGUI;
import GUI.MenuPanels.CalendarPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ReportPanel extends JPanel {
    public ReportPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
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
        textPanel.setPreferredSize(new Dimension(750, 600));
        textPanel.setBackground(Color.white); // Changed to white
        mainPanel.add(textPanel);


        // Main panel 1
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setPreferredSize(new Dimension(600, 170));
        panel1.setBackground(Color.white); // Changed to white
        textPanel.add(panel1);

        // Main panel 2
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 50,0));
        panel2.setPreferredSize(new Dimension(600, 170));
        panel2.setBackground(Color.white); // Changed to white
        textPanel.add(panel2);

        //Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setPreferredSize(new Dimension(600, 30));
        titlePanel.setBackground(Color.white); // Changed to white

        JLabel titleLabel = new JLabel("Please Select From the Options Below:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        // preview old reports panel and button
        JPanel pastReportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30,0));
        pastReportPanel.setPreferredSize(new Dimension(600, 30));
        pastReportPanel.setBackground(Color.white); // Changed to white

        JButton pastReportButton = new JButton("Preview Past Report");
        pastReportPanel.add(pastReportButton);

        // new report panel and button
        JPanel newReportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30,0));
        newReportPanel.setPreferredSize(new Dimension(600, 30));
        newReportPanel.setBackground(Color.white); // Changed to white
        panel1.add(newReportPanel);

        JButton newReportButton = new JButton("Generate New Report");
        newReportPanel.add(newReportButton);

        mainMenu.stylizeButton(pastReportButton);
        mainMenu.stylizeButton(newReportButton);

        //action listeners
        cardPanel.add(new NewReportPanel(), "Generate New Report");
        cardPanel.add(new PastReportPanel(), "Preview Past Report");

        pastReportButton.addActionListener(_ -> {cardLayout.show(cardPanel, "Preview Past Report");});
        newReportButton.addActionListener(_ -> {cardLayout.show(cardPanel, "Generate New Report");});

        panel1.add(titlePanel);
        panel1.add(pastReportPanel);
        panel1.add(newReportPanel);

        textPanel.add(panel2);


        // Bottom panel for buttons or additional elements
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.white); // Changed to white
        add(bottomPanel);

        mainPanel.add(bottomPanel);
    }

    // Method to stylize dropdown menus
    private void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size
        dropdown.setBackground(Color.white); // Set background color
        dropdown.setForeground(Color.BLACK); // Set text color
        dropdown.setBorder(new LineBorder(Color.BLACK, 1)); // Add border
    }
}
