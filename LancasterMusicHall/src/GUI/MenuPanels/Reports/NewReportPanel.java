package GUI.MenuPanels.Reports;

import Database.SQLConnection;
import Database.SQLConnection.ReportData;
import GUI.MainMenuGUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class NewReportPanel extends JPanel {
    // Color scheme matching other panels
    private final Color PRIMARY_COLOR = new Color(200, 170, 250); // Lavender
    private final Color ACCENT_COLOR = new Color(230, 210, 250); // Lighter lavender
    private final Color TEXT_COLOR = new Color(60, 60, 60); // Dark gray for text
    private final Color BORDER_COLOR = new Color(0, 0, 0); // Black for borders

    private SQLConnection sqlCon;
    private int fontSize = 16;

    public NewReportPanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        this.sqlCon = new SQLConnection();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Generate New Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
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
        JLabel instructionLabel = new JLabel("Select report type to generate:");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        instructionLabel.setForeground(TEXT_COLOR);
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        instructionLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        contentPanel.add(instructionLabel);

        // Report type buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Daily Report
        JPanel dailyPanel = createReportOptionPanel("Daily Report:",
                e -> generateReport("daily"));
        buttonsPanel.add(dailyPanel);
        buttonsPanel.add(Box.createVerticalStrut(15));

        // Quarterly Report
        JPanel quarterlyPanel = createReportOptionPanel("Quarterly Report:",
                e -> generateReport("quarterly"));
        buttonsPanel.add(quarterlyPanel);
        buttonsPanel.add(Box.createVerticalStrut(15));

        // Yearly Report
        JPanel yearlyPanel = createReportOptionPanel("Yearly Report:",
                e -> generateReport("yearly"));
        buttonsPanel.add(yearlyPanel);

        contentPanel.add(buttonsPanel);

        // Empty space panel
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(700, 100));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(emptyPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(700, 800));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createReportOptionPanel(String labelText, ActionListener action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        panel.add(label);

        JButton button = createStyledButton("Generate", PRIMARY_COLOR, fontSize);
        button.addActionListener(action);
        panel.add(button);

        return panel;
    }

    private void generateReport(String reportType) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate today = LocalDate.now();

        switch (reportType.toLowerCase()) {
            case "daily":
                for (int i = 0; i < 7; i++) {
                    LocalDate day = today.minusDays(i);
                    double revenue = 1000 + i * 100;
                    double profit = revenue * 0.25;
                    dataset.addValue(revenue, "Revenue (£)", day.toString());
                    dataset.addValue(profit, "Profit (£)", day.toString());
                }
                break;
            case "quarterly":
                for (int i = 0; i < 4; i++) {
                    String quarter = "Q" + (4 - i);
                    double revenue = 5000 + i * 500;
                    double profit = revenue * 0.30;
                    dataset.addValue(revenue, "Revenue (£)", quarter);
                    dataset.addValue(profit, "Profit (£)", quarter);
                }
                break;
            case "yearly":
                int currentYear = today.getYear();
                for (int i = 0; i < 5; i++) {
                    int year = currentYear - i;
                    double revenue = 20000 + i * 2000;
                    double profit = revenue * 0.35;
                    dataset.addValue(revenue, "Revenue (£)", String.valueOf(year));
                    dataset.addValue(profit, "Profit (£)", String.valueOf(year));
                }
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid report type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                reportType.substring(0, 1).toUpperCase() + reportType.substring(1) + " Report",
                "Time Period",
                "Amount (£)",
                dataset
        );

        // Style the chart
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(BORDER_COLOR);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        chartPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        JDialog dialog = new JDialog((Frame) null, "Report Preview", true);
        dialog.getContentPane().add(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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
        this.fontSize = newFontSize;

        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().equals("Generate New Report")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
                } else {
                    label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
                }
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            } else if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }

        revalidate();
        repaint();
    }

    private void updatePanelFonts(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                ((JLabel) component).setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            } else if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }
    }
}