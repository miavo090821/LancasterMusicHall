package GUI.MenuPanels.Booking;

import Database.SQLConnection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code BookingDetailForm} class represents a dialog form that displays detailed booking information.
 * <p>
 * It shows booking, client, event, and contract details, and allows users to update or delete a booking.
 * The form retrieves data from the database using an instance of {@link SQLConnection}.
 * </p>
 */
public class BookingDetailForm extends JDialog {

    private SQLConnection sqlCon;
    private String bookingId;

    // Booking / Client UI Components
    private JTextField bookingStartDateField, bookingEndDateField, totalCostField, paymentStatusField;
    private JTextField companyNameField, contactNameField, telephoneField, emailField;

    // Event Details UI Components (displayed in a table)
    private JTable eventTable;
    private DefaultTableModel eventTableModel;

    // Contract Details UI Components
    private JLabel contractStatusLabel;
    private JButton downloadContractButton;
    // Store contract data and contract ID at the class level
    private byte[] contractData;
    private int contractId = -1;

    private String bookingStatus;

    /**
     * Constructs a new {@code BookingDetailForm} dialog.
     *
     * @param owner     the parent frame for this dialog
     * @param sqlCon    the {@code SQLConnection} instance used to load booking details
     * @param bookingId the booking ID for which details are to be displayed
     */
    public BookingDetailForm(Frame owner, SQLConnection sqlCon, String bookingId) {
        super(owner, "Booking Details - " + bookingId, true);
        this.sqlCon = sqlCon;
        this.bookingId = bookingId;
        initComponents();
        loadBookingDetails();
        setSize(600, 800);
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes and lays out all UI components of the booking detail form.
     * <p>
     * This includes fields for booking, client, event, and contract details as well as buttons
     * for updating, deleting, and downloading contracts.
     * </p>
     */
    private void initComponents() {
        // Main panel with vertical layout and scroll pane
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // --- Booking & Client Details Panel ---
        JPanel bookingPanel = new JPanel(new GridBagLayout());
        bookingPanel.setBorder(new TitledBorder("Booking & Client Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Booking Start Date
        gbc.gridx = 0;
        gbc.gridy = 0;
        bookingPanel.add(new JLabel("Booking Start Date:"), gbc);
        gbc.gridx = 1;
        bookingStartDateField = new JTextField(20);
        bookingPanel.add(bookingStartDateField, gbc);

        // Booking End Date
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Booking End Date:"), gbc);
        gbc.gridx = 1;
        bookingEndDateField = new JTextField(20);
        bookingPanel.add(bookingEndDateField, gbc);

        // Total Cost
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Total Cost:"), gbc);
        gbc.gridx = 1;
        totalCostField = new JTextField(20);
        bookingPanel.add(totalCostField, gbc);

        // Payment Status
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Payment Status:"), gbc);
        gbc.gridx = 1;
        paymentStatusField = new JTextField(20);
        bookingPanel.add(paymentStatusField, gbc);

        // Company Name (from Clients via join)
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Company Name:"), gbc);
        gbc.gridx = 1;
        companyNameField = new JTextField(20);
        bookingPanel.add(companyNameField, gbc);

        // Primary Contact
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Primary Contact:"), gbc);
        gbc.gridx = 1;
        contactNameField = new JTextField(20);
        bookingPanel.add(contactNameField, gbc);

        // Telephone
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Telephone:"), gbc);
        gbc.gridx = 1;
        telephoneField = new JTextField(20);
        bookingPanel.add(telephoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy++;
        bookingPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        bookingPanel.add(emailField, gbc);

        mainPanel.add(bookingPanel);

        // --- Event Details Panel ---
        JPanel eventPanel = new JPanel(new BorderLayout());
        eventPanel.setBorder(new TitledBorder("Event Details"));

        // Set up event table model with non-editable Event ID column.
        String[] eventColumns = {"Event ID", "Name", "Start Date", "End Date", "Start Time", "End Time", "Event Type", "Venue Name"};
        eventTableModel = new DefaultTableModel(eventColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make all columns editable except the Event ID column.
                return column != 0;
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.setFillsViewportHeight(true);
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventScrollPane.setPreferredSize(new Dimension(550, 150));
        eventPanel.add(eventScrollPane, BorderLayout.CENTER);

        mainPanel.add(eventPanel);

        // --- Contract Details Panel ---
        JPanel contractPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contractPanel.setBorder(new TitledBorder("Contract Details"));
        contractStatusLabel = new JLabel("No contract uploaded.");
        downloadContractButton = new JButton("Download Contract");
        downloadContractButton.setEnabled(false);
        contractPanel.add(contractStatusLabel);
        contractPanel.add(downloadContractButton);
        mainPanel.add(contractPanel);

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update Booking");
        updateButton.addActionListener(e -> updateBooking());
        buttonPanel.add(updateButton);
        mainPanel.add(buttonPanel);

        setContentPane(new JScrollPane(mainPanel));

        // Download button action listener for contract download.
        downloadContractButton.addActionListener(e -> {
            if (contractData != null && contractData.length > 0) {
                // Use a default file name based on contract ID.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("contract_" + contractId + ".dat"));
                int option = fileChooser.showSaveDialog(BookingDetailForm.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        fos.write(contractData);
                        JOptionPane.showMessageDialog(BookingDetailForm.this, "Contract downloaded successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(BookingDetailForm.this, "Error saving file: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(BookingDetailForm.this, "No contract data available to download.");
            }
        });

        // Create the Delete Booking button.
        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.setBackground(Color.RED);
        deleteButton.addActionListener(e -> deleteBookingAction());
        buttonPanel.add(deleteButton);

        // Adding button panel (containing update and delete buttons) to main panel.
        mainPanel.add(buttonPanel);
    }

    /**
     * Loads booking details from the database and populates the form fields.
     * <p>
     * This method retrieves booking and client data as well as event and contract details,
     * then populates the corresponding UI components.
     * </p>
     */
    private void loadBookingDetails() {
        try {
            // --- Load Booking Data (join with Clients for company_name) ---
            ResultSet rsBooking = sqlCon.getBookingDetails(Integer.parseInt(bookingId));
            if (rsBooking.next()) {
                bookingStartDateField.setText(rsBooking.getString("booking_DateStart"));
                bookingEndDateField.setText(rsBooking.getString("booking_DateEnd"));
                totalCostField.setText(rsBooking.getString("total_cost"));
                paymentStatusField.setText(rsBooking.getString("payment_status"));
                companyNameField.setText(rsBooking.getString("company_name"));
                // Capture booking_status; allowed values: "confirmed" or "held"
                bookingStatus = rsBooking.getString("booking_status");
            }
            rsBooking.close();

            // --- Load Client Data ---
            ResultSet rsClient = sqlCon.getClientDetails(Integer.parseInt(bookingId));
            if (rsClient.next()) {
                contactNameField.setText(rsClient.getString("Contact Name"));
                telephoneField.setText(rsClient.getString("Phone Number"));
                emailField.setText(rsClient.getString("Contact Email"));
            }
            rsClient.close();

            // --- Load Event Data ---
            ResultSet rsEvent = sqlCon.getEventDetails(Integer.parseInt(bookingId));
            while (rsEvent.next()) {
                Object[] rowData = {
                        rsEvent.getInt("event_id"),
                        rsEvent.getString("name"),
                        rsEvent.getString("start_date"),
                        rsEvent.getString("end_date"),
                        rsEvent.getString("start_time"),
                        rsEvent.getString("end_time"),
                        rsEvent.getString("event_type"),
                        rsEvent.getString("venue_name")
                };
                eventTableModel.addRow(rowData);
            }
            rsEvent.close();

            // --- Load Contract Data ---
            ResultSet rsContract = sqlCon.getContractDetails(Integer.parseInt(bookingId));
            if (rsContract.next()) {
                contractId = rsContract.getInt("contract_id");
                String contractDetails = rsContract.getString("details");
                contractData = rsContract.getBytes("file_data");    // store contract file data

                if (contractData != null && contractData.length > 0) {
                    contractStatusLabel.setText("<html>Contract ID: " + contractId +
                            "<br/>Details: " + contractDetails + "</html>");
                    downloadContractButton.setEnabled(true);
                } else {
                    contractStatusLabel.setText("No contract uploaded.");
                    downloadContractButton.setEnabled(false);
                }
            } else {
                contractStatusLabel.setText("No contract uploaded.");
                downloadContractButton.setEnabled(false);
            }
            rsContract.close();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading booking details: " + ex.getMessage());
        }
    }

    /**
     * Updates the booking details based on user changes in the form.
     * <p>
     * This method validates the input, builds a list of updated events from the event table,
     * and calls the SQLConnection's {@code updateFullBooking} method.
     * </p>
     */
    private void updateBooking() {
        try {
            // Helper function: treat blank as null.
            java.util.function.Function<String, String> emptyToNull = s -> s.trim().isEmpty() ? null : s.trim();

            String bId = bookingId; // bookingId is already stored as a class field.
            String bookingEventName = ""; // Not used when each event has its own name.

            // Parse dates; if blank, pass null.
            LocalDate bookingStartDate = emptyToNull.apply(bookingStartDateField.getText()) != null
                    ? LocalDate.parse(bookingStartDateField.getText().trim()) : null;
            LocalDate bookingEndDate = emptyToNull.apply(bookingEndDateField.getText()) != null
                    ? LocalDate.parse(bookingEndDateField.getText().trim()) : null;

            // Instead of reading booking_status from a field (which may contain an invalid value),
            // pass null so that the current database value is kept.
            String bookingStatus = null;

            String companyName = emptyToNull.apply(companyNameField.getText());
            String primaryContact = emptyToNull.apply(contactNameField.getText());
            String telephone = emptyToNull.apply(telephoneField.getText());
            String email = emptyToNull.apply(emailField.getText());

            // Build the list of updated events from the event table.
            List<operations.entities.Event> events = new ArrayList<>();
            int rowCount = eventTableModel.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                int eventId = (int) eventTableModel.getValueAt(i, 0);
                String eventName = emptyToNull.apply((String) eventTableModel.getValueAt(i, 1));
                LocalDate eventStartDate = emptyToNull.apply((String) eventTableModel.getValueAt(i, 2)) != null
                        ? LocalDate.parse((String) eventTableModel.getValueAt(i, 2)) : null;
                LocalDate eventEndDate = emptyToNull.apply((String) eventTableModel.getValueAt(i, 3)) != null
                        ? LocalDate.parse((String) eventTableModel.getValueAt(i, 3)) : null;
                LocalTime eventStartTime = emptyToNull.apply((String) eventTableModel.getValueAt(i, 4)) != null
                        ? LocalTime.parse((String) eventTableModel.getValueAt(i, 4)) : null;
                LocalTime eventEndTime = emptyToNull.apply((String) eventTableModel.getValueAt(i, 5)) != null
                        ? LocalTime.parse((String) eventTableModel.getValueAt(i, 5)) : null;
                String eventType = emptyToNull.apply((String) eventTableModel.getValueAt(i, 6));
                String venueName = emptyToNull.apply((String) eventTableModel.getValueAt(i, 7));

                // Create a simple Venue object (adjust details as needed).
                operations.entities.Venue venue = new operations.entities.Venue(0, venueName, venueName, 0, "N/A", false, false, 0.0);

                operations.entities.Event event = new operations.entities.Event(
                        eventId,
                        eventName,
                        eventType,
                        eventStartDate,
                        eventEndDate,
                        eventStartTime,
                        eventEndTime,
                        false,
                        "",
                        venue,
                        null,
                        "",
                        venueName,
                        companyName,
                        null,
                        0.0,
                        "", // description
                        ""  // layout
                );
                events.add(event);
            }

            double customerBillTotal = emptyToNull.apply(totalCostField.getText()) != null
                    ? Double.parseDouble(totalCostField.getText().trim()) : 0.0;
            // For fields not editable in this form, pass null (to keep current value).
            Double ticketPrice = null;
            String customerAccount = null;
            String customerSortCode = null;
            String streetAddress = null;
            String city = null;
            String postcode = null;
            LocalDate paymentDueDate = bookingStartDate; // Default to booking start date.

            // Validate payment_status: allow only "paid" or "unpaid"; if invalid or blank, default to "unpaid".
            String paymentStatus = emptyToNull.apply(paymentStatusField.getText());
            if (paymentStatus != null) {
                paymentStatus = paymentStatus.toLowerCase();
                if (!paymentStatus.equals("paid") && !paymentStatus.equals("unpaid")) {
                    paymentStatus = "unpaid";
                }
            }

            String contractDetails = null; // Not editable in this form.
            File contractFile = null;      // Not updated here.
            Double maxDiscount = null;     // Pass null if not provided.
            Integer staffId = sqlCon.getCurrentStaffId();
            if (staffId == null) {
                staffId = 0;
            }

            // Call the SQLConnection updateFullBooking method.
            boolean success = sqlCon.updateFullBooking(
                    bId,
                    bookingEventName,
                    bookingStartDate,
                    bookingEndDate,
                    bookingStatus,
                    companyName,
                    primaryContact,
                    telephone,
                    email,
                    events,
                    customerBillTotal,
                    ticketPrice,
                    customerAccount,
                    customerSortCode,
                    streetAddress,
                    city,
                    postcode,
                    paymentDueDate,
                    paymentStatus,
                    contractDetails,
                    contractFile,
                    (maxDiscount != null) ? maxDiscount : 0.0,
                    staffId
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Booking updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the booking after verifying that the booking status is "held".
     * <p>
     * This method calls the {@code deleteFullBooking} method from the {@code SQLConnection} instance.
     * If successful, the form is closed.
     * </p>
     */
    private void deleteBookingAction() {
        try {
            // Only allow deletion if the booking_status is "held"
            if (!"held".equalsIgnoreCase(bookingStatus)) {
                JOptionPane.showMessageDialog(this, "Only held bookings can be deleted.");
                return;
            }
            int bId = Integer.parseInt(bookingId);
            boolean success = sqlCon.deleteFullBooking(bId);
            if (success) {
                System.out.println("Booking " + bookingId + " has been deleted.");
                JOptionPane.showMessageDialog(this, "Booking " + bookingId + " has been deleted.");
                dispose(); // Close the form
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
