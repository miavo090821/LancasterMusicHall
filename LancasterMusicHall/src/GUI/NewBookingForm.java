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
    private JTextField streetAddressField; // new
    private JTextField cityField;           // new
    private JTextField postcodeField;       // new
    private JCheckBox saveCompanyCheck;
    private JButton contractUploadButton;
    private File contractFile = null;

    // --- Contract Details Fields ---
    private JTextArea contractDetailsArea;
    private JLabel contractFileLabel;

    // --- Booking Details Fields ---
    private JTextField eventNameField;
    private JTextField bookingStartDateField;
    private JTextField bookingEndDateField;
    private JCheckBox confirmedCheck;

    // --- Container for multiple Event Details Panels ---
    private JPanel eventsContainer;
    private JButton addEventButton;
    private List<EventDetailPanel> eventPanels;

    // --- Pricing Panel Fields ---
    private JLabel customerBillTotalLabel;
    private JTextField ticketPriceField;
    private JTextField customerAccountField;
    private JTextField customerSortCodeField;
    private JTextField paymentDueDateField;
    private JComboBox<String> paymentStatusCombo;
    private JTextField maxDiscountField; // new

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
        streetAddressField = new JTextField(); // new
        cityField = new JTextField();           // new
        postcodeField = new JTextField();       // new
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
        clientPanel.add(new JLabel("Street Address:")); // new
        clientPanel.add(streetAddressField);            // new
        clientPanel.add(new JLabel("City:"));             // new
        clientPanel.add(cityField);                       // new
        clientPanel.add(new JLabel("Postcode:"));         // new
        clientPanel.add(postcodeField);                   // new
        clientPanel.add(saveCompanyCheck);
        clientPanel.add(new JLabel());
        clientPanel.add(new JLabel("Contract Upload:"));
        clientPanel.add(contractUploadButton);

        // --- Contract Details Panel ---
        JPanel contractPanel = new JPanel(new BorderLayout(5, 5));
        contractPanel.setBorder(BorderFactory.createTitledBorder("Contract Details"));
        contractDetailsArea = new JTextArea(4, 20);
        JScrollPane contractScrollPane = new JScrollPane(contractDetailsArea);
        contractPanel.add(new JLabel("Enter contract details:"), BorderLayout.NORTH);
        contractPanel.add(contractScrollPane, BorderLayout.CENTER);
        contractFileLabel = new JLabel("No contract file uploaded.");
        contractPanel.add(contractFileLabel, BorderLayout.SOUTH);

        // --- Booking Details Panel ---
        JPanel bookingPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        bookingPanel.setBorder(BorderFactory.createTitledBorder("Booking Details"));
        eventNameField = new JTextField();
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
        maxDiscountField = new JTextField(); // new
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
        pricingPanel.add(new JLabel("Max Discount:")); // new
        pricingPanel.add(maxDiscountField);            // new
        pricingPanel.add(new JLabel("Payment Status:"));
        pricingPanel.add(paymentStatusCombo);

        // --- Submit Booking Button ---
        submitButton = new JButton("Submit Booking");
        submitButton.addActionListener(this::handleSubmitBooking);

        // Add all panels to main panel.
        mainPanel.add(clientPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(contractPanel);
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
            contractFileLabel.setText("Uploaded: " + contractFile.getName());
            JOptionPane.showMessageDialog(this, "Selected contract: " + contractFile.getName());
        }
    }

    private void handleSubmitBooking(ActionEvent e) {
        try {
            // Collect Client details.
            String companyName = companyNameField.getText().trim();
            String primaryContact = primaryContactField.getText().trim();
            String telephone = telephoneField.getText().trim();
            String email = emailField.getText().trim();
            String streetAddress = streetAddressField.getText().trim();
            String city = cityField.getText().trim();
            String postcode = postcodeField.getText().trim();

            // Collect Booking details.
            String bookingEventName = eventNameField.getText().trim();
            LocalDate bookingStartDate = LocalDate.parse(bookingStartDateField.getText().trim(), DATE_FORMATTER);
            LocalDate bookingEndDate = LocalDate.parse(bookingEndDateField.getText().trim(), DATE_FORMATTER);
            boolean confirmed = confirmedCheck.isSelected();

            List<Event> events = new ArrayList<>();
            double totalBill = 0.0;
            for (EventDetailPanel panel : eventPanels) {
                Event event = panel.getEvent();
                if (event != null) {
                    events.add(event);
                    totalBill += event.getPrice();
                }
            }
            customerBillTotalLabel.setText("Customer Bill Total: £" + totalBill);

            // Pricing and discount details.
            double ticketPrice = Double.parseDouble(ticketPriceField.getText().trim());
            String customerAccount = customerAccountField.getText().trim();
            String customerSortCode = customerSortCodeField.getText().trim();
            LocalDate paymentDueDate = LocalDate.parse(paymentDueDateField.getText().trim(), DATE_FORMATTER);
            double maxDiscount = Double.parseDouble(maxDiscountField.getText().trim());
            String paymentStatus = (String) paymentStatusCombo.getSelectedItem();

            // Contract details.
            String contractDetails = contractDetailsArea.getText().trim();

            // Retrieve the logged-in staff id (or default to 0 for guests).
            Integer staffId = sqlCon.getCurrentStaffId();
            if (staffId == null) {
                staffId = 0;
            }

            // Call insertFullBooking with all the details.
            // Note: totalBill is passed as the total_cost for the booking.
            boolean success = sqlCon.insertFullBooking(
                    bookingEventName,
                    bookingStartDate,
                    bookingEndDate,
                    confirmed ? "confirmed" : "held",
                    companyName,
                    primaryContact,
                    telephone,
                    email,
                    events,
                    totalBill,           // total_cost from customer bill
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
                    maxDiscount,
                    staffId
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
        // New fields for event description and layout:
        private JTextField descriptionField;
        private JTextField layoutField;

        public EventDetailPanel() {
            setLayout(new GridLayout(0, 2, 5, 5));
            setBorder(BorderFactory.createTitledBorder("Event Detail"));

            eventStartDateField = new JTextField("01/04/2025");
            eventEndDateField = new JTextField("01/04/2025");
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
            add(layoutField);                   // new
            add(calcPriceButton);
            add(eventPriceLabel);
        }

        // Calculate price using SQLConnection pricing functions.
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
                    // Ignore if parsing fails.
                }
            }
            customerBillTotalLabel.setText("Customer Bill Total: £" + totalBill);
        }

        // Collect data from this panel and return an Event object.
        // Returns an Event object including the new description and layout fields.
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
                String companyName = NewBookingForm.this.companyNameField.getText().trim();

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
                        description,       // new description field
                        layout             // new layout field
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error collecting event details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }

    // Mapping method for venue ID remains unchanged.
    private int mapLocationToVenueId(String location) {
        switch (location) {
            case "Venue": return 1;
            case "The Green Room": return 2;
            case "Room Brontë Boardroom": return 3;
            case "Room Dickens Den": return 4;
            case "Room Poe Parlor": return 5;
            case "Room Globe Room": return 6;
            case "Chekhov Chamber": return 7;
            case "Main_Hall": return 8;
            case "Small_Hall": return 9;
            case "Rehearsal_Space": return 10;
            default: return 1;
        }
    }
}
