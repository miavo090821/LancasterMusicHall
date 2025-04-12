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

/**
 * The PastReportPanel class provides an interface for viewing historical financial reports
 * with date range filtering capabilities. It supports three report types:
 * <ul>
 *   <li>Yearly reports</li>
 *   <li>Quarterly reports</li>
 *   <li>Daily reports</li>
 * </ul>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Interactive date range selection</li>
 *   <li>Visual chart display of revenue and profit data</li>
 *   <li>Consistent styling matching the application design system</li>
 * </ul>
 *
 * @see MainMenuGUI
 * @see SQLConnection
 * @since 1.3
 */
public class PastReportPanel extends JPanel {
    // Color scheme matching other panels
    /** Primary lavender color used for buttons and accents */
    private final Color PRIMARY_COLOR = new Color(200, 170, 250);

    /** Lighter lavender accent color for hover effects */
    private final Color ACCENT_COLOR = new Color(230, 210, 250);

    /** Dark gray color for text and labels */
    private final Color TEXT_COLOR = new Color(60, 60, 60);

    /** Black color for borders and separators */
    private final Color BORDER_COLOR = new Color(0, 0, 0);

    /** Dropdown for selecting report type (Yearly/Quarterly/Daily) */
    private JComboBox<String> reportTypeCombo;

    /** Date picker for selecting the report start date */
    private JDateChooser startDateChooser;

    /** Date picker for selecting the report end date */
    private JDateChooser endDateChooser;

    /** Button to trigger report generation */
    private JButton previewButton;

    /** Database connection handler for report data */
    private SQLConnection sqlCon = new SQLConnection();

    /** Current base font size for UI elements */
    private int fontSize = 16;

    /**
     * Constructs a new PastReportPanel with reference to the main menu.
     *
     * @param mainMenu The parent MainMenuGUI that contains this panel
     */
    public PastReportPanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        initializeUI();
    }

    /**
     * Initializes the panel UI components and layout.
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createTitlePanel(), BorderLayout.NORTH);
        add(createMainContentPanel(), BorderLayout.CENTER);
    }

    /**
     * Creates the title panel with the main heading.
     *
     * @return Configured title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel titleLabel = new JLabel("Preview Past Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    /**
     * Creates the main content panel with report controls.
     *
     * @return Configured content panel
     */
    private JScrollPane createMainContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        contentPanel.add(createReportTypePanel());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createDateSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createButtonPanel());
        contentPanel.add(createEmptySpacePanel());

        return wrapInScrollPane(contentPanel);
    }

    /**
     * Creates the report type selection panel.
     *
     * @return Configured report type panel
     */
    private JPanel createReportTypePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 25));
        panel.setPreferredSize(new Dimension(800, 40));
        panel.setBackground(Color.white);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel typeLabel = new JLabel("Report Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        typeLabel.setForeground(TEXT_COLOR);
        panel.add(typeLabel);

        String[] reportTypes = {"Yearly", "Quarterly", "Daily"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        styleDropdown(reportTypeCombo);
        panel.add(reportTypeCombo);

        return panel;
    }

    /**
     * Creates the date selection panel with start/end date pickers.
     *
     * @return Configured date selection panel
     */
    private JPanel createDateSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(createStartDatePanel());
        panel.add(createEndDatePanel());

        return panel;
    }

    /**
     * Creates the start date selection panel.
     *
     * @return Configured start date panel
     */
    private JPanel createStartDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Start Date:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        panel.add(label);

        startDateChooser = createDateChooser();
        panel.add(startDateChooser);

        return panel;
    }

    /**
     * Creates the end date selection panel.
     *
     * @return Configured end date panel
     */
    private JPanel createEndDatePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("End Date:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        panel.add(label);

        endDateChooser = createDateChooser();
        panel.add(endDateChooser);

        return panel;
    }

    /**
     * Creates a styled date chooser component.
     *
     * @return Configured JDateChooser
     */
    private JDateChooser createDateChooser() {
        JDateChooser chooser = new JDateChooser();
        chooser.setDateFormatString("dd-MM-yyyy");
        chooser.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        chooser.setPreferredSize(new Dimension(150, 30));
        chooser.setBackground(Color.WHITE);
        return chooser;
    }

    /**
     * Creates the button panel with the preview button.
     *
     * @return Configured button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        previewButton = createStyledButton("Preview Report", PRIMARY_COLOR, fontSize);
        previewButton.addActionListener(this::generateReport);
        panel.add(previewButton);

        return panel;
    }

    /**
     * Creates an empty space panel for layout purposes.
     *
     * @return Configured empty panel
     */
    private JPanel createEmptySpacePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        panel.setPreferredSize(new Dimension(700, 100));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    /**
     * Wraps a panel in a scroll pane with consistent styling.
     *
     * @param panel The panel to wrap
     * @return Configured JScrollPane
     */
    private JScrollPane wrapInScrollPane(JPanel panel) {
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(700, 800));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * Generates and displays a report based on the selected parameters.
     *
     * @param e The action event that triggered this method
     */
    private void generateReport(ActionEvent e) {
        String reportType = (String) reportTypeCombo.getSelectedItem();

        Date startDateRaw = startDateChooser.getDate();
        Date endDateRaw = endDateChooser.getDate();
        if (startDateRaw == null || endDateRaw == null) {
            showErrorDialog("Please select both start and end dates.");
            return;
        }

        LocalDate startDate = convertToLocalDate(startDateRaw);
        LocalDate endDate = convertToLocalDate(endDateRaw);

        ReportData data = sqlCon.getReportData(startDate, endDate);
        if (data == null) {
            showErrorDialog("Failed to retrieve report data.");
            return;
        }

        displayReportChart(reportType, data.getTotalRevenue(), data.getTotalProfit());
    }

    /**
     * Converts a Date object to LocalDate.
     *
     * @param date The Date to convert
     * @return The corresponding LocalDate
     */
    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Displays an error dialog with the specified message.
     *
     * @param message The error message to display
     */
    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays the report data in a chart dialog.
     *
     * @param reportType The type of report being displayed
     * @param revenue The total revenue value
     * @param profit The total profit value
     */
    private void displayReportChart(String reportType, double revenue, double profit) {
        DefaultCategoryDataset dataset = createReportDataset(reportType, revenue, profit);
        JFreeChart chart = createChart(reportType, dataset);

        JDialog dialog = createChartDialog(chart);
        dialog.setVisible(true);
    }

    /**
     * Creates a dataset for the report chart.
     *
     * @param reportType The report type
     * @param revenue The revenue value
     * @param profit The profit value
     * @return Configured dataset
     */
    private DefaultCategoryDataset createReportDataset(String reportType, double revenue, double profit) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(revenue, "Revenue (£)", reportType);
        dataset.addValue(profit, "Profit (£)", reportType);
        return dataset;
    }

    /**
     * Creates a chart from the report data.
     *
     * @param reportType The report type
     * @param dataset The data to display
     * @return Configured JFreeChart
     */
    private JFreeChart createChart(String reportType, DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createBarChart(
                reportType + " Report",
                "Report Type",
                "Amount (£)",
                dataset
        );
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setRange(0.0, 100000000.0);
        return chart;
    }

    /**
     * Creates a dialog to display the chart.
     *
     * @param chart The chart to display
     * @return Configured JDialog
     */
    private JDialog createChartDialog(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        JDialog dialog = new JDialog((Frame) null, "Report Preview", true);
        dialog.getContentPane().add(chartPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    /**
     * Creates a styled button with consistent appearance.
     *
     * @param text The button text
     * @param color The base color
     * @param fontSize The font size
     * @return Configured JButton
     */
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

    /**
     * Styles a dropdown combo box consistently.
     *
     * @param dropdown The combo box to style
     */
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

    /**
     * Updates font sizes throughout the panel.
     *
     * @param newFontSize The new base font size
     */
    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                updateLabelFont((JLabel) component);
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

    /**
     * Updates the font of a label based on its role.
     *
     * @param label The label to update
     */
    private void updateLabelFont(JLabel label) {
        if (label.getText().equals("Preview Past Report")) {
            label.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        } else {
            label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        }
    }

    /**
     * Recursively updates fonts in a panel and its child components.
     *
     * @param panel The panel to update
     */
    private void updatePanelFonts(JPanel panel) {
        for (Component component : panel.getComponents()) {
            if (component instanceof JLabel) {
                updateLabelFont((JLabel) component);
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