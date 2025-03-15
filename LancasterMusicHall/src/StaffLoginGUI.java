import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;

public class StaffLoginGUI {
    // the staff id and password fields
    private JTextField staffIdField;
    private JPasswordField passwordField;

    /**
     * Initialise the frame, and the three main parts:
     *      1. Top Panel
     *      2. Main Panel
     *      3. Bottom Panel
     * */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffLoginGUI::new);
    }

    public StaffLoginGUI() {
        staffIdField = new JTextField(15);
        passwordField = new JPasswordField(15);

        JFrame frame = new JFrame("Staff Login"); // name of the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // frame deletes after closing window
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS)); // components will be arranged vertically
        // components = (buttons, panels, labels)

        // you have to add the panels to the frame
        frame.add(getTopPanel());
        frame.add(getMainCenterPanel());
        frame.add(getBottomPanel());

        frame.setVisible(true);
    }

    /**
     * change color to look at the sections
     *
     **/

    // [1] Top Panel - Application Title
    /**
     * function to write the title, Lancaster music hall
     * */
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
    /**
     * function to write the main section, split into four parts:
     *         1. Login Panel
     *         2. Staff ID Panel
     *         3. Password Panel
     *         4. ButtonPanel
     * */
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
     * function to write staff login:
     * */
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

    /**
     * function to write staff ID
     * */
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

    /**
     * function to write staff password
     * */
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

    /**
     * function to write enter button
     * */
    // [6] Enter Button Panel
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setPreferredSize(new Dimension(500, 30));
        buttonPanel.setBackground(Color.white);

        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));
        enterButton.setBackground(Color.WHITE);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setContentAreaFilled(true);
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        enterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(new Color(50, 150, 250));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(Color.WHITE);
            }
        });

        enterButton.addActionListener(e -> {
            if (getStaffID().isEmpty() || getPassword().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter Staff ID and Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Login successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(enterButton);
        return buttonPanel;
    }

    /**
     * Bottom panel has one part:
     *        1. forgotten password button
     * */
    private JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 50, 0)));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 40));

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(forgotPasswordButton);
        return bottomPanel;
    }

    // New method to get the Staff ID from the field
    private String getStaffID() {
        return staffIdField.getText();
    }

    // New method to get the Password from the field
    private String getPassword() {
        return new String(passwordField.getPassword());
    }
}
