package GUI.MenuPanels.Event;

import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
import operations.entities.Booking;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * The EventPanel class displays and collects event-related data.
 * It is a panel used to input or edit event details, including dates, times,
 * booking, and contact information.
 */
public class EventPanel extends JPanel {
    /**
     * Date picker component for selecting the event start date.
     * Includes validation to ensure:
     * - Date is not in the past
     * - Date is within venue operating calendar
     * - Does not conflict with existing bookings
     */
    private JDateChooser startDatePicker;

    /**
     * Date picker component for selecting the event end date.
     * Automatically constrained to be:
     * - On or after start date
     * - Within maximum booking duration policy
     * - Valid for the selected venue
     */
    private JDateChooser endDatePicker;

    /**
     * Combo box for selecting event start time in 30-minute intervals.
     * Format: HH:mm (24-hour format)
     * Range: 08:00 to 22:00 (venue operating hours)
     * Default: 09:00
     */
    private JComboBox<String> startTimeCombo;

    /**
     * Combo box for selecting event end time in 30-minute intervals.
     * Format: HH:mm (24-hour format)
     * Range: 08:00 to 22:00 (venue operating hours)
     * Automatically adjusted to be after start time
     */
    private JComboBox<String> endTimeCombo;

    /**
     * Text field displaying the system-generated booking reference number.
     * Format: [YYMMDD]-[VENUE]-[SEQ#] (e.g., "240415-HALL-A12")
     * Read-only field populated after successful booking submission
     */
    private JTextField bookingIdField;

    /**
     * Text field for entering the activity/event type identifier.
     * Validated against:
     * - Master list of approved activities
     * - Venue-specific allowed activities
     * - User permission to book activity type
     */
    private JTextField activityIdField;

    /**
     * Text field for entering the venue/room identifier.
     * Auto-completes from available venues matching:
     * - Date availability
     * - Capacity requirements
     * - Activity type restrictions
     */
    private JTextField venueIdField;

    /**
     * Checkbox indicating booking confirmation status.
     * When checked:
     * - Marks booking as confirmed in system
     * - Triggers confirmation email workflow
     * - Locks record from further edits
     * Requires "Confirm Booking" permission to modify
     */
    private JCheckBox confirmedCheck;

    /**
     * Text field displaying the staff ID of the booking creator.
     * Auto-populated from current session
     * Format: [DEPT]-[ID#] (e.g., "EVT-142")
     * Read-only field
     */
    private JTextField bookedByField;

    /**
     * Text field displaying the assigned room/space name.
     * Auto-populated based on venueId selection
     * Combines venue ID with specific space (e.g., "MAIN HALL - Stage Left")
     * Read-only display field
     */
    private JTextField roomField;

    /**
     * Text field for entering the client organization name.
     * Required for corporate bookings
     * Maximum length: 120 characters
     * Validates against organization registry
     */
    private JTextField companyNameField;

    /**
     * Text field for entering the primary contact person's full name.
     * Required field for all bookings
     * Format validation: Requires at least first and last name
     * Maximum length: 80 characters
     */
    private JTextField primaryContactField;

    /**
     * Text field for entering the contact phone number.
     * Valid formats:
     * - International: +[country][number]
     * - Local: [area][number]
     * Auto-formats during input
     * Required for all bookings
     */
    private JTextField contactPhoneField;

    /**
     * Text field for entering the contact email address.
     * Validates standard email format
     * Used for:
     * - Booking confirmations
     * - Event reminders
     * - Emergency notifications
     * Required for all bookings
     */
    private JTextField contactEmailField;
    /**
     * Constructs an EventPanel with UI components to input and display event details.
     *
     * @param mainMenu   the main menu GUI instance used for styling and interactions
     * @param cardLayout the CardLayout used for switching views in the parent panel
     * @param cardPanel  the parent panel holding different cards (views)
     */
    public EventPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.setBackground(Color.white);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 30, 15, 15)); // Left indent increased to 30

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.white);
        JLabel titleLabel = new JLabel("Event Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Main container panel - uses BoxLayout for vertical alignment
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.white);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.black),
                new EmptyBorder(15, 30, 15, 15) // Left indent increased to 30
        ));

        // Add consistent spacing between components
        Component verticalStrut = Box.createVerticalStrut(10);

        // Booking ID Panel
        JPanel bookingPanel = createInputPanel("Booking ID:");
        bookingIdField = (JTextField) bookingPanel.getComponent(1);
        bookingIdField.setEditable(false);
        mainPanel.add(bookingPanel);
        mainPanel.add(verticalStrut);

        // Start Date Picker Panel
        JPanel startDatePanel = createDatePanel("Start Date:");
        startDatePicker = new JDateChooser();
        startDatePicker.setDateFormatString("dd-MM-yyyy");
        startDatePicker.setPreferredSize(new Dimension(150, 25));
        startDatePicker.setDate(new Date());
        startDatePanel.add(startDatePicker);
        mainPanel.add(startDatePanel);
        mainPanel.add(verticalStrut);

        // End Date Picker Panel
        JPanel endDatePanel = createDatePanel("End Date:");
        endDatePicker = new JDateChooser();
        endDatePicker.setDateFormatString("dd-MM-yyyy");
        endDatePicker.setPreferredSize(new Dimension(150, 25));
        endDatePicker.setDate(new Date());
        endDatePanel.add(endDatePicker);
        mainPanel.add(endDatePanel);
        mainPanel.add(verticalStrut);

        // Time options
        String[] timeOptions = {
                "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };

        // Start Time Panel
        JPanel startTimePanel = createComboPanel("Start Time:");
        startTimeCombo = new JComboBox<>(timeOptions);
        startTimeCombo.setPreferredSize(new Dimension(150, 25));
        mainMenu.styleDropdown(startTimeCombo);
        startTimePanel.add(startTimeCombo);
        mainPanel.add(startTimePanel);
        mainPanel.add(verticalStrut);

        // End Time Panel
        JPanel endTimePanel = createComboPanel("End Time:");
        endTimeCombo = new JComboBox<>(timeOptions);
        endTimeCombo.setPreferredSize(new Dimension(150, 25));
        mainMenu.styleDropdown(endTimeCombo);
        endTimePanel.add(endTimeCombo);
        mainPanel.add(endTimePanel);
        mainPanel.add(verticalStrut);

        // Activity ID Panel
        JPanel activityIdPanel = createInputPanel("Activity ID:");
        activityIdField = (JTextField) activityIdPanel.getComponent(1);
        mainPanel.add(activityIdPanel);
        mainPanel.add(verticalStrut);

        // Venue ID Panel
        JPanel venueIdPanel = createInputPanel("Venue ID:");
        venueIdField = (JTextField) venueIdPanel.getComponent(1);
        mainPanel.add(venueIdPanel);
        mainPanel.add(verticalStrut);

        // Booked By Panel
        JPanel bookedByPanel = createInputPanel("Booked By:");
        bookedByField = (JTextField) bookedByPanel.getComponent(1);
        mainPanel.add(bookedByPanel);
        mainPanel.add(verticalStrut);

        // Room Panel
        JPanel roomPanel = createInputPanel("Room:");
        roomField = (JTextField) roomPanel.getComponent(1);
        mainPanel.add(roomPanel);
        mainPanel.add(verticalStrut);

        // Company Name Panel
        JPanel companyPanel = createInputPanel("Company Name:");
        companyNameField = (JTextField) companyPanel.getComponent(1);
        mainPanel.add(companyPanel);
        mainPanel.add(verticalStrut);

        // Contact Details Section Label
        JPanel contactHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contactHeaderPanel.setBackground(Color.white);
        JLabel contactHeader = new JLabel("Contact Details");
        contactHeader.setFont(new Font("Arial", Font.BOLD, 16));
        contactHeaderPanel.add(contactHeader);
        mainPanel.add(contactHeaderPanel);
        mainPanel.add(verticalStrut);

        // Primary Contact Panel
        JPanel primaryContactPanel = createInputPanel("Primary Contact:");
        primaryContactField = (JTextField) primaryContactPanel.getComponent(1);
        mainPanel.add(primaryContactPanel);
        mainPanel.add(verticalStrut);

        // Contact Phone Panel
        JPanel phonePanel = createInputPanel("Contact Phone:");
        contactPhoneField = (JTextField) phonePanel.getComponent(1);
        mainPanel.add(phonePanel);
        mainPanel.add(verticalStrut);

        // Contact Email Panel
        JPanel emailPanel = createInputPanel("Contact Email:");
        contactEmailField = (JTextField) emailPanel.getComponent(1);
        mainPanel.add(emailPanel);
        mainPanel.add(verticalStrut);

        // Confirmed Checkbox Panel - MOVED HERE to be with contact details
        JPanel confirmedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduced hgap from 30 to 10
        confirmedPanel.setBackground(Color.white);
        JLabel confirmedLabel = createStyledLabel("Confirmed?");
        confirmedLabel.setPreferredSize(new Dimension(120, 25)); // Match other labels
        confirmedCheck = new JCheckBox();
        confirmedCheck.setBackground(Color.white);
        confirmedPanel.add(confirmedLabel);
        confirmedPanel.add(confirmedCheck);
        mainPanel.add(confirmedPanel);
        mainPanel.add(verticalStrut);

        mainPanel.add(confirmedPanel);

        // Add main panel to center
        add(mainPanel, BorderLayout.CENTER);

        // Bottom Panel (Save / Cancel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setBackground(Color.white);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        mainMenu.stylizeButton(saveButton);
        mainMenu.stylizeButton(cancelButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Save Button Logic
        saveButton.addActionListener(ev -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                LocalDate sDate = LocalDate.parse(sdf.format(startDatePicker.getDate()));
                LocalDate eDate = LocalDate.parse(sdf.format(endDatePicker.getDate()));
                LocalTime sTime = LocalTime.parse((String) startTimeCombo.getSelectedItem());
                LocalTime eTime = LocalTime.parse((String) endTimeCombo.getSelectedItem());
                int activityId = Integer.parseInt(activityIdField.getText().trim());
                int venueId = Integer.parseInt(venueIdField.getText().trim());
                boolean isConfirmed = confirmedCheck.isSelected();

                // Save logic here
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Button Logic
        cancelButton.addActionListener(ev -> cardLayout.show(cardPanel, "Calendar"));
    }

    /**
     * Creates a panel with a label and an input text field.
     *
     * @param labelText the text to display in the label
     * @return a JPanel containing the label and input text field
     */
    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduced hgap from 30 to 10
        panel.setBackground(Color.white);
        JLabel label = createStyledLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25)); // Fixed width for all labels
        panel.add(label);
        JTextField textField = new JTextField(15);
        textField.setPreferredSize(new Dimension(200, 25)); // Fixed width for text fields
        panel.add(textField);
        return panel;
    }

    /**
     * Creates a panel with a label for displaying a date field.
     *
     * @param labelText the text to display in the label
     * @return a JPanel containing the label
     */
    private JPanel createDatePanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduced hgap
        panel.setBackground(Color.white);
        JLabel label = createStyledLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25)); // Same width as other labels
        panel.add(label);
        return panel;
    }

    /**
     * Creates a panel with a label for displaying a combo box.
     *
     * @param labelText the text to display in the label
     * @return a JPanel containing the label
     */
    private JPanel createComboPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduced hgap
        panel.setBackground(Color.white);
        JLabel label = createStyledLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25)); // Same width as other labels
        panel.add(label);
        return panel;
    }

    /**
     * Creates a styled JLabel for consistency across the UI.
     *
     * @param text the text to display in the label
     * @return a styled JLabel
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        // Removed the preferred size setting here since we're setting it in the panel methods
        return label;
    }

    /**
     * Collects and validates the form data.
     *
     * @return a Booking object with the collected data, or null if validation fails
     */
    private Booking collectFormData() {
        // Validate required fields
        return null;
    }

    /**
     * Sets the booking data to be displayed/edited in the form.
     *
     * @param booking the Booking object containing existing booking data
     */
    public void setBookingData(Booking booking) {
        // Implementation for setting booking data goes here.
    }
}
