package GUI.MenuPanels;

import Database.SQLConnection;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewsPanel extends JPanel {
    private SQLConnection sqlCon;
    private JList<Review> reviewsList;
    private DefaultListModel<Review> listModel;
    private JTextArea reviewDisplayArea;
    private JTextArea replyArea;
    private JButton saveButton;
    private JButton postButton;
    private JButton replyButton;
    private JButton refreshButton;
    private Review selectedReview;

    public ReviewsPanel(SQLConnection sqlCon) {
        this.sqlCon = sqlCon;
        setLayout(new BorderLayout(10, 10)); // Added gaps between components
        setBackground(Color.WHITE);

        // Create reviews list panel with resizing behavior
        JPanel listPanel = createListPanel();

        // Create a split pane to handle resizing between list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, createReviewPanel());
        splitPane.setResizeWeight(0.3); // 30% for list, 70% for details
        splitPane.setDividerLocation(300); // Initial divider position
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Load all reviews initially
        refreshReviews();
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("All Reviews"));
        panel.setMinimumSize(new Dimension(200, 0)); // Minimum width for list

        listModel = new DefaultListModel<>();
        reviewsList = new JList<>(listModel);
        reviewsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reviewsList.setCellRenderer(new ReviewListRenderer());
        reviewsList.addListSelectionListener(this::reviewSelected);

        JScrollPane scrollPane = new JScrollPane(reviewsList);
        scrollPane.setPreferredSize(new Dimension(300, Integer.MAX_VALUE));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshReviews());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Review Details"));

        reviewDisplayArea = new JTextArea();
        reviewDisplayArea.setEditable(false);
        reviewDisplayArea.setLineWrap(true);
        reviewDisplayArea.setWrapStyleWord(true);
        JScrollPane reviewScroll = new JScrollPane(reviewDisplayArea);
        reviewScroll.setPreferredSize(new Dimension(400, 0)); // Preferred width for details

        JPanel replyPanel = new JPanel(new BorderLayout());
        replyPanel.setBorder(BorderFactory.createTitledBorder("Reply to Review"));
        replyArea = new JTextArea(3, 40);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        JScrollPane replyScroll = new JScrollPane(replyArea);

        replyButton = new JButton("Submit Reply");
        replyButton.addActionListener(this::submitReply);

        replyPanel.add(replyScroll, BorderLayout.CENTER);
        replyPanel.add(replyButton, BorderLayout.SOUTH);

        panel.add(reviewScroll, BorderLayout.CENTER);
        panel.add(replyPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(Color.WHITE);

        saveButton = new JButton("Save Review");
        saveButton.addActionListener(e -> saveReview());

        postButton = new JButton("Post to Company");
        postButton.addActionListener(e -> postReview());

        panel.add(saveButton);
        panel.add(postButton);

        return panel;
    }

    private void refreshReviews() {
        try {
            listModel.clear();
            List<Review> reviews = fetchAllReviews();
            for (Review review : reviews) {
                listModel.addElement(review);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading reviews: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Review> fetchAllReviews() throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String query = "SELECT review_id, review_text, review_date, source, event_id FROM Review ORDER BY review_date DESC";

        try (PreparedStatement ps = sqlCon.getConnection().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Review review = new Review(
                        rs.getInt("review_id"),
                        rs.getString("review_text"),
                        rs.getDate("review_date"),
                        rs.getString("source"),
                        rs.getInt("event_id")
                );
                reviews.add(review);
            }
        }
        return reviews;
    }

    private void reviewSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            selectedReview = reviewsList.getSelectedValue();
            if (selectedReview != null) {
                displayReviewDetails(selectedReview);
            }
        }
    }

    private void displayReviewDetails(Review review) {
        String reviewText = String.format(
                "Review ID: %d\nEvent ID: %d\nDate: %s\nSource: %s\n\nReview:\n%s",
                review.getId(),
                review.getEventId(),
                review.getDate(),
                review.getSource() != null ? review.getSource() : "Not specified",
                review.getText()
        );
        reviewDisplayArea.setText(reviewText);
        replyArea.setText("");
    }

    private void saveReview() {
        if (selectedReview == null) {
            JOptionPane.showMessageDialog(this, "Please select a review first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Review saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void postReview() {
        if (selectedReview == null) {
            JOptionPane.showMessageDialog(this, "Please select a review first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Review has been sent to the company for processing",
                "Posted", JOptionPane.INFORMATION_MESSAGE);
    }

    private void submitReply(ActionEvent e) {
        if (selectedReview == null) {
            JOptionPane.showMessageDialog(this, "Please select a review first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String replyText = replyArea.getText().trim();
        if (replyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a reply", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Update the database
            String query = "UPDATE Review SET reply_text = ? WHERE review_id = ?";
            try (PreparedStatement ps = sqlCon.getConnection().prepareStatement(query)) {
                ps.setString(1, replyText);
                ps.setInt(2, selectedReview.getId());
                ps.executeUpdate();
            }

            // Update the selected review object
            selectedReview.setReply(replyText);

            // Refresh the display
            displayReviewDetails(selectedReview);

            // Refresh the list to show the updated reply status
            refreshReviews();

            JOptionPane.showMessageDialog(this, "Reply submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class to represent a Review
    private static class Review {
        private int id;
        private String text;
        private String reply;
        private java.sql.Date date;
        private String source;
        private int eventId;

        public Review(int id, String text, java.sql.Date date, String source, int eventId) {
            this.id = id;
            this.text = text;
            this.date = date;
            this.source = source;
            this.eventId = eventId;
        }

        // Getters and setters.
        public int getId() { return id; }
        public String getText() { return text; }
        public String getReply() { return reply; }
        public void setReply(String reply) { this.reply = reply; }
        public java.sql.Date getDate() { return date; }
        public String getSource() { return source; }
        public int getEventId() { return eventId; }

        @Override
        public String toString() {
            String shortText = text.length() > 30 ? text.substring(0, 30) + "..." : text;
            return String.format("Review #%d (Event %d) - %s", id, eventId, shortText);
        }
    }

    // Custom renderer to make the list items look better
    private static class ReviewListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Review) {
                Review review = (Review) value;
                setText(review.toString());
                if (review.getReply() != null) {
                    setForeground(new Color(0, 128, 0));
                } else {
                }
            }
            return this;
        }
    }
}