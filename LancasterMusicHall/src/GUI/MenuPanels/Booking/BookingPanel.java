package GUI.MenuPanels.Booking;

import Database.SQLConnection;
import GUI.MainMenuGUI;

import java.awt.*;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * The {@code BookingPanel} class represents the main user interface panel for displaying and
 * interacting with booking records.
 * <p>
 * It retrieves booking data via a {@link SQLConnection} instance, and provides functionality
 * to filter, add, and view detailed booking information.
 * </p>
 */
public class BookingPanel extends JPanel {
    private SQLConnection sqlCon;
    private MainMenuGUI mainMenu;

    /**
     * Constructs a {@code BookingPanel} using the given main menu.
     *
     * @param mainMenu the main menu GUI that provides the SQL connection and other application context
     */
    public BookingPanel(MainMenuGUI mainMenu) {
        this.mainMenu = mainMenu;
        this.sqlCon = mainMenu.getSqlConnection();
        // Main panel setup
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // === Title Panel ===
        JPanel titlePanel = createTitlePanel();
        titlePanel.setBackground(Color.WHITE);
        add(titlePanel, BorderLayout.NORTH);

        // === Main Content Panel ===
        JPanel contentPanel = createContentPanel();
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Creates and returns the title panel for the booking panel.
     *
     * @return a {@code JPanel} containing the title label
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setPreferredSize(new Dimension(550, 40));

        JLabel titleLabel = new JLabel("Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    /**
     * Creates and returns the content panel which includes the search controls and the bookings table.
     * <p>
     * The panel also includes mouse listeners to refresh the table and to launch the booking detail form on double-click.
     * </p>
     *
     * @return a {@code JPanel} containing the main content for booking management
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // === Search Panel ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextField searchField = new JTextField(18);
        searchField.setPreferredSize(new Dimension(200, 30));

        JButton filterButton = new JButton("Filter");
        mainMenu.stylizeButton(filterButton);

        JButton newBookingButton = new JButton("New Booking");
        mainMenu.stylizeButton(newBookingButton);
        newBookingButton.addActionListener(e -> showNewBookingForm());

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(filterButton);
        searchPanel.add(Box.createHorizontalStrut(50));
        searchPanel.add(newBookingButton);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // === Bookings Table ===
        DefaultTableModel originalModel = sqlCon.getBookingsTableModel();
        // Create our own column vector since getColumnIdentifiers() is not accessible
        String[] colNames = {"ID No.", "Name", "Start Date", "End Date", "Status"};
        Vector<String> columnNamesVector = new Vector<>(Arrays.asList(colNames));

        DefaultTableModel model = new DefaultTableModel(originalModel.getDataVector(), columnNamesVector) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // disable editing for all cells
            }
        };

        JTable table = new JTable(model);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                if (column == 4) {
                    if ("confirmed".equalsIgnoreCase(String.valueOf(value))) {
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

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Add a mouse listener for double-click to open the booking details form.
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // On double-click, launch the BookingDetailForm for viewing.
                if (evt.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        // Assuming the booking ID is in the first column.
                        String bookingId = table.getValueAt(selectedRow, 0).toString();
                        Window ownerWindow = SwingUtilities.getWindowAncestor(BookingPanel.this);
                        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
                        BookingDetailForm detailForm = new BookingDetailForm(ownerFrame, sqlCon, bookingId);
                        detailForm.setVisible(true);
                    }
                }
            }
        });

        // Add a mouse listener to refresh the table when the panel is clicked.
        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DefaultTableModel newModel = sqlCon.getBookingsTableModel();
                table.setModel(new DefaultTableModel(newModel.getDataVector(), columnNamesVector) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
            }
        });

        return contentPanel;
    }

    /**
     * Displays the new booking form.
     * <p>
     * The new booking form is launched as a modal dialog.
     * </p>
     */
    private void showNewBookingForm() {
        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
        NewBookingForm newBookingDialog = new NewBookingForm(ownerFrame, sqlCon);
        newBookingDialog.setVisible(true);
    }
}
