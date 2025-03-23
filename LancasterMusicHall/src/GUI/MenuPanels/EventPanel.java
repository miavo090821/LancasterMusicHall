package GUI.MenuPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
import com.toedter.calendar.JCalendar;
import com.toedter.calendar.JDateChooser;
import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Venue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class EventPanel extends JPanel {

    private JDateChooser startDatePicker;
    private JDateChooser endDatePicker;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;

    public EventPanel(MainMenuGUI mainMenu) {
        this.setBackground(Color.white);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(5, 5, 5, 5));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setPreferredSize(new Dimension(600, 40));
        titlePanel.setBackground(Color.white);
        JLabel titleLabel = new JLabel("Event ID:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titlePanel.add(titleLabel);
        this.add(titlePanel);

        // Main container panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(600, 350));
        mainPanel.setBackground(Color.white);
        mainPanel.setBorder(new LineBorder(Color.black));
        add(mainPanel);

        // Booking ID Panel
        JPanel bookingPanel = createInputPanel("Booking ID:", 15);
        JTextField bookingIdField = (JTextField) bookingPanel.getComponent(1);
        mainPanel.add(bookingPanel);

        // Start Date Picker Panel
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startDatePanel.setPreferredSize(new Dimension(600, 30));
        startDatePanel.setBackground(Color.white);
        JLabel startDateLabel = createStyledLabel("Start Date:");
        startDatePicker = new JDateChooser();
        startDatePicker.setDateFormatString("yyyy-MM-dd");
        startDatePicker.setPreferredSize(new Dimension(150, 25));
        startDatePicker.setDate(new Date()); // default to today
        startDatePanel.add(startDateLabel);
        startDatePanel.add(startDatePicker);
        mainPanel.add(startDatePanel);

        // End Date Picker Panel
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endDatePanel.setPreferredSize(new Dimension(600, 30));
        endDatePanel.setBackground(Color.white);
        JLabel endDateLabel = createStyledLabel("End Date:");
        endDatePicker = new JDateChooser();
        endDatePicker.setDateFormatString("yyyy-MM-dd");
        endDatePicker.setPreferredSize(new Dimension(150, 25));
        endDatePicker.setDate(new Date()); // default to today
        endDatePanel.add(endDateLabel);
        endDatePanel.add(endDatePicker);
        mainPanel.add(endDatePanel);

        // Time options
        String[] timeOptions = {
                "08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
        };

        // Start Time Panel
        JPanel startTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startTimePanel.setPreferredSize(new Dimension(600, 30));
        startTimePanel.setBackground(Color.white);
        JLabel startTimeLabel = createStyledLabel("Start Time:");
        startTimeCombo = new JComboBox<>(timeOptions);
        startTimeCombo.setPreferredSize(new Dimension(150, 25));
        startTimePanel.add(startTimeLabel);
        startTimePanel.add(startTimeCombo);
        mainPanel.add(startTimePanel);

        // End Time Panel
        JPanel endTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endTimePanel.setPreferredSize(new Dimension(600, 30));
        endTimePanel.setBackground(Color.white);
        JLabel endTimeLabel = createStyledLabel("End Time:");
        endTimeCombo = new JComboBox<>(timeOptions);
        endTimeCombo.setPreferredSize(new Dimension(150, 25));
        endTimePanel.add(endTimeLabel);
        endTimePanel.add(endTimeCombo);
        mainPanel.add(endTimePanel);

        // Activity ID Panel
        JPanel activityIdPanel = createInputPanel("Activity ID:", 15);
        JTextField activityIdField = (JTextField) activityIdPanel.getComponent(1);
        activityIdField.setText("1");
        mainPanel.add(activityIdPanel);

        // Venue ID Panel
        JPanel venueIdPanel = createInputPanel("Venue ID:", 15);
        JTextField venueIdField = (JTextField) venueIdPanel.getComponent(1);
        venueIdField.setText("1");
        mainPanel.add(venueIdPanel);

        // Held Checkbox Panel
        JPanel heldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        heldPanel.setPreferredSize(new Dimension(600, 30));
        heldPanel.setBackground(Color.white);
        JLabel heldLabel = createStyledLabel("Held?");
        JCheckBox heldCheck = new JCheckBox();
        heldCheck.setBackground(Color.white);
        heldPanel.add(heldLabel);
        heldPanel.add(heldCheck);
        mainPanel.add(heldPanel);

        // Hold Expiry Panel
        JPanel holdExpiryPanel = createInputPanel("Hold Expiry (yyyy-MM-dd):", 15);
        JTextField holdExpiryField = (JTextField) holdExpiryPanel.getComponent(1);
        mainPanel.add(holdExpiryPanel);

        // Bottom Panel (Save / Cancel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setPreferredSize(new Dimension(600, 40));
        bottomPanel.setBackground(Color.white);
        add(bottomPanel);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        mainMenu.stylizeButton(saveButton);
        mainMenu.stylizeButton(cancelButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);

        // SQL Connection (assumed to be passed or fetched)
        SQLConnection sqlConnection = mainMenu.getSqlConnection();

        // Save Button Logic
        saveButton.addActionListener(ev -> {
            try {
                int bookingId = Integer.parseInt(bookingIdField.getText().trim());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date sDateRaw = startDatePicker.getDate();
                Date eDateRaw = endDatePicker.getDate();

                LocalDate sDate = LocalDate.parse(sdf.format(sDateRaw));
                LocalDate eDate = LocalDate.parse(sdf.format(eDateRaw));

                LocalTime sTime = LocalTime.parse((String) startTimeCombo.getSelectedItem());
                LocalTime eTime = LocalTime.parse((String) endTimeCombo.getSelectedItem());

                int activityId = Integer.parseInt(activityIdField.getText().trim());
                int venueId = Integer.parseInt(venueIdField.getText().trim());
                boolean isHeld = heldCheck.isSelected();
                String holdExpiryStr = holdExpiryField.getText().trim();
                if (holdExpiryStr.isEmpty()) holdExpiryStr = null;

                // Minimal placeholders
                Activity activity = new Activity(activityId, "Activity " + activityId);
                Venue venue = new Venue(venueId, "Venue " + venueId, "Hall", 300);

                Booking newBooking = new Booking(
                        bookingId, sDate, eDate, sTime, eTime,
                        activity, venue, isHeld, holdExpiryStr, new ArrayList<>()
                );

                boolean success = sqlConnection.createBooking(newBooking);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event created successfully!");
                    // Optionally refresh or clear fields
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Cancel Button Logic
        cancelButton.addActionListener(ev -> {
            // Clear or close panel/dialog
            JOptionPane.showMessageDialog(this, "Action cancelled.");
        });
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setBackground(Color.white);
        return label;
    }

    private JPanel createInputPanel(String labelText, int fieldLength) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setPreferredSize(new Dimension(600, 30));
        panel.setBackground(Color.white);
        JLabel label = createStyledLabel(labelText);
        JTextField textField = new JTextField(fieldLength);
        panel.add(label);
        panel.add(textField);
        return panel;
    }

    public static void applyMinimalFlatUI() {
        Color white = Color.WHITE;
        Color lightGray = new Color(220, 220, 220);
        Color darkGray = new Color(50, 50, 50);

        // Global background and text colors
        UIManager.put("Panel.background", white);
        UIManager.put("Button.background", white);
        UIManager.put("ComboBox.background", white);
        UIManager.put("TextField.background", white);
        UIManager.put("FormattedTextField.background", white);
        UIManager.put("Spinner.background", white);

        UIManager.put("Button.foreground", darkGray);
        UIManager.put("ComboBox.foreground", darkGray);
        UIManager.put("TextField.foreground", darkGray);
        UIManager.put("FormattedTextField.foreground", darkGray);
        UIManager.put("Spinner.foreground", darkGray);

        // Flat borders
        Border flatBorder = BorderFactory.createLineBorder(lightGray, 1);
        UIManager.put("TextField.border", flatBorder);
        UIManager.put("ComboBox.border", flatBorder);
        UIManager.put("Spinner.border", flatBorder);
        UIManager.put("Button.border", flatBorder);

        // Remove focus painting (dotted outlines)
        UIManager.put("Button.focusPainted", false);
        UIManager.put("ComboBox.focusPainted", false);
    }


}
