import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {
    private final JTextField staffIdField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginUI() {
        setTitle("Lancaster Music Hall");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        JLabel titleLabel = new JLabel("Lancaster Music Hall");
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));

        JLabel loginLabel = new JLabel("Staff Log In:");
        JLabel staffIdLabel = new JLabel("Staff ID:");
        JLabel passwordLabel = new JLabel("Password:");

        JButton enterButton = new JButton("Enter");
        JLabel forgotPasswordLabel = new JLabel("Forgotten your password?");

        enterButton.addActionListener(this::handleLogin);

        // Horizontal layout
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(titleLabel)
                        .addComponent(loginLabel, GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(staffIdLabel)
                                        .addComponent(passwordLabel))
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addComponent(staffIdField, 200, 200, 200)
                                        .addComponent(passwordField, 200, 200, 200)))
                        .addComponent(enterButton)
                        .addComponent(forgotPasswordLabel, GroupLayout.Alignment.LEADING)
        );

        // Vertical layout
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addGap(20)
                        .addComponent(loginLabel)
                        .addGap(10)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(staffIdLabel)
                                .addComponent(staffIdField))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(passwordLabel)
                                .addComponent(passwordField))
                        .addGap(20)
                        .addComponent(enterButton)
                        .addGap(20)
                        .addComponent(forgotPasswordLabel)
        );

        getContentPane().add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleLogin(ActionEvent event) {
        String staffId = staffIdField.getText();
        String password = new String(passwordField.getPassword());

        System.out.println("Staff ID: " + staffId);
        System.out.println("Password: " + password);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}