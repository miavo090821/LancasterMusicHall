package GUI.MenuPanels.Calendar;

import javax.swing.*;
import java.awt.*;

public class DayCellPanel extends JPanel {
    private JLabel dayLabel;
    private JPanel eventPanel;

    public DayCellPanel(int dayNumber) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);

        // Day number label at the top
        dayLabel = new JLabel(String.valueOf(dayNumber));
        dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        add(dayLabel, BorderLayout.NORTH);

        // Panel to hold event names
        eventPanel = new JPanel();
        eventPanel.setLayout(new BoxLayout(eventPanel, BoxLayout.Y_AXIS));
        add(eventPanel, BorderLayout.CENTER);
    }

    // Clear existing event labels
    public void clearEvents() {
        eventPanel.removeAll();
    }

    // Add a new event name as a label into the cell
    public void addEvent(String eventName) {
        JLabel eventLabel = new JLabel(eventName);
        eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        eventLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        eventLabel.setOpaque(true);
        eventLabel.setBackground(Color.WHITE);
        eventPanel.add(eventLabel);
    }

    // Update the day number (if needed)
    public void updateDayNumber(int dayNumber) {
        dayLabel.setText(String.valueOf(dayNumber));
    }

    // Refresh the panel to show updated events
    public void refresh() {
        revalidate();
        repaint();
    }
}
