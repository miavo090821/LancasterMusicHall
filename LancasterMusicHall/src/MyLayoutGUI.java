import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MyLayoutGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Layout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        // 🔵 Top Panel (Title)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.white);
        topPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        topPanel.setPreferredSize(new Dimension(700, 80));

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        topPanel.add(titleLabel);

        // 🔵 Center Panel (Login Section)
        JPanel centerPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 50, 15)));
        centerPanel.setBackground(Color.white);
        centerPanel.setPreferredSize(new Dimension(700, 30));

        JLabel contentLabel = new JLabel("Staff Login:");
        contentLabel.setFont(new Font("Arial", Font.BOLD, 20));
        centerPanel.add(contentLabel);

        // 🔵 Login Panel Staff ID
        JPanel staffIDPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 100, 15)));
        staffIDPanel.setPreferredSize(new Dimension(700, 30));
        staffIDPanel.setBackground(Color.white);

        JLabel staffIdLabel = new JLabel("Staff ID:");
        staffIdLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JTextField staffIdField = new JTextField(15);

        staffIDPanel.add(staffIdLabel);
        staffIDPanel.add(staffIdField);

        // 🔵 Login Panel password
        JPanel passwordPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 100, 15)));
        passwordPanel.setPreferredSize(new Dimension(700, 30));
        passwordPanel.setBackground(Color.white);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JPasswordField passwordField = new JPasswordField(15);

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        // 🔵 Login Panel button
        JPanel buttonPanel = new JPanel((new FlowLayout(FlowLayout.CENTER, 0, 15)));
        buttonPanel.setPreferredSize(new Dimension(700, 30));
        buttonPanel.setBackground(Color.white);

        JButton enterButton = new JButton("Enter");

        // Set Font and Size
        enterButton.setFont(new Font("Arial", Font.BOLD, 16));

        // Change Background & Text Color
        enterButton.setBackground(Color.WHITE); // Blue

        // Remove Borders for a Modern Look
        enterButton.setBorderPainted(false);
        enterButton.setFocusPainted(false);
        enterButton.setContentAreaFilled(true);

        buttonPanel.add(enterButton);

        // Cursor Change on Hover
        enterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add Hover Effect
        enterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(new Color(50, 150, 250)); // Darker Blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                enterButton.setBackground(Color.WHITE); // Back to Original
            }
        });

        // 🔵 Wrapper Panel for Center and Staff (Stacked Layout)
        JPanel mainCenterPanel = new JPanel();
        mainCenterPanel.setLayout(new BoxLayout(mainCenterPanel, BoxLayout.Y_AXIS));
        mainCenterPanel.add(centerPanel);
        mainCenterPanel.add(staffIDPanel);
        mainCenterPanel.add(passwordPanel);
        mainCenterPanel.add(buttonPanel);

        // 🔵 Bottom Panel forgotten password
        JPanel bottomPanel = new JPanel((new FlowLayout(FlowLayout.LEFT, 50, 0)));
        bottomPanel.setBackground(Color.white);
        bottomPanel.setPreferredSize(new Dimension(700, 75));

        JButton forgotPasswordButton = new JButton("Forgotten Password?");
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(Color.BLUE);
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomPanel.add(forgotPasswordButton);

        // ✅ Adding Panels to Frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainCenterPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
