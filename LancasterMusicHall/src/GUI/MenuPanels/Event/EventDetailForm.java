package GUI.MenuPanels.Event;

import Database.SQLConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

public class EventDetailForm extends JDialog {
    private SQLConnection sqlCon;
    private String eventId;

    // Event UI Components
    private JTextField startDateField, endDateField, startTimeField, endTimeField;
    private JTextField eventNameField, venueNameField, eventTypeField, descriptionField, bookedByField, layoutField;

    // You can add more fields as needed

    public EventDetailForm(Frame owner, SQLConnection sqlCon, String eventId) {
        super(owner, "Event Details - " + eventId, true);
        this.sqlCon = sqlCon;
        this.eventId = eventId;
        initComponents();
        loadEventDetails();
        setSize(600, 500);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Event Name
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Event Name:"), gbc);
        gbc.gridx = 1;
        eventNameField = new JTextField(20);
        eventNameField.setEditable(false);
        mainPanel.add(eventNameField, gbc);

        // Venue Name
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Venue Name:"), gbc);
        gbc.gridx = 1;
        venueNameField = new JTextField(20);
        venueNameField.setEditable(false);
        mainPanel.add(venueNameField, gbc);

        // Start Date
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        startDateField = new JTextField(20);
        startDateField.setEditable(false);
        mainPanel.add(startDateField, gbc);

        // End Date
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 1;
        endDateField = new JTextField(20);
        endDateField.setEditable(false);
        mainPanel.add(endDateField, gbc);

        // Start Time
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(20);
        startTimeField.setEditable(false);
        mainPanel.add(startTimeField, gbc);

        // End Time
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("End Time:"), gbc);
        gbc.gridx = 1;
        endTimeField = new JTextField(20);
        endTimeField.setEditable(false);
        mainPanel.add(endTimeField, gbc);

        // Event Type
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Event Type:"), gbc);
        gbc.gridx = 1;
        eventTypeField = new JTextField(20);
        eventTypeField.setEditable(false);
        mainPanel.add(eventTypeField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(20);
        descriptionField.setEditable(false);
        mainPanel.add(descriptionField, gbc);

        // Booked By
        gbc.gridx = 0; gbc.gridy++;
        mainPanel.add(new JLabel("Booked By:"), gbc);
        gbc.gridx = 1;
        bookedByField = new JTextField(20);
        bookedByField.setEditable(false);
        mainPanel.add(bookedByField, gbc);

        // New input fields for Description and Layout.
        descriptionField = new JTextField();
        layoutField = new JTextField();


        // Add a button to close the form
        gbc.gridx = 1; gbc.gridy++;
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        mainPanel.add(closeButton, gbc);

        setContentPane(new JScrollPane(mainPanel));
    }

    private void loadEventDetails() {
        try {
            // Assume you have a method in SQLConnection that gets event details by event_id.
            // The query should join with the Venue table to get the venue name.
            ResultSet rsEvent = sqlCon.getEventDetailsByEventId(Integer.parseInt(eventId));
            if (rsEvent.next()) {
                eventNameField.setText(rsEvent.getString("name"));
                startDateField.setText(rsEvent.getString("start_date"));
                endDateField.setText(rsEvent.getString("end_date"));
                startTimeField.setText(rsEvent.getString("start_time"));
                endTimeField.setText(rsEvent.getString("end_time"));
                eventTypeField.setText(rsEvent.getString("event_type"));
                venueNameField.setText(rsEvent.getString("venue_name"));  // Venue name from join
                descriptionField.setText(rsEvent.getString("description"));
                bookedByField.setText(rsEvent.getString("booked_by"));
            }
            rsEvent.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading event details: " + ex.getMessage());
        }
    }
}
