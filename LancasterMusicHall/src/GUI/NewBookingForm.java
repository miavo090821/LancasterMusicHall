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
            updateCustomerBillTotal();
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

    // On submit, collect client, booking, and event details from all panels.
    private void handleSubmitBooking(ActionEvent e) {
        try {
            // Collect Client details.
            String companyName = companyNameField.getText().trim();
            String primaryContact = primaryContactField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String email = emailField.getText().trim();

            // Collect Booking details.
            String bookingEventName = eventNameField.getText().trim();
            LocalDate bookingStartDate = LocalDate.parse(bookingStartDateField.getText().trim(), DATE_FORMATTER);
            LocalDate bookingEndDate = LocalDate.parse(bookingEndDateField.getText().trim(), DATE_FORMATTER);
            boolean confirmed = confirmedCheck.isSelected();

            // Prepare a list for event objects and recalc total.
            List<Event> events = new ArrayList<>();
            double totalBill = 0.0;
            for (EventDetailPanel panel : eventPanels) {
                Event event = panel.getEvent();
                if (event != null) {
                    events.add(event);
                    totalBill += event.getPrice();
                }
            }
            // Update the customer bill total label.
            customerBillTotalLabel.setText("Customer Bill Total: £" + totalBill);

            // Overall pricing details.
            double ticketPrice = Double.parseDouble(ticketPriceField.getText().trim());
            String customerAccount = customerAccountField.getText().trim();
            LocalDate paymentDueDate = LocalDate.parse(paymentDueDateField.getText().trim(), DATE_FORMATTER);
            String paymentStatus = (String) paymentStatusCombo.getSelectedItem();

            // Call the SQLConnection.insertFullBooking method (must be implemented in SQLConnection)
            boolean success = sqlCon.insertFullBooking(
                    bookingEventName,
                    bookingStartDate,
                    bookingEndDate,
                    confirmed ? "confirmed" : "held",
                    companyName,
                    primaryContact,
                    telephone,
                    email,
                    events, // list of event objects
                    totalBill, // total calculated from events
                    ticketPrice,
                    customerAccount,
                    paymentDueDate,
                    paymentStatus,
                    contractFile
            );
            if (success) {
                JOptionPane.showMessageDialog(this, "Booking submitted successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to submit booking.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting booking: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Inner Class: EventDetailPanel ---
    private class EventDetailPanel extends JPanel {
        private JTextField eventStartDateField;
        private JTextField eventEndDateField;
        private JComboBox<String> locationCombo;
        private JTextField eventStartTimeField;
        private JTextField eventEndTimeField;
        private JComboBox<String> eventTypeCombo;
        private JButton calcPriceButton;
        private JLabel eventPriceLabel;

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
            add(calcPriceButton);
            add(eventPriceLabel);
        }

        // Calculate price using SQLConnection pricing functions.

        // A simple class to store the rates for each room.
        class RoomRate {
            double hourly;
            double morningAfternoon;
            double allDay;
            double week;

            public RoomRate(double hourly, double morningAfternoon, double allDay, double week) {
                this.hourly = hourly;
                this.morningAfternoon = morningAfternoon;
                this.allDay = allDay;
                this.week = week;
            }
        }

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


        // Collect data from this panel and return an Event object.
        public Event getEvent() {
            try {
                LocalDate startDate = LocalDate.parse(eventStartDateField.getText().trim(), DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(eventEndDateField.getText().trim(), DATE_FORMATTER);
                String eventType = (String) eventTypeCombo.getSelectedItem();
                String location = (String) locationCombo.getSelectedItem();
                LocalTime startTime = LocalTime.parse(eventStartTimeField.getText().trim());
                LocalTime endTime = LocalTime.parse(eventEndTimeField.getText().trim());
                int venueId = mapLocationToVenueId(location);
                Venue venue = new Venue(venueId, location, "Type", 0);
                // Parse the price (remove the currency symbol).
                double price = Double.parseDouble(eventPriceLabel.getText().replaceAll("[£]", ""));
                // Create a new Event object.
                return new Event(
                        0,               // id
                        "",              // name (could be set from the booking details)
                        eventType,       // eventType
                        startDate,
                        endDate,
                        startTime,
                        endTime,
                        false,           // held
                        "",              // holdExpiryDate
                        venue,
                        null,            // seats (not set)
                        "",              // bookedBy (set later)
                        location,        // room (use location)
                        "",              // companyName (set later)
                        null,            // contactDetails (set later)
                        price
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error collecting event details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }

    // Mapping method: returns unique venue ID based on location.
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
}
