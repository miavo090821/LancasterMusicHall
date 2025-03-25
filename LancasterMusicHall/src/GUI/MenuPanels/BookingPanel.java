package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BookingPanel extends JPanel {
    public BookingPanel(MainMenuGUI mainMenu) {
        // Main panel setup
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // === Title Panel ===
        JPanel titlePanel = createTitlePanel();
        titlePanel.setBackground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = createContentPanel(mainMenu);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setPreferredSize(new Dimension(550, 40));

        JLabel titleLabel = new JLabel("Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    private JPanel createContentPanel(MainMenuGUI mainMenu) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // === Search Panel ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(Color.WHITE); // Changed from gray to match design
        searchPanel.setBorder(new EmptyBorder(0,0,20,0));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextField searchField = new JTextField(18);
        searchField.setPreferredSize(new Dimension(200, 30));

        JButton filterButton = new JButton("Filter");
        mainMenu.stylizeButton(filterButton);

        JButton newBookingButton = new JButton("New Booking");
        mainMenu.stylizeButton(newBookingButton);

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(filterButton);
        searchPanel.add(Box.createHorizontalStrut(50));
        searchPanel.add(newBookingButton);

        // Add search panel to NORTH position
        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // === Bookings Table ===
        String[] columnNames = {"ID No.", "Name", "Start Date", "End Date", "Status"};
        Object[][] data = {
                {"1003", "CinemaLTD", "01/01/2025", "07/01/2025", "Confirmed"},
                {"1004", "FilmProd", "20/01/2025", "28/01/2025", "Confirmed"},
                {"1005", "TheatreCo", "05/01/2025", "10/01/2025", "Unconfirmed"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // Remove all grid lines between cells
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Custom row border rendering
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Center align all text
                setHorizontalAlignment(SwingConstants.CENTER);

                // Add bottom border to each row (except last row)
                if (row < table.getRowCount() - 1) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                } else {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                            BorderFactory.createEmptyBorder(5, 5, 5, 5)
                    ));
                }

                // Status column coloring
                if (column == 4) {
                    if ("Confirmed".equals(value)) {
                        setForeground(new Color(0, 128, 0));
                    } else {
                        setForeground(new Color(200, 0, 0));
                    }
                } else {
                    setForeground(new Color(0, 0, 0));
                }

                return this;
            }
        });

        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setFillsViewportHeight(true);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        return contentPanel;
    }
}
