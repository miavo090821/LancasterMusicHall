package GUI.MenuPanels.Reports;

import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
import Database.SQLConnection;
import Database.SQLConnection.ReportData;
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
import java.time.ZoneId;
import java.util.Date;

public class PastReportPanel extends JPanel {
    private JComboBox<String> reportTypeCombo;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JButton previewButton;
    private SQLConnection sqlCon = new SQLConnection();

    public PastReportPanel(MainMenuGUI mainMenu) {
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

        JLabel titleLabel = new JLabel("Preview Past Report:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        // Report type panel
        JPanel reportTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        reportTypePanel.setPreferredSize(new Dimension(600, 30));
        reportTypePanel.setBackground(Color.white);

        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        String[] reportTypes = {"Yearly", "Quarterly", "Daily"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        styleDropdown(reportTypeCombo);
        reportTypePanel.add(typeLabel);
        reportTypePanel.add(reportTypeCombo);

        // Start date panel
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        startDatePanel.setPreferredSize(new Dimension(600, 30));
        startDatePanel.setBackground(Color.white);

        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd-MM-yyyy");
        startDateChooser.setPreferredSize(new Dimension(150, 25));
        startDatePanel.add(startLabel);
        startDatePanel.add(startDateChooser);

        // End date panel
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 0));
        endDatePanel.setPreferredSize(new Dimension(600, 30));
        endDatePanel.setBackground(Color.white);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd-MM-yyyy");
        endDateChooser.setPreferredSize(new Dimension(150, 25));
        endDatePanel.add(endLabel);
        endDatePanel.add(endDateChooser);

        // Add components to panel1
        panel1.add(titlePanel);
        panel1.add(reportTypePanel);
        panel1.add(startDatePanel);
        panel1.add(endDatePanel);

        // Preview button panel
        JPanel previewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        previewButtonPanel.setPreferredSize(new Dimension(600, 50));
        previewButtonPanel.setBackground(Color.white);

        previewButton = new JButton("Preview");
        mainMenu.stylizeButton(previewButton);
        previewButton.setFont(new Font("Arial", Font.BOLD, 16));
        previewButton.addActionListener(this::generateReport);
        previewButtonPanel.add(previewButton);

        panel2.add(previewButtonPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        bottomPanel.setPreferredSize(new Dimension(600, 50));
        bottomPanel.setBackground(Color.white);
        add(bottomPanel);

        mainPanel.add(bottomPanel);
    }

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

        ReportData data = sqlCon.getReportData(startDate, endDate);
        if (data == null) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve report data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        double totalRevenue = data.getTotalRevenue();
        double totalProfit = data.getTotalProfit();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(totalRevenue, "Revenue (£)", reportType);
        dataset.addValue(totalProfit, "Profit (£)", reportType);

        JFreeChart barChart = ChartFactory.createBarChart(
                reportType + " Report",
                "Report Type",
                "Amount (£)",
                dataset
        );
        CategoryPlot plot = barChart.getCategoryPlot();
        plot.getRangeAxis().setRange(0.0, 100000000.0);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JDialog reportDialog = new JDialog((Frame) null, "Report Preview", true);
        reportDialog.getContentPane().add(chartPanel);
        reportDialog.pack();
        reportDialog.setLocationRelativeTo(this);
        reportDialog.setVisible(true);
    }

    private void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Arial", Font.PLAIN, 16));
        dropdown.setBackground(Color.white);
        dropdown.setForeground(Color.BLACK);
        dropdown.setBorder(new LineBorder(Color.BLACK, 1));
    }
}