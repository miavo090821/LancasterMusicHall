package GUI.MenuPanels.Reports;

import Database.SQLConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DailyEventsPanel extends JPanel {
    private SQLConnection sqlCon;
    private JTable eventsTable;
    private JButton refreshButton, downloadButton;
    private JLabel dateLabel, statusLabel;
    private LocalDate currentDate;
    private JProgressBar progressBar;

    public DailyEventsPanel(SQLConnection sqlCon) {
        this.sqlCon = sqlCon;
        this.currentDate = LocalDate.now();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Create header panel with date navigation
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Create table for events
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Create download panel
        JPanel downloadPanel = createDownloadPanel();
        add(downloadPanel, BorderLayout.SOUTH);

        // Load initial data
        loadDailyEvents();
    }

    private JPanel createDownloadPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Download button
        downloadButton = new JButton("Download as CSV");
        downloadButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        downloadButton.setBackground(new Color(207, 185, 255)); // Lavender
        downloadButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        downloadButton.addActionListener(e -> downloadAsCSV());

        // Status label and progress bar
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(progressBar, BorderLayout.CENTER);

        panel.add(downloadButton, BorderLayout.WEST);
        panel.add(statusPanel, BorderLayout.CENTER);

        return panel;
    }

    private void downloadAsCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Daily Events");
        fileChooser.setSelectedFile(new File("events_" + currentDate + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            new SwingWorker<Void, Integer>() {
                @Override
                protected Void doInBackground() throws Exception {
                    publish(0);
                    statusLabel.setText("Preparing download...");
                    progressBar.setVisible(true);

                    try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                        DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();

                        // Write headers
                        for (int i = 0; i < model.getColumnCount(); i++) {
                            writer.write(model.getColumnName(i));
                            if (i < model.getColumnCount() - 1) writer.write(",");
                        }
                        writer.write("\n");

                        // Write data
                        for (int row = 0; row < model.getRowCount(); row++) {
                            for (int col = 0; col < model.getColumnCount(); col++) {
                                writer.write(model.getValueAt(row, col).toString());
                                if (col < model.getColumnCount() - 1) writer.write(",");
                            }
                            writer.write("\n");
                            publish((row * 100) / model.getRowCount());
                        }

                        publish(100);
                        statusLabel.setText("Download completed successfully!");
                    } catch (IOException ex) {
                        statusLabel.setText("Error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    int progress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(progress);
                    if (progress < 100) {
                        statusLabel.setText("Downloading... " + progress + "%");
                    }
                }

                @Override
                protected void done() {
                    new Timer(3000, e -> {
                        statusLabel.setText(" ");
                        progressBar.setVisible(false);
                    }).start();
                }
            }.execute();
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Date label
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        dateLabel.setForeground(new Color(60, 60, 60));
        updateDateLabel();

        // Navigation buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBackground(Color.WHITE);

        JButton prevDayButton = createNavButton("<");
        prevDayButton.addActionListener(e -> navigateDays(-1));

        JButton nextDayButton = createNavButton(">");
        nextDayButton.addActionListener(e -> navigateDays(1));

        JButton todayButton = createNavButton("Today");
        todayButton.addActionListener(e -> {
            currentDate = LocalDate.now();
            updateDateLabel();
            loadDailyEvents();
        });

        refreshButton = createNavButton("Refresh");
        refreshButton.addActionListener(e -> loadDailyEvents());

        navPanel.add(prevDayButton);
        navPanel.add(todayButton);
        navPanel.add(nextDayButton);
        navPanel.add(refreshButton);

        panel.add(dateLabel, BorderLayout.CENTER);
        panel.add(navPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void navigateDays(int days) {
        currentDate = currentDate.plusDays(days);
        updateDateLabel();
        loadDailyEvents();
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(60, 60, 60));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFocusPainted(false);
        return button;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder());

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Event Name", "Type", "Venue", "Start", "End", "Description"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        eventsTable = new JTable(model);
        customizeTableAppearance();

        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void customizeTableAppearance() {
        eventsTable.setRowHeight(30);
        eventsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eventsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        eventsTable.setBackground(Color.WHITE);
        eventsTable.setGridColor(new Color(240, 240, 240));
        eventsTable.setShowGrid(true);
        eventsTable.setAutoCreateRowSorter(true);
        eventsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        eventsTable.setSelectionBackground(new Color(230, 230, 250));
        eventsTable.setSelectionForeground(Color.BLACK);
        eventsTable.setIntercellSpacing(new Dimension(0, 1));

        // Set column widths
        eventsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Type
        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Venue
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Start
        eventsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // End
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(300); // Description
    }

    private void updateDateLabel() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateLabel.setText("Events for " + currentDate.format(formatter));
    }

    private void loadDailyEvents() {
        DefaultTableModel model = (DefaultTableModel) eventsTable.getModel();
        model.setRowCount(0); // Clear existing data

        try {
            String query = "SELECT e.event_id, e.name, e.event_type, " +
                    "e.location, e.start_time, e.end_time, e.description " +
                    "FROM Event e " +
                    "WHERE DATE(e.start_date) = ? " +
                    "ORDER BY e.start_time";

            Date sqlDate = Date.valueOf(currentDate);

            try (PreparedStatement ps = sqlCon.getConnection().prepareStatement(query)) {
                ps.setDate(1, sqlDate);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("event_id"),
                            rs.getString("name"),
                            rs.getString("event_type"),
                            rs.getString("location"),
                            formatTime(rs.getTime("start_time")),
                            formatTime(rs.getTime("end_time")),
                            rs.getString("description") != null ? rs.getString("description") : ""
                    });
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading events: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatTime(Time time) {
        if (time == null) return "";
        return time.toString().substring(0, 5); // Show just HH:MM
    }
}