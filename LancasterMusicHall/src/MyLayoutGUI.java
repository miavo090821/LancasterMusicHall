import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MyLayoutGUI {
    /**
     * Initialise the frame, and the three main parts:
     *      1. Top Panel
     *      2. Main Panel
     *      3. Bottom Panel
     * */
    public static void main(String[] args) {
        // Create the main application frame
        JFrame frame = new JFrame("Layout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout()); // Using BorderLayout to organize sections

        // Add all panels to the frame
        frame.add(getTopPanel(), BorderLayout.NORTH);
        frame.add(getMainCenterPanel(), BorderLayout.CENTER);
        frame.add(getBottomPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    /**
     * change color to look at the sections
     */

    // [1] Top Panel - Application Title
    /**
     * function to write the title, Lancaster music hall
     * */
    private static JPanel getTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(20, 0, 0, 0)); // Adds padding at the top
        topPanel.setPreferredSize(new Dimension(700, 80));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
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
    private static JPanel getMainCenterPanel() {
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS)); // Stack components vertically
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
    private static JPanel getLoginPanel() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 15));
        centerPanel.setBackground(Color.white);
        centerPanel.setPreferredSize(new Dimension(700, 30));

        JLabel contentLabel = new JLabel("Staff Login:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(contentLabel);

        return centerPanel;
    }

    /**
     * function to write staff ID
     * */
    // [4] Staff ID Panel - Label and Input Field
    private static JPanel getStaffIDPanel() {
        JPanel staffIDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduce horizontal gap
        staffIDPanel.add(Box.createHorizontalStrut(90));
        staffIDPanel.setBackground(Color.white);

        JLabel staffIdLabel = new JLabel("Staff ID:");
        staffIdLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JTextField staffIdField = new JTextField(15);

        staffIDPanel.add(staffIdLabel);
        staffIDPanel.add(staffIdField);
        return staffIDPanel;
    }

    /**
     * function to write staff password
     * */
    // [5] Password Panel - Label and Input Field
    private static JPanel getPasswordPanel() {
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Reduce horizontal gap
        passwordPanel.add(Box.createHorizontalStrut(90));

        passwordPanel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JPasswordField passwordField = new JPasswordField(15);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        return passwordPanel;
    }

    /**
     * function to write enter button
     * */
    // [6] Enter Button Panel
    private static JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5)); // Reduced vertical gap
        buttonPanel.setPreferredSize(new Dimension(700, 30));
        buttonPanel.setBackground(Color.white);

        JButton enterButton = new JButton("Enter");
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));
        enterButton.setBackground(Color.WHITE);
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setContentAreaFilled(true);
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add Hover Effect for Button
        enterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(new Color(50, 150, 250)); // Darker Blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(Color.WHITE); // Reverts to original
            }
        });

        buttonPanel.add(enterButton);
        return buttonPanel;
    }


    /**
     * Bottom panel has one part:
     *        1. forgotten password button
     * */
    private static JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 0)); // Reduced vertical padding
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 40)); // Reduced height

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.blue);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(forgotPasswordButton);
        return bottomPanel;
    }
}
