package GUI.MenuPanels.Reports;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    // Color scheme matching other panels
    private final Color PRIMARY_COLOR = new Color(207, 185, 255); // Lavender
    private final Color TEXT_COLOR = new Color(60, 60, 60); // Dark gray for text
    private final Color BORDER_COLOR = new Color(0, 0, 0); // Light gray for borders

    public ReportPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Instruction label
        JLabel instructionLabel = new JLabel("Please select from the options below:");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPanel.add(instructionLabel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add vertical space between buttons
        int buttonGap = 15;

        // Past Reports button
        JButton pastReportButton = createStyledButton("Preview Past Reports", PRIMARY_COLOR, 18);
        pastReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        pastReportButton.setMaximumSize(new Dimension(300, 40));
        buttonPanel.add(pastReportButton);
        buttonPanel.add(Box.createVerticalStrut(buttonGap));

        // New Report button
        JButton newReportButton = createStyledButton("Generate New Report", PRIMARY_COLOR, 18);
        newReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        newReportButton.setMaximumSize(new Dimension(300, 40));
        buttonPanel.add(newReportButton);
        buttonPanel.add(Box.createVerticalStrut(buttonGap));

        // Daily Events button (new third button)
        JButton dailyEventsButton = createStyledButton("Daily Events Sheet", PRIMARY_COLOR, 18);
        dailyEventsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyEventsButton.setMaximumSize(new Dimension(300, 40));
        buttonPanel.add(dailyEventsButton);

        contentPanel.add(buttonPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // === Card Panel Setup ===
        cardPanel.add(new NewReportPanel(mainMenu), "Generate New Report");
        cardPanel.add(new PastReportPanel(mainMenu), "Preview Past Reports");
        cardPanel.add(new DailyEventsPanel(mainMenu.getSqlConnection()), "Daily Events Sheet");

        // Action listeners
        pastReportButton.addActionListener(_ -> cardLayout.show(cardPanel, "Preview Past Reports"));
        newReportButton.addActionListener(_ -> cardLayout.show(cardPanel, "Generate New Report"));
        dailyEventsButton.addActionListener(_ -> cardLayout.show(cardPanel, "Daily Events Sheet"));
    }

    private JButton createStyledButton(String text, Color color, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        button.setBackground(color);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
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

    public void updateFontSizes(int newFontSize) {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().equals("Reports")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, newFontSize + 4));
                } else {
                    label.setFont(new Font("Segoe UI", Font.PLAIN, newFontSize));
                }
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, newFontSize));
            }
        }
        revalidate();
        repaint();
    }
}