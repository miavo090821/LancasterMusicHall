package GUI.MenuPanels.Booking;

import Database.SQLConnection;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;

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

    public BookingDetailForm(Frame owner, SQLConnection sqlCon, String bookingId) {
        super(owner, "Booking Details - " + bookingId, true);
        this.sqlCon = sqlCon;
        this.bookingId = bookingId;
        initComponents();
        loadBookingDetails();
        setSize(600, 800);
        setLocationRelativeTo(owner);
    }

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

        String[] eventColumns = {"Event ID", "Name", "Start Date", "End Date", "Start Time", "End Time", "Event Type", "Venue Name"};
        eventTableModel = new DefaultTableModel(eventColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Make table non-editable
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

        // Download button action listener
        downloadContractButton.addActionListener(e -> {
            if (contractData != null && contractData.length > 0) {
                // Use a default file name based on contract ID
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
    }

    private void loadBookingDetails() {
        try {
            // --- Load Booking Data (join with Clients for company_name) ---
            ResultSet rsBooking = sqlCon.getBookingDetails(Integer.parseInt(bookingId));
            if (rsBooking.next()) {
                bookingStartDateField.setText(rsBooking.getString("booking_DateStart"));
                bookingEndDateField.setText(rsBooking.getString("booking_DateEnd"));
                totalCostField.setText(rsBooking.getString("total_cost"));
                paymentStatusField.setText(rsBooking.getString("payment_status"));
                // Company name retrieved via join (alias "company_name")
                companyNameField.setText(rsBooking.getString("company_name"));
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
            // The updated contract entity now includes only 'contract_id', 'details', and 'file_data'
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

    private void updateBooking() {
        // Implement your update logic to persist any changes.
        JOptionPane.showMessageDialog(this, "Booking updated successfully!");
    }
}
