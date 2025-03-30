package GUI.MenuPanels.Reports;

import com.toedter.calendar.JDateChooser;
import Database.SQLConnection;
import Database.SQLConnection.ReportData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PastReportPanel extends JPanel {
    private JComboBox<String> reportTypeCombo;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JButton previewButton;

    // Reference to the SQLConnection. In a full application, you might retrieve this
    // from your main application or via dependency injection.
    private SQLConnection sqlCon = new SQLConnection();

    public PastReportPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(600, 350));
        setBackground(Color.WHITE);

        // Section 1: Report Type
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(Color.WHITE);
        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] reportTypes = {"Yearly", "Quarterly", "Daily"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setFont(new Font("Arial", Font.PLAIN, 16));
        typePanel.add(typeLabel);
        typePanel.add(reportTypeCombo);
        add(typePanel);

        // Section 2: Start Date
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startPanel.setBackground(Color.WHITE);
        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd-MM-yyyy");
        startDateChooser.setPreferredSize(new Dimension(150, 25));
        startPanel.add(startLabel);
        startPanel.add(startDateChooser);
        add(startPanel);

        // Section 3: End Date
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endPanel.setBackground(Color.WHITE);
        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd-MM-yyyy");
        endDateChooser.setPreferredSize(new Dimension(150, 25));
        endPanel.add(endLabel);
        endPanel.add(endDateChooser);
        add(endPanel);

        // Preview Button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        previewButton = new JButton("Preview");
        previewButton.setFont(new Font("Arial", Font.BOLD, 16));
        previewButton.addActionListener(this::generateReport);
        buttonPanel.add(previewButton);
        add(buttonPanel);
    }

    /**
     * Called when the Preview button is clicked.
     * Retrieves the start and end dates and the report type,
     * calls SQLConnection.getReportData() to obtain the total revenue and profit,
     * and then displays a bar chart in a modal dialog.
     */
    private void generateReport(ActionEvent e) {
        String reportType = (String) reportTypeCombo.getSelectedItem();

        Date startDateRaw = startDateChooser.getDate();
        Date endDateRaw = endDateChooser.getDate();
        if (startDateRaw == null || endDateRaw == null) {
            JOptionPane.showMessageDialog(this, "Please select both start and end dates.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalDate startDate = startDateRaw.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endDateRaw.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // Retrieve report data from SQL using our helper method.
        ReportData data = sqlCon.getReportData(startDate, endDate);
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve report data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double totalRevenue = data.getTotalRevenue();
        double totalProfit = data.getTotalProfit();


        // Create dataset for the bar chart.
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(totalRevenue, "Revenue (£)", reportType);
        dataset.addValue(totalProfit, "Profit (£)", reportType);

        // Create the bar chart.
        JFreeChart barChart = ChartFactory.createBarChart(
                reportType + " Report", // Chart title
                "Report Type",          // X-axis label
                "Amount (£)",           // Y-axis label
                dataset
        );
        // Set y-axis range from 0 to 100,000,000.
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRangeAxis().setRange(0.0, 100000000.0);

        // Display the chart in a modal dialog.
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JDialog reportDialog = new JDialog((Frame) null, "Report Preview", true);
        reportDialog.getContentPane().add(chartPanel);
        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }
}
