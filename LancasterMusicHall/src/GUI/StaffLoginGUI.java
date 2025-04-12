package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import Database.SQLConnection;

/**
 * The StaffLoginGUI class provides a graphical user interface for staff members to log in
 * to the Lancaster Music Hall management system. It handles authentication and provides
 * password reset functionality.
 *
 * <p>This class integrates with the SQLConnection class to validate credentials and
 * perform password resets against the database.</p>
 *
 * @author Mia
 * @version 1.0
 * @see SQLConnection
 */
public class StaffLoginGUI {
    /** Text field for staff ID input */
    private JTextField staffIdField;

    /** Password field for password input */
    private JPasswordField passwordField;

    /** Main application frame */
    private JFrame frame;

    /** Database connection handler */
    private SQLConnection sqlConnection;

    /**
     * Main method to launch the Staff Login GUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffLoginGUI::new);
    }

    /**
     * Constructs a new StaffLoginGUI and initializes the UI components.
     * Creates a new SQLConnection instance for database operations.
     */
    public StaffLoginGUI() {
        sqlConnection = new SQLConnection();

        staffIdField = new JTextField(15);
        passwordField = new JPasswordField(15);

        frame = new JFrame("Staff Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        frame.add(getTopPanel());
        frame.add(getMainCenterPanel());
        frame.add(getBottomPanel());

        frame.setVisible(true);
    }

    /**
     * Creates the top panel containing the application title.
     *
     * @return JPanel with the title label
     */
    private JPanel getTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(500, 40));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    /**
     * Creates the main center panel that contains all login components.
     *
     * @return JPanel containing login form elements
     */
    private JPanel getMainCenterPanel() {
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));

        mainCenterPanel.add(getLoginPanel());
        mainCenterPanel.add(getStaffIDPanel());
        mainCenterPanel.add(getPasswordPanel());
        mainCenterPanel.add(getButtonPanel());

        return mainCenterPanel;
    }

    /**
     * Creates the login header panel.
     *
     * @return JPanel with the "Staff Login" label
     */
    private JPanel getLoginPanel() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 15));
        centerPanel.setBackground(Color.white);
        centerPanel.setPreferredSize(new Dimension(500, 30));

        JLabel contentLabel = new JLabel("Staff Login:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(contentLabel);

        return centerPanel;
    }

    /**
     * Creates the staff ID input panel.
     *
     * @return JPanel with staff ID label and text field
     */
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

    /**
     * Creates the password input panel.
     *
     * @return JPanel with password label and password field
     */
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

    /**
     * Creates the login button panel with hover effects.
     *
     * @return JPanel containing the login button
     */
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

        enterButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                enterButton.setBackground(new Color(50, 150, 250));
            }
            public void mouseExited(MouseEvent evt) {
                enterButton.setBackground(Color.WHITE);
            }
        });

        enterButton.addActionListener(_ -> {
            String staffId = getStaffID();
            String password = getPassword();
            if (staffId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Staff ID and Password", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean isValid = sqlConnection.loginStaff(staffId, password);
                if (isValid) {
                    Integer currentStaffId = sqlConnection.getCurrentStaffId();
                    if (currentStaffId != null) {
                        System.out.println("Logged in Staff ID: " + currentStaffId);
                        new MainMenuGUI(currentStaffId, sqlConnection);
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error retrieving Staff ID from session.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Staff ID or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(enterButton);
        return buttonPanel;
    }

    /**
     * Creates the bottom panel with password reset option.
     *
     * @return JPanel containing the "Forgotten Password" button
     */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 40));

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        forgotPasswordButton.addActionListener(_ -> showResetPasswordDialog());

        bottomPanel.add(forgotPasswordButton);
        return bottomPanel;
    }

    /**
     * Gets the staff ID from the input field.
     *
     * @return Trimmed staff ID string
     */
    private String getStaffID() {
        return staffIdField.getText().trim();
    }

    /**
     * Gets the password from the password field.
     *
     * @return Password as string
     */
    private String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Displays a modal dialog for password reset functionality.
     * Validates inputs and uses SQLConnection to update the password.
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
        resetButton.addActionListener(_ -> {
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