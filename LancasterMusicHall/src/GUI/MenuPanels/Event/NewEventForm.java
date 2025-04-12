package GUI.MenuPanels.Event;

import Database.SQLConnection;
import operations.entities.Event;
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

/**
 * The NewEventForm class represents a dialog form used for creating a new event.
 * It contains fields for client details, event details, multiple event panels, and pricing.
 * The form connects to a SQL database via the provided SQLConnection.
 */
public class NewEventForm extends JDialog {
    /**
     * Database connection handler for all event-related database operations.
     * <p>
     * <b>Security:</b> Must be initialized with authenticated connection.
     * Never exposed publicly.
     */
    private SQLConnection sqlCon;

    /**
     * Thread-safe formatter for date display/parsing in UK format (day/month/year).
     * <p>
     * <b>Format:</b> Strictly follows "dd/MM/yyyy" pattern
     * <b>Usage:</b> Used for all date fields in the form
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

// --- Client Details Fields ---

    /**
     * Input field for client's legal business name.
     * <p>
     * <b>Validation:</b> Required field, max 100 chars
     * <b>Business Rule:</b> Used for contract generation
     */
    private JTextField companyNameField;

    /**
     * Input field for primary contact person's full name.
     * <p>
     * <b>Validation:</b> Required field, max 50 chars
     */
    private JTextField primaryContactField;

    /**
     * Input field for client's contact telephone number.
     * <p>
     * <b>Format:</b> Accepts any phone number format
     * <b>Business Rule:</b> Used for urgent event notifications
     */
    private JTextField telephoneField;

    /**
     * Input field for client's email address.
     * <p>
     * <b>Validation:</b> Basic email format validation
     * <b>Business Rule:</b> Primary communication channel
     */
    private JTextField emailField;

    /**
     * Checkbox to persist client details in database for future bookings.
     * <p>
     * <b>Privacy:</b> When checked, stores all client details
     * <b>Default:</b> Unchecked (opt-in)
     */
    private JCheckBox saveCompanyCheck;

    /**
     * Button to initiate contract document upload process.
     * <p>
     * <b>Supported Formats:</b> PDF, DOC, DOCX (max 10MB)
     * <b>Security:</b> Local file system access only
     */
    private JButton contractUploadButton;

    /**
     * Reference to uploaded contract document file.
     * <p>
     * <b>Lifecycle:</b> Stored as reference until form submission
     * <b>Null State:</b> Indicates no contract uploaded
     */
    private File contractFile = null;

// --- Event Details Fields ---

    /**
     * Unique booking reference identifier field.
     * <p>
     * <b>Format:</b> Auto-generated if empty
     * <b>Business Rule:</b> Must be unique across all events
     */
    private JTextField bookingIDField;

    /**
     * Input field for event start date in UK format.
     * <p>
     * <b>Format:</b> dd/MM/yyyy
     * <b>Validation:</b> Must be current or future date
     */
    private JTextField eventStartDateField;

    /**
     * Input field for event end date in UK format.
     * <p>
     * <b>Business Rule:</b> Must be ≥ start date
     * <b>Default:</b> Matches start date initially
     */
    private JTextField eventEndDateField;

    /**
     * Checkbox indicating booking confirmation status.
     * <p>
     * <b>Business Rule:</b> Unconfirmed events may be auto-cancelled
     * <b>Default:</b> Checked (confirmed)
     */
    private JCheckBox confirmedCheck;

// --- Event Panels Container ---

    /**
     * Container panel for holding multiple event detail panels.
     * <p>
     * <b>UI:</b> Uses vertical BoxLayout
     * <b>Dynamic:</b> Grows with added events
     */
    private JPanel eventsContainer;

    /**
     * Button to add additional event panels to the booking.
     * <p>
     * <b>Business Rule:</b> Max 10 events per booking
     * <b>UI Effect:</b> Triggers revalidation of container
     */
    private JButton addEventButton;

    /**
     * Collection of all active event detail panels.
     * <p>
     * <b>Invariant:</b> Always contains at least one panel
     * <b>Lifecycle:</b> Managed by add/remove operations
     */
    private List<EventDetailPanel> eventPanels;

// --- Pricing Fields ---

    /**
     * Display label showing cumulative total of all events.
     * <p>
     * <b>Format:</b> "Customer Bill Total: £X.XX"
     * <b>Behavior:</b> Auto-updates on price changes
     */
    private JLabel customerBillTotalLabel;

    /**
     * Input field for per-ticket price (if applicable).
     * <p>
     * <b>Business Rule:</b> Optional for non-ticketed events
     * <b>Validation:</b> Must be positive number if provided
     */
    private JTextField ticketPriceField;

    /**
     * Input field for client's bank account number.
     * <p>
     * <b>Security:</b> Stored as plaintext - consider encryption
     * <b>Validation:</b> UK account number rules
     */
    private JTextField customerAccountField;

    /**
     * Input field for client's bank sort code.
     * <p>
     * <b>Format:</b> XX-XX-XX or XXXXXX
     * <b>Privacy:</b> Sensitive financial data
     */
    private JTextField customerSortCodeField;

    /**
     * Input field for payment due date in UK format.
     * <p>
     * <b>Business Rule:</b> Defaults to 10 days after submission
     * <b>Format:</b> dd/MM/yyyy
     */
    private JTextField paymentDueDateField;

    /**
     * Dropdown selector for payment status.
     * <p>
     * <b>Options:</b> Paid, Pending, Overdue
     * <b>Business Rule:</b> Affects invoice generation
     */
    private JComboBox<String> paymentStatusCombo;

// --- Submission Control ---

    /**
     * Primary submission button for the entire booking.
     * <p>
     * <b>Workflow:</b> Validates all data before submission
     * <b>Behavior:</b> Disabled during processing
     */
    private JButton submitButton;

    /**
     * Constructs a new NewEventForm dialog with the specified owner and SQL connection.
     *
     * @param owner  the Frame that owns this dialog
     * @param sqlCon the SQLConnection to communicate with the database
     */
    public NewEventForm(Frame owner, SQLConnection sqlCon) {
        super(owner, "New event", true);
        this.sqlCon = sqlCon;
        eventPanels = new ArrayList<>();
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Initializes the GUI components of the form, sets up layout and event listeners.
     */
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

        // --- Event Details Panel ---
        JPanel eventPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        eventPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        bookingIDField = new JTextField();
        // Set default text in dd/MM/yyyy format.
        eventStartDateField = new JTextField("01/04/2025");
        eventEndDateField = new JTextField("01/04/2025");
        confirmedCheck = new JCheckBox("Confirmed", true);

        eventPanel.add(new JLabel("Booking ID:"));
        eventPanel.add(bookingIDField);
        eventPanel.add(new JLabel("Status:"));
        eventPanel.add(confirmedCheck);

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
            updateCustomerBillTotal();
        });

        // --- Pricing Panel ---
        JPanel pricingPanel = new JPanel(new GridLayout(0, 2, 6, 6));
        pricingPanel.setBorder(BorderFactory.createTitledBorder("Pricing"));
        customerBillTotalLabel = new JLabel("Customer Bill Total: £0.00");
        ticketPriceField = new JTextField();
        customerAccountField = new JTextField();
        customerSortCodeField = new JTextField();
        paymentDueDateField = new JTextField("10/04/2025");
        String[] paymentStatusOptions = {"Paid", "Pending", "Overdue"};
        paymentStatusCombo = new JComboBox<>(paymentStatusOptions);

        pricingPanel.add(new JLabel("Customer Bill Total:"));
        pricingPanel.add(customerBillTotalLabel);
        pricingPanel.add(new JLabel("Ticket Price:"));
        pricingPanel.add(ticketPriceField);
        pricingPanel.add(new JLabel("Customer Account Number:"));
        pricingPanel.add(customerAccountField);
        pricingPanel.add(new JLabel("Customer Sort Code:"));
        pricingPanel.add(customerSortCodeField);
        pricingPanel.add(new JLabel("Payment Due Date (dd/MM/yyyy):"));
        pricingPanel.add(paymentDueDateField);
        pricingPanel.add(new JLabel("Payment Status:"));
        pricingPanel.add(paymentStatusCombo);

        // --- Submit Event Button ---
        submitButton = new JButton("Submit event");
        // submitButton.addActionListener(this::handleSubmitevent); // Uncomment when implementing submit logic

        // Add all panels to the main panel.
        mainPanel.add(clientPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(eventPanel);
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

    /**
     * Updates the customer bill total based on the prices of the individual event panels.
     */
    private void updateCustomerBillTotal() {
        double totalBill = 0.0;
        for (EventDetailPanel panel : eventPanels) {
            Event event = panel.getEvent();
            if (event != null) {
                totalBill += event.getPrice();
            }
        }
        customerBillTotalLabel.setText("Customer Bill Total: £" + totalBill);
    }

    /**
     * Creates and adds a new EventDetailPanel to the events container.
     */
    private void addNewEventPanel() {
        EventDetailPanel eventPanel = new EventDetailPanel();
        eventPanels.add(eventPanel);
        eventsContainer.add(eventPanel);
    }

    /**
     * Handles the contract file upload action by showing a JFileChooser dialog.
     *
     * @param e the ActionEvent triggered by the upload button
     */
    private void handleContractUpload(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            contractFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Selected contract: " + contractFile.getName());
        }
    }


    /**
     * Maps a given location string to a unique venue ID.
     *
     * @param location the location string
     * @return the corresponding venue ID
     */
    private int mapLocationToVenueId(String location) {
        switch (location) {
            case "Venue":
                return 1;
            case "The Green Room":
                return 2;
            case "Room Brontë Boardroom":
                return 3;
            case "Room Dickens Den":
                return 4;
            case "Room Poe Parlor":
                return 5;
            case "Room Globe Room":
                return 6;
            case "Chekhov Chamber":
                return 7;
            case "Main_Hall":
                return 8;
            case "Small_Hall":
                return 9;
            case "Rehearsal_Space":
                return 10;
            default:
                return 1;
        }
    }

    // --- Inner Class: EventDetailPanel ---

    /**
     * The EventDetailPanel class represents an individual panel containing the details
     * for a single event, such as dates, times, location, event type, description, and layout.
     */
    private class EventDetailPanel extends JPanel {
        private JTextField eventStartDateField;
        private JTextField eventEndDateField;
        private JComboBox<String> locationCombo;
        private JTextField eventStartTimeField;
        private JTextField eventEndTimeField;
        private JComboBox<String> eventTypeCombo;
        private JButton calcPriceButton;
        private JLabel eventPriceLabel;
        // New fields for event description and layout:
        private JTextField descriptionField;
        private JTextField layoutField;

        /**
         * Constructs a new EventDetailPanel with default values and layout.
         */
        public EventDetailPanel() {
            setLayout(new GridLayout(0, 2, 5, 5));
            setBorder(BorderFactory.createTitledBorder("Event Detail"));

            // Set default dates in dd/MM/yyyy format.
            eventStartDateField = new JTextField("01/04/2025");
            eventEndDateField = new JTextField("01/04/2025");
            // Updated locations per your mapping.
            String[] locations = {"Venue", "The Green Room", "Room Brontë Boardroom", "Room Dickens Den", "Room Poe Parlor", "Room Globe Room", "Chekhov Chamber", "Main_Hall", "Small_Hall", "Rehearsal_Space"};
            locationCombo = new JComboBox<>(locations);
            eventStartTimeField = new JTextField("10:00");
            eventEndTimeField = new JTextField("12:00");
            String[] eventTypes = {"Film", "Show", "Meeting"};
            eventTypeCombo = new JComboBox<>(eventTypes);
            calcPriceButton = new JButton("Calc Price");
            eventPriceLabel = new JLabel("£0.00");

            // New input fields for Description and Layout.
            descriptionField = new JTextField();
            layoutField = new JTextField();

            calcPriceButton.addActionListener(e -> {
                double price = calculateEventPrice();
                eventPriceLabel.setText("£" + price);
                // Optionally update the overall customer total here:
                updateCustomerBillTotal();
            });

            add(new JLabel("Event Start Date (dd/MM/yyyy):"));
            add(eventStartDateField);
            add(new JLabel("Event End Date (dd/MM/yyyy):"));
            add(eventEndDateField);
            add(new JLabel("Location:"));
            add(locationCombo);
            add(new JLabel("Event Start Time (HH:mm):"));
            add(eventStartTimeField);
            add(new JLabel("Event End Time (HH:mm):"));
            add(eventEndTimeField);
            add(new JLabel("Event Type:"));
            add(eventTypeCombo);
            add(new JLabel("Description:"));  // new
            add(descriptionField);              // new
            add(new JLabel("Layout:"));         // new
            add(calcPriceButton);
            add(eventPriceLabel);
        }

        /**
         * Represents rates for a given room.
         */
        class RoomRate {
            double hourly;
            double morningAfternoon;
            double allDay;
            double week;

            /**
             * Constructs a RoomRate with specified rates.
             *
             * @param hourly          the hourly rate
             * @param morningAfternoon the rate for morning/afternoon sessions
             * @param allDay          the rate for a full day
             * @param week            the weekly rate
             */
            public RoomRate(double hourly, double morningAfternoon, double allDay, double week) {
                this.hourly = hourly;
                this.morningAfternoon = morningAfternoon;
                this.allDay = allDay;
                this.week = week;
            }
        }

        /**
         * Returns the RoomRate for a given room name.
         *
         * @param roomName the name of the room
         * @return the RoomRate object for that room
         */
        private RoomRate getRoomRate(String roomName) {
            // Return the correct RoomRate based on roomName.
            switch (roomName) {
                case "The Green Room":
                    return new RoomRate(25, 75, 130, 600);
                case "Brontë Boardroom":
                    return new RoomRate(120, 200, 650, 900);
                case "Dickens Den":
                    return new RoomRate(40, 75, 130, 500);
                case "Poe Parlor":
                    return new RoomRate(50, 100, 150, 700);
                case "Globe Room":
                    return new RoomRate(150, 250, 800, 1200);
                case "Chekhov Chamber":
                    return new RoomRate(38, 110, 160, 850);
                default:
                    // You might throw an exception or return a default rate if room is not found.
                    return new RoomRate(0, 0, 0, 0);
            }
        }

        /**
         * Calculates the price of the event based on dates, times, location, and room rates.
         *
         * @return the calculated price as a double
         */
        private double calculateEventPrice() {
            double price = 0.0;
            try {
                LocalDate startDate = LocalDate.parse(eventStartDateField.getText().trim(), DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(eventEndDateField.getText().trim(), DATE_FORMATTER);
                LocalTime startTime = LocalTime.parse(eventStartTimeField.getText().trim());
                LocalTime endTime = LocalTime.parse(eventEndTimeField.getText().trim());

                long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
                int totalDays = (daysDiff == 0) ? 1 : (int) daysDiff + 1;
                long hours = java.time.Duration.between(startTime, endTime).toHours();

                String locationRaw = (String) locationCombo.getSelectedItem();
                String location = locationRaw.replace("Room ", "").trim();
                int venueId = mapLocationToVenueId(locationRaw);
                java.sql.Date sqlDate = java.sql.Date.valueOf(startDate);

                switch (location) {
                    case "Main_Hall":
                        if (totalDays == 1) {
                            if (startTime.getHour() >= 17) {
                                price = sqlCon.calculateMainHallCost(sqlDate, "evening", 0);
                            } else {
                                price = sqlCon.calculateMainHallCost(sqlDate, "hourly", (int) Math.max(hours, 3));
                            }
                        } else {
                            price = sqlCon.calculateMainHallCost(sqlDate, "daily", 0) * totalDays;
                        }
                        break;

                    case "Small_Hall":
                        if (totalDays == 1) {
                            if (startTime.getHour() >= 17) {
                                price = sqlCon.calculateSmallHallCost(sqlDate, "evening", 0);
                            } else {
                                price = sqlCon.calculateSmallHallCost(sqlDate, "hourly", (int) Math.max(hours, 3));
                            }
                        } else {
                            price = sqlCon.calculateSmallHallCost(sqlDate, "daily", 0) * totalDays;
                        }
                        break;

                    case "Rehearsal_Space":
                        if (totalDays == 1) {
                            price = sqlCon.calculateRehearsalCost(sqlDate, "hourly", (int) Math.max(hours, 3));
                        } else if (totalDays == 7) {
                            price = sqlCon.calculateRehearsalCost(sqlDate, "weekly_long", 0);
                        } else {
                            price = sqlCon.calculateRehearsalCost(sqlDate, "daily_long", 0) * totalDays;
                        }
                        break;

                    case "Venue":
                        if (totalDays == 1) {
                            if (startTime.getHour() >= 17) {
                                price = sqlCon.calculateVenueCost(sqlDate, "evening");
                            } else {
                                price = sqlCon.calculateVenueCost(sqlDate, "full_day");
                            }
                        } else {
                            price = sqlCon.calculateVenueCost(sqlDate, "full_day") * totalDays;
                        }
                        break;

                    case "The Green Room":
                    case "Brontë Boardroom":
                    case "Dickens Den":
                    case "Poe Parlor":
                    case "Globe Room":
                    case "Chekhov Chamber":
                        if (totalDays == 1) {
                            String duration;
                            if (hours <= 1) {
                                duration = "1 Hour";
                            } else if (hours <= 4) {
                                duration = "Morning/Afternoon";
                            } else {
                                duration = "All Day";
                            }
                            price = sqlCon.calculateRoomCost(venueId, location, duration);
                        } else if (totalDays == 7) {
                            price = sqlCon.calculateRoomCost(venueId, location, "Week");
                        } else {
                            price = sqlCon.calculateRoomCost(venueId, location, "All Day") * totalDays;
                        }
                        break;

                    default:
                        // Fallback to main hall hourly
                        price = sqlCon.calculateMainHallCost(sqlDate, "hourly", (int) Math.max(hours, 3));
                        break;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error calculating event price: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return price;
        }

        /**
         * Updates the overall customer bill total based on the prices in each EventDetailPanel.
         */
        private void updateCustomerBillTotal() {
            double totalBill = 0.0;
            for (EventDetailPanel panel : eventPanels) {
                try {
                    String priceStr = panel.eventPriceLabel.getText().replaceAll("[£]", "").trim();
                    totalBill += Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    // Ignore if parsing fails
                }
            }
            customerBillTotalLabel.setText("Customer Bill Total: £" + totalBill);
        }

        /**
         * Collects data from the panel and returns an Event object.
         *
         * @return the Event object representing the event details from this panel, or null if an error occurs
         */
        public Event getEvent() {
            try {
                LocalDate startDate = LocalDate.parse(eventStartDateField.getText().trim(), DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(eventEndDateField.getText().trim(), DATE_FORMATTER);
                String eventType = (String) eventTypeCombo.getSelectedItem();
                String location = (String) locationCombo.getSelectedItem();
                LocalTime startTime = LocalTime.parse(eventStartTimeField.getText().trim());
                LocalTime endTime = LocalTime.parse(eventEndTimeField.getText().trim());
                int venueId = mapLocationToVenueId(location);
                Venue venue = new Venue(venueId, location, location, 0, "N/A", false, false, 0.0);
                double price = Double.parseDouble(eventPriceLabel.getText().replaceAll("[£]", ""));
                String description = descriptionField.getText().trim(); // new
                String layout = layoutField.getText().trim();           // new

                // Use the outer form's company name field for companyName.
                String companyName = NewEventForm.this.companyNameField.getText().trim();

                return new Event(
                        0,                 // id (to be generated)
                        "",                // name (can be set later)
                        eventType,         // eventType
                        startDate,         // startDate
                        endDate,           // endDate
                        startTime,         // startTime
                        endTime,           // endTime
                        false,             // held flag
                        "",                // holdExpiryDate (if not applicable)
                        venue,             // venue
                        null,              // seats (if not applicable)
                        "",                // bookedBy (placeholder if needed)
                        location,          // room (using location as room name)
                        companyName,       // companyName (from outer form)
                        null,              // contactDetails (if not applicable)
                        price,             // price
                        description,       // description field
                        layout             // layout field
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error collecting event details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }
}
