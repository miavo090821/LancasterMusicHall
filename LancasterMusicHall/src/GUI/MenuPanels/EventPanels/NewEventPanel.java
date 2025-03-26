package GUI.MenuPanels.EventPanels;

import Database.SQLConnection;
import GUI.MainMenuGUI;
import com.toedter.calendar.JDateChooser;
import operations.entities.Activity;
import operations.entities.Booking;
import operations.entities.Venue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class NewEventPanel extends JPanel {

    private JDateChooser startDatePicker;
    private JDateChooser endDatePicker;
    private JComboBox<String> startTimeCombo;
    private JComboBox<String> endTimeCombo;
    private JTextField bookingIdField;
    private JTextField activityIdField;
    private JTextField venueIdField;
    private JCheckBox heldCheck;

    public NewEventPanel(MainMenuGUI mainMenu, CardLayout cardLayout, JPanel cardPanel) {
        this.setBackground(Color.white);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(15, 30, 15, 15)); // Left indent increased to 30

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.white);
        JLabel titleLabel = new JLabel("New Event");
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
        mainPanel.add(bookingPanel);
        mainPanel.add(verticalStrut);

        // Start Date Picker Panel
        JPanel startDatePanel = createDatePanel("Start Date:");
        startDatePicker = new JDateChooser();
        startDatePicker.setDateFormatString("yyyy-MM-dd");
        startDatePicker.setPreferredSize(new Dimension(150, 25));
        startDatePicker.setDate(new Date());
        startDatePanel.add(startDatePicker);
        mainPanel.add(startDatePanel);
        mainPanel.add(verticalStrut);

        // End Date Picker Panel
        JPanel endDatePanel = createDatePanel("End Date:");
        endDatePicker = new JDateChooser();
        endDatePicker.setDateFormatString("yyyy-MM-dd");
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
        activityIdField.setText("1");
        mainPanel.add(activityIdPanel);
        mainPanel.add(verticalStrut);

        // Venue ID Panel
        JPanel venueIdPanel = createInputPanel("Venue ID:");
        venueIdField = (JTextField) venueIdPanel.getComponent(1);
        venueIdField.setText("1");
        mainPanel.add(venueIdPanel);
        mainPanel.add(verticalStrut);

        // Held Checkbox Panel
        JPanel heldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        heldPanel.setBackground(Color.white);
        heldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel heldLabel = createStyledLabel("Confirmed?");
        heldCheck = new JCheckBox();
        heldCheck.setBackground(Color.white);
        heldPanel.add(heldLabel);
        heldPanel.add(heldCheck);
        mainPanel.add(heldPanel);

        // Add main panel to center
        add(mainPanel, BorderLayout.CENTER);

        // Bottom Panel (Save / Cancel)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
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
                boolean isHeld = heldCheck.isSelected();

                Booking newBooking = new Booking(
                        bookingId, sDate, eDate, sTime, eTime,
                        new Activity(activityId, "Activity " + activityId),
                        new Venue(venueId, "Venue " + venueId, "Hall", 300),
                        isHeld, "", new ArrayList<>()
                );

                SQLConnection sqlConnection = mainMenu.getSqlConnection();
                boolean success = sqlConnection.createBooking(newBooking);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Event created successfully!");
                    cardLayout.show(cardPanel, "Calendar");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create event.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(ev -> cardLayout.show(cardPanel, "Calendar"));
    }

    // Helper methods for consistent panel creation
    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.white);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(createStyledLabel(labelText));
        JTextField textField = new JTextField(15);
        panel.add(textField);
        return panel;
    }

    private JPanel createDatePanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.white);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(createStyledLabel(labelText));
        return panel;
    }

    private JPanel createComboPanel(String labelText) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(Color.white);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(createStyledLabel(labelText));
        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setPreferredSize(new Dimension(100, 25)); // Fixed width for alignment
        return label;
    }
}