import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MyLayoutGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Layout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

        frame.add(getTopPanel(), BorderLayout.NORTH);
        frame.add(getMainCenterPanel(), BorderLayout.CENTER);
        frame.add(getBottomPanel(), BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JPanel getTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        topPanel.setMaximumSize(new Dimension(500, 150));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 27));
        topPanel.add(titleLabel);

        return topPanel;
    }

    private static JPanel getMainCenterPanel() {
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));
        mainCenterPanel.setMaximumSize(new Dimension(500, 240));

        mainCenterPanel.add(getLoginPanel());
        mainCenterPanel.add(getStaffIDPanel());
        mainCenterPanel.add(getPasswordPanel());
        mainCenterPanel.add(getButtonPanel());

        return mainCenterPanel;
    }

    private static JPanel getLoginPanel() {
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 15));
        centerPanel.setBackground(Color.white);
        centerPanel.setMaximumSize(new Dimension(500, 60));

        JLabel contentLabel = new JLabel("Staff Login:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(contentLabel);

        return centerPanel;
    }

    private static JPanel getStaffIDPanel() {
        JPanel staffIDPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        staffIDPanel.add(Box.createHorizontalStrut(90));
        staffIDPanel.setMaximumSize(new Dimension(500, 60));
        staffIDPanel.setBackground(Color.white);

        JLabel staffIdLabel = new JLabel("Staff ID:");
        staffIdLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JTextField staffIdField = new JTextField(15);

        staffIDPanel.add(staffIdLabel);
        staffIDPanel.add(staffIdField);
        return staffIDPanel;
    }

    private static JPanel getPasswordPanel() {
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        passwordPanel.add(Box.createHorizontalStrut(90));
        passwordPanel.setMaximumSize(new Dimension(500, 60));
        passwordPanel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPasswordField passwordField = new JPasswordField(15);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        return passwordPanel;
    }

    private static JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        buttonPanel.setMaximumSize(new Dimension(500, 60));
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

        buttonPanel.add(enterButton);
        return buttonPanel;
    }

    private static JPanel getBottomPanel() {
        JPanel bottomPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 50, 20)));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 75));

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(forgotPasswordButton);
        return bottomPanel;
    }

}
