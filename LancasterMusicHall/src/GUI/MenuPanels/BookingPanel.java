package GUI.MenuPanels;

import GUI.MainMenuGUI;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BookingPanel extends JPanel {
    public BookingPanel(MainMenuGUI mainMenu) {
        // Main panel setup - parent container
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 20, 10, 20));

        // === Child 1: Title Panel (NORTH) ===
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // === Child 2: Main Content Panel (CENTER) ===
        JPanel contentPanel = createContentPanel(mainMenu);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Bookings:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 25));
        titlePanel.add(titleLabel);

        return titlePanel;
    }

    private JPanel createContentPanel(MainMenuGUI mainMenu) {
        JPanel contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(550, 300));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // === Search Section ===
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JTextField searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 30));

        JButton filterButton = new JButton("Filter");
        mainMenu.stylizeButton(filterButton);

        JButton newBookingButton = new JButton("New Booking");
        mainMenu.stylizeButton(newBookingButton);

        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(filterButton);
        searchPanel.add(Box.createHorizontalStrut(80));
        searchPanel.add(newBookingButton);

        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(10)); // Spacer

        // === Bookings List ===
        JPanel bookingsPanel = new JPanel();
        bookingsPanel.setLayout(new BoxLayout(bookingsPanel, BoxLayout.Y_AXIS));
        bookingsPanel.setBackground(Color.WHITE);
        bookingsPanel.setBorder(new LineBorder(Color.black));
        bookingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel idLabel = new JLabel("ID No.");
        JLabel nameLabel = new JLabel("Name");
        JLabel dateLabel = new JLabel("Date Range");
        JLabel statusLabel = new JLabel("Status");

        Font headerFont = new Font("Arial", Font.BOLD, 16);
        idLabel.setFont(headerFont);
        nameLabel.setFont(headerFont);
        dateLabel.setFont(headerFont);
        statusLabel.setFont(headerFont);

        headerPanel.add(idLabel);
        headerPanel.add(nameLabel);
        headerPanel.add(dateLabel);
        headerPanel.add(statusLabel);

        bookingsPanel.add(headerPanel);
        bookingsPanel.add(new JSeparator(SwingConstants.HORIZONTAL));

        // Booking Items
        addBookingItem(bookingsPanel, "1003", "CinemaLTD", "01/01/2025", "07/01/2025", "Confirmed");
        addBookingItem(bookingsPanel, "1004", "FilmProd", "20/01/2025", "28/01/2025", "Confirmed");
        addBookingItem(bookingsPanel, "1005", "TheatreCo", "05/01/2025", "10/01/2025", "Unconfirmed");

        JScrollPane scrollPane = new JScrollPane(bookingsPanel);
        scrollPane.setPreferredSize(new Dimension(550, 300));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(scrollPane);

        return contentPanel;
    }

    private void addBookingItem(JPanel panel, String id, String name, String startDate, String endDate, String status) {
        JPanel itemPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel idLabel = new JLabel(id);
        JLabel nameLabel = new JLabel(name);
        JLabel dateLabel = new JLabel(startDate + " - " + endDate);
        JLabel statusLabel = new JLabel(status);

        Font itemFont = new Font("Arial", Font.PLAIN, 14);
        idLabel.setFont(itemFont);
        nameLabel.setFont(itemFont);
        dateLabel.setFont(itemFont);
        statusLabel.setFont(itemFont);

        if ("Confirmed".equals(status)) {
            statusLabel.setForeground(new Color(0, 128, 0));
        } else {
            statusLabel.setForeground(new Color(200, 0, 0));
        }

        itemPanel.add(idLabel);
        itemPanel.add(nameLabel);
        itemPanel.add(dateLabel);
        itemPanel.add(statusLabel);

        panel.add(itemPanel);
        panel.add(new JSeparator(SwingConstants.HORIZONTAL));
    }
}