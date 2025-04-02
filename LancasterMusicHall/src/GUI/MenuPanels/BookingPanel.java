package GUI.MenuPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
import GUI.NewBookingForm;
import java.awt.Window;

import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Seat;
import operations.entities.Venue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class BookingPanel extends JPanel {
    private SQLConnection sqlCon;
    private MainMenuGUI mainMenu;

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

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setPreferredSize(new Dimension(550, 40));

        JLabel titleLabel = new JLabel("Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

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
        DefaultTableModel model = sqlCon.getBookingsTableModel();
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

        // Optionally: add a mouse listener to refresh the table when the panel is clicked.
        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                DefaultTableModel newModel = sqlCon.getBookingsTableModel();
                table.setModel(newModel);
            }
        });

        return contentPanel;
    }
    private void showNewBookingForm() {
        Window ownerWindow = SwingUtilities.getWindowAncestor(this);
        Frame ownerFrame = (ownerWindow instanceof Frame) ? (Frame) ownerWindow : null;
        NewBookingForm newBookingDialog = new NewBookingForm(ownerFrame, sqlCon);
        newBookingDialog.setVisible(true);
    }





    /**
     * Displays a form to capture new booking (event) details, then calls SQLConnection.insertEvent(...) to insert the data.
     */
//    private void showNewBookingForm() {
//        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "New Booking", Dialog.ModalityType.APPLICATION_MODAL);
//        dialog.setSize(400, 450);
//        dialog.setLayout(new GridLayout(0, 2, 10, 10));
//
//        // Fields for event details (order is important):
//        // booking_id, start_date, end_date, start_time, end_time,
//        // activity_id, venue_id, staff_ID, client_id (to get company name), and held status.
//        JTextField bookingIdField = new JTextField();
//        JTextField startDateField = new JTextField("2025-03-01");  // yyyy-MM-dd
//        JTextField endDateField = new JTextField("2025-03-01");
//        JTextField startTimeField = new JTextField("10:00");       // HH:mm
//        JTextField endTimeField = new JTextField("12:00");
//        JTextField activityIdField = new JTextField("1");
//        JTextField venueIdField = new JTextField("1");
//        JTextField staffIdField = new JTextField("1001");
//        JTextField clientIdField = new JTextField("2001");
//        JCheckBox heldCheck = new JCheckBox("Held");
//
//        dialog.add(new JLabel("Booking ID:"));
//        dialog.add(bookingIdField);
//        dialog.add(new JLabel("Start Date (yyyy-MM-dd):"));
//        dialog.add(startDateField);
//        dialog.add(new JLabel("End Date (yyyy-MM-dd):"));
//        dialog.add(endDateField);
//        dialog.add(new JLabel("Start Time (HH:mm):"));
//        dialog.add(startTimeField);
//        dialog.add(new JLabel("End Time (HH:mm):"));
//        dialog.add(endTimeField);
//        dialog.add(new JLabel("Activity ID:"));
//        dialog.add(activityIdField);
//        dialog.add(new JLabel("Venue ID:"));
//        dialog.add(venueIdField);
//        dialog.add(new JLabel("Staff ID:"));
//        dialog.add(staffIdField);
//        dialog.add(new JLabel("Client ID:"));
//        dialog.add(clientIdField);
//        dialog.add(new JLabel("Held:"));
//        dialog.add(heldCheck);
//
//        JButton saveButton = new JButton("Save");
//        saveButton.addActionListener(ev -> {
//            try {
//                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
//                LocalDate sDate = LocalDate.parse(startDateField.getText().trim());
//                LocalDate eDate = LocalDate.parse(endDateField.getText().trim());
//                LocalTime sTime = LocalTime.parse(startTimeField.getText().trim());
//                LocalTime eTime = LocalTime.parse(endTimeField.getText().trim());
//                int activityId = Integer.parseInt(activityIdField.getText().trim());
//                int venueId = Integer.parseInt(venueIdField.getText().trim());
//                int staffId = Integer.parseInt(staffIdField.getText().trim());
//                int clientId = Integer.parseInt(clientIdField.getText().trim());
//                boolean isHeld = heldCheck.isSelected();
//
//                // Create placeholder Activity and Venue objects.
//                Activity act = new Activity(activityId, "Activity " + activityId);
//                Venue ven = new Venue(venueId, "Room " + venueId, "Hall", 300);
//                // For simplicity, we assume an empty list for seats.
//                java.util.List<Seat> seats = new ArrayList<>();
//
//                // Construct a new Booking using the collected fields.
//                // For client data, we assume that the client ID will be used to lookup the company name in the SQL insertEvent() method.
//                Booking newBooking = new Booking(
//                        bookingId,
//                        sDate,
//                        eDate,
//                        sTime,
//                        eTime,
//                        act,
//                        ven,
//                        isHeld,
//                        "",  // holdExpiryDate is empty if not used
//                        seats,
//                        String.valueOf(staffId),  // bookedBy is the staff ID (as string)
//                        ven.getName(),            // room from the Venue
//                        String.valueOf(clientId), // companyName placeholder (client ID as string)
//                        null                      // contactDetails (null for now; SQL join can bring real client data)
//                );
//
//                // Insert the new booking via SQLConnection.insertEvent().
//                boolean inserted = sqlCon.insertEvent(newBooking);
//                if (inserted) {
//                    JOptionPane.showMessageDialog(dialog, "Event created successfully!");
//                    dialog.dispose();
//                    // Optionally refresh your table model to reflect new data.
//                } else {
//                    JOptionPane.showMessageDialog(dialog, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//        dialog.add(saveButton);
//
//        JButton cancelButton = new JButton("Cancel");
//        cancelButton.addActionListener(ev -> dialog.dispose());
//        dialog.add(cancelButton);
//
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
//    }
}
