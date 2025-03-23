package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Database.SQLConnection;

public class StaffLoginGUI {
    // Fields for staff ID and password input
    private JTextField staffIdField;
    private JPasswordField passwordField;
    private JFrame frame;

    // SQLConnection instance to interact with the database.
    private SQLConnection sqlConnection;

    /**
     * Main method to start the Staff Login GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffLoginGUI::new);
    }

    /**
     * Constructor to initialize the Staff Login GUI and SQL connection.
     */
    public StaffLoginGUI() {
        // Initialize SQL connection instance.
        sqlConnection = new SQLConnection();

        staffIdField = new JTextField(15);
        passwordField = new JPasswordField(15);

        frame = new JFrame("Staff Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        // Add panels to the frame.
        frame.add(getTopPanel());
        frame.add(getMainCenterPanel());
        frame.add(getBottomPanel());

        frame.setVisible(true);
    }

    // [1] Top Panel - Application Title
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(500, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    // [2] Main Center Panel - Contains login components
    private JPanel getMainCenterPanel() {
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));

        mainCenterPanel.add(getLoginPanel());
        mainCenterPanel.add(getStaffIDPanel());
        mainCenterPanel.add(getPasswordPanel());
        mainCenterPanel.add(getButtonPanel());

        return mainCenterPanel;
    }

    // [3] Center Panel - Login Section Header
    private JPanel getLoginPanel() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 15));
        centerPanel.setBackground(Color.white);
        centerPanel.setPreferredSize(new Dimension(500, 30));

        JLabel contentLabel = new JLabel("Staff Login:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(contentLabel);

        return centerPanel;
    }

    // [4] Staff ID Panel - Label and Input Field
    private JPanel getStaffIDPanel() {
        JPanel staffIDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        staffIDPanel.add(Box.createHorizontalStrut(90));
        staffIDPanel.setPreferredSize(new Dimension(500, 30));
        staffIDPanel.setBackground(Color.white);

        JLabel staffIdLabel = new JLabel("Staff ID:");
        staffIdLabel.setFont(new Font("Arial", Font.BOLD, 20));

        staffIDPanel.add(staffIdLabel);
        staffIDPanel.add(staffIdField);
        return staffIDPanel;
    }

    // [5] Password Panel - Label and Input Field
    private JPanel getPasswordPanel() {
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        passwordPanel.add(Box.createHorizontalStrut(90));
        passwordPanel.setPreferredSize(new Dimension(500, 30));
        passwordPanel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 20));

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        return passwordPanel;
    }

    // [6] Enter Button Panel - Handles Login Action
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setPreferredSize(new Dimension(500, 30));
        buttonPanel.setBackground(Color.white);

        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));
        enterButton.setBackground(Color.WHITE);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects for Enter button
        enterButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                enterButton.setBackground(new Color(50, 150, 250));
            }
            public void mouseExited(MouseEvent evt) {
                enterButton.setBackground(Color.WHITE);
            }
        });

        // Action listener for login validation using SQL query.
        enterButton.addActionListener(e -> {
            String staffId = getStaffID();
            String password = getPassword();
            if (staffId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Staff ID and Password", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Validate credentials using SQLConnection's loginStaff method.
                boolean isValid = sqlConnection.loginStaff(staffId, password);
                if (isValid) {
                    JOptionPane.showMessageDialog(null, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // After successful login, open Main Menu GUI.
                    new MainMenuGUI();
                    frame.dispose(); // Close login window.
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Staff ID or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(enterButton);
        return buttonPanel;
    }

    // [7] Bottom Panel - Contains Forgotten Password Option
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 40));

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Action listener to open the Password Reset dialog.
        forgotPasswordButton.addActionListener(e -> showResetPasswordDialog());

        bottomPanel.add(forgotPasswordButton);
        return bottomPanel;
    }

    // Utility method to get Staff ID from input field.
    private String getStaffID() {
        return staffIdField.getText().trim();
    }

    // Utility method to get Password from input field.
    private String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Displays a dialog for resetting the password.
     */
    private void showResetPasswordDialog() {
        JDialog resetDialog = new JDialog(frame, "Reset Password", true);
        resetDialog.setSize(400, 300);
        resetDialog.setLayout(new BoxLayout(resetDialog.getContentPane(), BoxLayout.Y_AXIS));

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel idLabel = new JLabel("Staff ID:");
        JTextField idField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel newPassLabel = new JLabel("New Password:");
        JPasswordField newPassField = new JPasswordField();

        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPassField = new JPasswordField();

        infoPanel.add(idLabel);
        infoPanel.add(idField);
        infoPanel.add(emailLabel);
        infoPanel.add(emailField);
        infoPanel.add(newPassLabel);
        infoPanel.add(newPassField);
        infoPanel.add(confirmPassLabel);
        infoPanel.add(confirmPassField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton resetButton = new JButton("Reset Password");
        resetButton.addActionListener(e -> {
            String staffId = idField.getText().trim();
            String email = emailField.getText().trim();
            String newPassword = new String(newPassField.getPassword());
            String confirmPassword = new String(confirmPassField.getPassword());

            if (staffId.isEmpty() || email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(resetDialog, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(resetDialog, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call SQLConnection to update the password.
            boolean success = sqlConnection.resetPassword(staffId, email, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(resetDialog, "Password reset successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                resetDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(resetDialog, "Password reset failed. Please check your Staff ID and Email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(resetButton);

        resetDialog.add(infoPanel);
        resetDialog.add(buttonPanel);
        resetDialog.setLocationRelativeTo(frame);
        resetDialog.setVisible(true);
    }
}
