package GUI.MenuPanels.Reports;

import Database.SQLConnection;
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

/**
 * The NewReportPanel class provides an interactive interface for generating and viewing
 * various financial reports for the venue management system.
 *
 * <p>Key features include:
 * <ul>
 *   <li>Generation of daily, quarterly, and yearly financial reports</li>
 *   <li>Interactive chart visualization of revenue and profit data</li>
 *   <li>Consistent styling matching the application's design system</li>
 *   <li>Responsive layout that adapts to different screen sizes</li>
 * </ul>
 *
 * @see MainMenuGUI
 * @see SQLConnection
 * @since 1.3
 */
public class NewReportPanel extends JPanel {
    // Color scheme matching other panels
    /** Primary lavender color used for buttons and accents */
    private final Color PRIMARY_COLOR = new Color(200, 170, 250);

    /** Lighter lavender accent color for hover effects */
    private final Color ACCENT_COLOR = new Color(230, 210, 250);

    /** Dark gray color for text and labels */
    private final Color TEXT_COLOR = new Color(60, 60, 60);

    /** Black color for borders and grid lines */
    private final Color BORDER_COLOR = new Color(0, 0, 0);

    /** Database connection handler for report data */
    private SQLConnection sqlCon;

    /** Current base font size for UI elements */
    private int fontSize = 16;

    /**
     * Constructs a new NewReportPanel with reference to the main menu.
     *
     * @param mainMenu The parent MainMenuGUI that contains this panel
     */
    public NewReportPanel(MainMenuGUI mainMenu) {
        this.fontSize = mainMenu.getFontSize();
        this.sqlCon = new SQLConnection();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // === Title Panel ===
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = createContentPanel();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setPreferredSize(new Dimension(700, 800));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
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

        JLabel titleLabel = new JLabel("Generate New Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, fontSize + 4));
        titleLabel.setForeground(TEXT_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        return titlePanel;
    }

    /**
     * Creates the main content panel with report options.
     *
     * @return Configured content panel
     */
    private JPanel createContentPanel() {
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

        buttonsPanel.add(createReportOptionPanel("Daily Report:", e -> generateReport("daily")));
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(createReportOptionPanel("Quarterly Report:", e -> generateReport("quarterly")));
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(createReportOptionPanel("Yearly Report:", e -> generateReport("yearly")));

        contentPanel.add(buttonsPanel);

        // Empty space panel
        JPanel emptyPanel = new JPanel();
        emptyPanel.setPreferredSize(new Dimension(700, 100));
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(emptyPanel);

        return contentPanel;
    }

    /**
     * Creates a panel for a single report option with label and generate button.
     *
     * @param labelText The text to display for the report type
     * @param action The action to perform when the button is clicked
     * @return Configured report option panel
     */
    private JPanel createReportOptionPanel(String labelText, ActionListener action) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
        label.setForeground(TEXT_COLOR);
        panel.add(label);

        JButton button = createStyledButton(PRIMARY_COLOR, fontSize);
        button.addActionListener(action);
        panel.add(button);

        return panel;
    }

    /**
     * Generates and displays a report of the specified type.
     *
     * @param reportType The type of report to generate ("daily", "quarterly", or "yearly")
     */
    private void generateReport(String reportType) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate today = LocalDate.now();

        switch (reportType.toLowerCase()) {
            case "daily":
                generateDailyReportData(dataset, today);
                break;
            case "quarterly":
                generateQuarterlyReportData(dataset);
                break;
            case "yearly":
                generateYearlyReportData(dataset, today.getYear());
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid report type selected.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        displayReportChart(reportType, dataset);
    }

    /**
     * Generates sample daily report data for the past 7 days.
     *
     * @param dataset The dataset to populate with report data
     * @param today The current date to use as reference
     */
    private void generateDailyReportData(DefaultCategoryDataset dataset, LocalDate today) {
        for (int i = 0; i < 7; i++) {
            LocalDate day = today.minusDays(i);
            double revenue = 1000 + i * 100;
            double profit = revenue * 0.25;
            dataset.addValue(revenue, "Revenue (£)", day.toString());
            dataset.addValue(profit, "Profit (£)", day.toString());
        }
    }

    /**
     * Generates sample quarterly report data for the past 4 quarters.
     *
     * @param dataset The dataset to populate with report data
     */
    private void generateQuarterlyReportData(DefaultCategoryDataset dataset) {
        for (int i = 0; i < 4; i++) {
            String quarter = "Q" + (4 - i);
            double revenue = 5000 + i * 500;
            double profit = revenue * 0.30;
            dataset.addValue(revenue, "Revenue (£)", quarter);
            dataset.addValue(profit, "Profit (£)", quarter);
        }
    }

    /**
     * Generates sample yearly report data for the past 5 years.
     *
     * @param dataset The dataset to populate with report data
     * @param currentYear The current year to use as reference
     */
    private void generateYearlyReportData(DefaultCategoryDataset dataset, int currentYear) {
        for (int i = 0; i < 5; i++) {
            int year = currentYear - i;
            double revenue = 20000 + i * 2000;
            double profit = revenue * 0.35;
            dataset.addValue(revenue, "Revenue (£)", String.valueOf(year));
            dataset.addValue(profit, "Profit (£)", String.valueOf(year));
        }
    }

    /**
     * Displays the generated report data in a chart dialog.
     *
     * @param reportType The type of report being displayed
     * @param dataset The dataset containing the report data
     */
    private void displayReportChart(String reportType, DefaultCategoryDataset dataset) {
        String title = reportType.substring(0, 1).toUpperCase() + reportType.substring(1) + " Report";
        JFreeChart barChart = ChartFactory.createBarChart(
                title,
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

    /**
     * Creates a styled button with consistent appearance and hover effects.
     *
     * @param color    The base color for the button
     * @param fontSize The font size for the button text
     * @return Configured JButton
     */
    private JButton createStyledButton(Color color, int fontSize) {
        JButton button = new JButton("Generate");
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

    /**
     * Updates font sizes throughout the panel to maintain consistency.
     *
     * @param newFontSize The new base font size to apply
     */
    public void updateFontSizes(int newFontSize) {
        this.fontSize = newFontSize;

        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                updateLabelFont((JLabel) component);
            } else if (component instanceof JButton) {
                ((JButton) component).setFont(new Font("Segoe UI", Font.BOLD, fontSize));
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
        if (label.getText().equals("Generate New Report")) {
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
            } else if (component instanceof JPanel) {
                updatePanelFonts((JPanel) component);
            }
        }
    }
}