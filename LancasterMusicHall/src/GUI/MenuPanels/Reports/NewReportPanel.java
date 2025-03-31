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
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.time.LocalDate;

public class NewReportPanel extends JPanel {
    private SQLConnection sqlCon;

    public NewReportPanel(MainMenuGUI mainMenu) {
        sqlCon = new SQLConnection();
        setPreferredSize(new Dimension(600, 350));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Main container panel
        JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        mainPanel.setPreferredSize(new Dimension(600, 350));
        mainPanel.setBackground(Color.white);
        add(mainPanel);

        // Panel for text elements
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBorder(new LineBorder(Color.black));
        textPanel.setPreferredSize(new Dimension(750, 600));
        textPanel.setBackground(Color.white);
        mainPanel.add(textPanel);

        // Main panel 1
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        panel1.setPreferredSize(new Dimension(600, 120));
        panel1.setBackground(Color.white);
        textPanel.add(panel1);

        // Main panel 2
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
        panel2.setPreferredSize(new Dimension(600, 200));
        panel2.setBackground(Color.white);
        textPanel.add(panel2);

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setPreferredSize(new Dimension(600, 30));
        titlePanel.setBackground(Color.white);

        JLabel titleLabel = new JLabel("Generate New Report:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        // Daily report panel
        JPanel dailyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        dailyPanel.setPreferredSize(new Dimension(600, 30));
        dailyPanel.setBackground(Color.white);

        JLabel dailyLabel = new JLabel("Daily Report:");
        dailyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton dailyButton = new JButton("Generate and Preview");
        mainMenu.stylizeButton(dailyButton);
        dailyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        dailyButton.addActionListener(e -> generateReport("daily"));
        dailyPanel.add(dailyLabel);
        dailyPanel.add(dailyButton);

        // Quarterly report panel
        JPanel quarterlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        quarterlyPanel.setPreferredSize(new Dimension(600, 30));
        quarterlyPanel.setBackground(Color.white);

        JLabel quarterlyLabel = new JLabel("Quarterly Report:");
        quarterlyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton quarterlyButton = new JButton("Generate and Preview");
        mainMenu.stylizeButton(quarterlyButton);
        quarterlyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        quarterlyButton.addActionListener(e -> generateReport("quarterly"));
        quarterlyPanel.add(quarterlyLabel);
        quarterlyPanel.add(quarterlyButton);

        // Yearly report panel
        JPanel yearlyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        yearlyPanel.setPreferredSize(new Dimension(600, 30));
        yearlyPanel.setBackground(Color.white);

        JLabel yearlyLabel = new JLabel("Yearly Report:");
        yearlyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        JButton yearlyButton = new JButton("Generate and Preview");
        mainMenu.stylizeButton(yearlyButton);
        yearlyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        yearlyButton.addActionListener(e -> generateReport("yearly"));
        yearlyPanel.add(yearlyLabel);
        yearlyPanel.add(yearlyButton);

        // Add components to panel1
        panel1.add(titlePanel);
        panel1.add(dailyPanel);
        panel1.add(quarterlyPanel);
        panel1.add(yearlyPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.white);
        add(bottomPanel);

        mainPanel.add(bottomPanel);
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

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRangeAxis().setRange(0.0, 100000000.0);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        JDialog dialog = new JDialog((Frame) null, "Report Preview", true);
        dialog.getContentPane().add(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}