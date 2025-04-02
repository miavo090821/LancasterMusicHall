package GUI;

import Database.SQLConnection;
import operations.entities.Activities.Event;
import operations.entities.Venue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NewBookingForm extends JDialog {
    private SQLConnection sqlCon;

    // Date formatter for dd/MM/yyyy format.
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // --- Client Details Fields ---
    private JTextField companyNameField;
    private JTextField primaryContactField;
    private JTextField telephoneField;
    private JTextField emailField;
    private JCheckBox saveCompanyCheck;
    private JButton contractUploadButton;
    private File contractFile = null;

    // --- Booking Details Fields ---
    private JTextField eventNameField;
    private JTextField bookingStartDateField;  // dd/MM/yyyy
    private JTextField bookingEndDateField;    // dd/MM/yyyy
    private JCheckBox confirmedCheck;

    // --- Container for multiple Event Details Panels ---
    private JPanel eventsContainer;
    private JButton addEventButton;
    private List<EventDetailPanel> eventPanels;

    // --- Pricing Panel Fields (for overall booking) ---
    private JLabel customerBillTotalLabel;   // automatically calculated total
    private JTextField ticketPriceField;
    private JTextField customerAccountField;
    private JTextField paymentDueDateField;  // dd/MM/yyyy
    private JComboBox<String> paymentStatusCombo; // Paid, Pending, Overdue

    // --- Submit Booking Button ---
    private JButton submitButton;

    public NewBookingForm(Frame owner, SQLConnection sqlCon) {
        super(owner, "New Booking", true);
        this.sqlCon = sqlCon;
        eventPanels = new ArrayList<>();
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        // Main panel with vertical BoxLayout.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Client Details Panel ---
        JPanel clientPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        clientPanel.setBorder(BorderFactory.createTitledBorder("Client Details"));
        companyNameField = new JTextField();
        primaryContactField = new JTextField();
        telephoneField = new JTextField();
        emailField = new JTextField();
        saveCompanyCheck = new JCheckBox("Save company details for future use");
        contractUploadButton = new JButton("Upload Contract");
        contractUploadButton.addActionListener(this::handleContractUpload);

        clientPanel.add(new JLabel("Company Name:"));
        clientPanel.add(companyNameField);
        clientPanel.add(new JLabel("Primary Contact:"));
        clientPanel.add(primaryContactField);
        clientPanel.add(new JLabel("Telephone:"));
        clientPanel.add(telephoneField);
        clientPanel.add(new JLabel("Email:"));
        clientPanel.add(emailField);
        clientPanel.add(saveCompanyCheck);
        clientPanel.add(new JLabel());  // placeholder
        clientPanel.add(new JLabel("Contract Upload:"));
        clientPanel.add(contractUploadButton);

        // --- Booking Details Panel ---
        JPanel bookingPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        eventNameField = new JTextField();
        // Set default text in dd/MM/yyyy format.
        bookingStartDateField = new JTextField("01/04/2025");
        bookingEndDateField = new JTextField("01/04/2025");
        confirmedCheck = new JCheckBox("Confirmed", true);

        bookingPanel.add(new JLabel("Event Name:"));
        bookingPanel.add(eventNameField);
        bookingPanel.add(new JLabel("Booking Start Date (dd/MM/yyyy):"));
        bookingPanel.add(bookingStartDateField);
        bookingPanel.add(new JLabel("Booking End Date (dd/MM/yyyy):"));
        bookingPanel.add(bookingEndDateField);
        bookingPanel.add(new JLabel("Status:"));
        bookingPanel.add(confirmedCheck);

        // --- Event Details Container ---
        eventsContainer = new JPanel();
        eventsContainer.setLayout(new BoxLayout(eventsContainer, BoxLayout.Y_AXIS));
        eventsContainer.setBorder(BorderFactory.createTitledBorder("Event Details"));
        // Add one event detail panel by default.
        addNewEventPanel();

        addEventButton = new JButton("Add Another Event");
        addEventButton.addActionListener(e -> {
            addNewEventPanel();
            eventsContainer.revalidate();
            eventsContainer.repaint();
        });

        // --- Pricing Panel ---
        JPanel pricingPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        pricingPanel.setBorder(BorderFactory.createTitledBorder("Pricing"));
        customerBillTotalLabel = new JLabel("Customer Bill Total: £0.00");
        ticketPriceField = new JTextField();
        customerAccountField = new JTextField();
        paymentDueDateField = new JTextField("10/04/2025");
        String[] paymentStatusOptions = {"Paid", "Pending", "Overdue"};
        paymentStatusCombo = new JComboBox<>(paymentStatusOptions);

        pricingPanel.add(new JLabel("Customer Bill Total:"));
        pricingPanel.add(customerBillTotalLabel);
        pricingPanel.add(new JLabel("Ticket Price:"));
        pricingPanel.add(ticketPriceField);
        pricingPanel.add(new JLabel("Customer Account Number:"));
        pricingPanel.add(customerAccountField);
        pricingPanel.add(new JLabel("Payment Due Date (dd/MM/yyyy):"));
        pricingPanel.add(paymentDueDateField);
        pricingPanel.add(new JLabel("Payment Status:"));
        pricingPanel.add(paymentStatusCombo);

        // --- Submit Booking Button ---
        submitButton = new JButton("Submit Booking");
        submitButton.addActionListener(this::handleSubmitBooking);

        // Add all panels to the main panel.
        mainPanel.add(clientPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(bookingPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(eventsContainer);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(addEventButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(pricingPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(submitButton);

        // Wrap the main panel in a scroll pane.
        JScrollPane scrollPane = new JScrollPane(mainPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(500, 600));
        setContentPane(scrollPane);
    }

    // Creates and adds a new EventDetailPanel to the events container.
    private void addNewEventPanel() {
        EventDetailPanel eventPanel = new EventDetailPanel();
        eventPanels.add(eventPanel);
        eventsContainer.add(eventPanel);
    }

    private void handleContractUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            contractFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected contract: " + contractFile.getName());
        }
    }


}
