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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class PastReportPanel extends JPanel {
    // Color scheme matching other panels
    private final Color PRIMARY_COLOR = new Color(200, 170, 250); // Lavender
    private final Color ACCENT_COLOR = new Color(230, 210, 250); // Lighter lavender
    private final Color TEXT_COLOR = new Color(60, 60, 60); // Dark gray for text
    private final Color BORDER_COLOR = new Color(0, 0, 0); // Light gray for borders

    private JComboBox<String> reportTypeCombo;
    private JDateChooser startDateChooser;
    private JDateChooser endDateChooser;
    private JButton previewButton;
    private SQLConnection sqlCon = new SQLConnection();
    private int fontSize = 16; // Default font size

    public PastReportPanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Preview Past Report");
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

        // Report Type Selection
        JPanel reportTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 25));
        reportTypePanel.setPreferredSize(new Dimension(800, 40));
        reportTypePanel.setBackground(Color.white);
        reportTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        typeLabel.setForeground(TEXT_COLOR);
        reportTypePanel.add(typeLabel);

        String[] reportTypes = {"Yearly", "Quarterly", "Daily"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        styleDropdown(reportTypeCombo);
        reportTypePanel.add(reportTypeCombo);

        contentPanel.add(reportTypePanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Date Selection
        JPanel datePanel = new JPanel(new GridLayout(2, 1, 0, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Start Date
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        startDatePanel.setBackground(Color.WHITE);

        JLabel startLabel = new JLabel("Start Date:");
        startLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        startLabel.setForeground(TEXT_COLOR);
        startDatePanel.add(startLabel);

        startDateChooser = new JDateChooser();
        startDateChooser.setDateFormatString("dd-MM-yyyy");
        startDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        startDateChooser.setPreferredSize(new Dimension(150, 30));
        startDateChooser.setBackground(Color.WHITE);
        startDatePanel.add(startDateChooser);

        // End Date
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        endDatePanel.setBackground(Color.WHITE);

        JLabel endLabel = new JLabel("End Date:");
        endLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        endLabel.setForeground(TEXT_COLOR);
        endDatePanel.add(endLabel);

        endDateChooser = new JDateChooser();
        endDateChooser.setDateFormatString("dd-MM-yyyy");
        endDateChooser.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        endDateChooser.setPreferredSize(new Dimension(150, 30));
        endDateChooser.setBackground(Color.WHITE);
        endDatePanel.add(endDateChooser);

        datePanel.add(startDatePanel);
        datePanel.add(endDatePanel);
        contentPanel.add(datePanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Preview Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        previewButton = createStyledButton("Preview Report", PRIMARY_COLOR, fontSize);
        previewButton.addActionListener(this::generateReport);
        buttonPanel.add(previewButton);

        contentPanel.add(buttonPanel);

        // Report Type Selection
        JPanel EmptyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        EmptyPanel.setPreferredSize(new Dimension(700, 100));
        EmptyPanel.setBackground(Color.WHITE);
        EmptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(EmptyPanel);

        // Wrap in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(700, 800));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
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

    private void styleDropdown(JComboBox<String> dropdown) {
        dropdown.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        dropdown.setBackground(Color.WHITE);
        dropdown.setForeground(TEXT_COLOR);
        dropdown.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)
        ));
        dropdown.setMaximumSize(new Dimension(200, 30));
    }

    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText().equals("Preview Past Report")) {
                    label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
                } else {
                    label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
                }
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
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
            } else if (component instanceof JComboBox) {
                ((JComboBox<?>) component).setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            } else if (component instanceof JDateChooser) {
                ((JDateChooser) component).setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            } else if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }
    }
}