package GUI.MenuPanels.Reports;

import Database.SQLConnection;
import Database.SQLConnection.ReportData;
import Database.SQLConnection.ReportDateRange;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;

public class NewReportPanel extends JPanel {
    private SQLConnection sqlCon;

    // No-argument constructor
    public NewReportPanel() {
        // Create a new SQLConnection instance or get it from a singleton
        sqlCon = new SQLConnection();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(600, 400));
        setBackground(Color.WHITE);

        // Create three sections: Daily, Quarterly, Yearly
        JPanel dailyPanel = createReportSection("Daily Report", "daily");
        JPanel quarterlyPanel = createReportSection("Quarterly Report", "quarterly");
        JPanel yearlyPanel = createReportSection("Yearly Report", "yearly");

        add(dailyPanel);
        add(Box.createVerticalStrut(20));
        add(quarterlyPanel);
        add(Box.createVerticalStrut(20));
        add(yearlyPanel);
    }

    /**
     * Creates a panel for a specific report type with a "Generate and Preview" button.
     *
     * @param title      The section title.
     * @param reportType The report type identifier ("daily", "quarterly", or "yearly").
     * @return a JPanel containing the report section.
     */
    private JPanel createReportSection(String title, String reportType) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(title + ":");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        JButton previewButton = new JButton("Generate and Preview");
        previewButton.setFont(new Font("Arial", Font.PLAIN, 14));
        previewButton.addActionListener((ActionEvent e) -> generateReport(reportType));
        panel.add(label);
        panel.add(previewButton);
        return panel;
    }

    /**
     * Generates the report based on the report type.
     * It calculates the start date from today using SQLConnection.getReportDateRange,
     * retrieves aggregated revenue and profit via SQLConnection.getReportData,
     * and then displays a bar chart.
     *
     * @param reportType the type of report ("daily", "quarterly", or "yearly").
     */
    private void generateReport(String reportType) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate today = LocalDate.now();

        switch (reportType.toLowerCase()) {
            case "daily":
                // Dummy data for last 7 days.
                for (int i = 0; i < 7; i++) {
                    LocalDate day = today.minusDays(i);
                    double revenue = 1000 + i * 100;   // Dummy revenue
                    double profit = revenue * 0.25;      // Dummy profit
                    dataset.addValue(revenue, "Revenue (£)", day.toString());
                    dataset.addValue(profit, "Profit (£)", day.toString());
                }
                break;
            case "quarterly":
                // Dummy data for 4 quarters.
                for (int i = 0; i < 4; i++) {
                    String quarter = "Q" + (4 - i);
                    double revenue = 5000 + i * 500;
                    double profit = revenue * 0.30;
                    dataset.addValue(revenue, "Revenue (£)", quarter);
                    dataset.addValue(profit, "Profit (£)", quarter);
                }
                break;
            case "yearly":
                // Dummy data for the last 5 years.
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

        // Create the bar chart.
        JFreeChart barChart = ChartFactory.createBarChart(
                reportType.substring(0, 1).toUpperCase() + reportType.substring(1) + " Report",
                "Time Period",
                "Amount (£)",
                dataset
        );

        // Set y-axis range from 0 to 100,000,000.
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRangeAxis().setRange(0.0, 100000000.0);

        // Display the chart in a dialog.
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        JDialog dialog = new JDialog((Frame) null, "Report Preview", true);
        dialog.getContentPane().add(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

}
