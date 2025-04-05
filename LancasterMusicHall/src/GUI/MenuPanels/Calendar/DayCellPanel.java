package GUI.MenuPanels.Calendar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;  // <-- Add this import

public class DayCellPanel extends JPanel {
    private JLabel dayLabel;
    private JPanel eventsPanel;
    private int dayNumber;
    private LocalDate date;

    public DayCellPanel(int dayNumber, LocalDate date) {
        this.dayNumber = dayNumber;
        this.date = date;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        setBackground(Color.WHITE);

        // Top: Display the day number.
        dayLabel = new JLabel(String.valueOf(dayNumber));
        dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(dayLabel, BorderLayout.NORTH);

        // Center: Panel to hold event labels.
        eventsPanel = new JPanel();
        eventsPanel.setLayout(new BoxLayout(eventsPanel, BoxLayout.Y_AXIS));
        eventsPanel.setBackground(Color.WHITE);
        JScrollPane eventsScroll = new JScrollPane(eventsPanel);
        eventsScroll.setBorder(null);
        add(eventsScroll, BorderLayout.CENTER);

        // Add mouse listener recursively to all components.
        addMouseListenerRecursively(this, new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onDayCellClicked(getDate());
            }
        });
    }

    private void onDayCellClicked(LocalDate date) {
        // Implement navigation logic here.
        System.out.println("Day cell clicked: " + date);
    }

    public LocalDate getDate() {
        return date;
    }

    public void addEvent(String eventText) {
        JLabel eventLabel = new JLabel(eventText);
        eventLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        eventLabel.setOpaque(true);
        eventLabel.setBackground(new Color(230, 255, 200));
        eventLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        eventsPanel.add(eventLabel);
    }

    public void clearEvents() {
        eventsPanel.removeAll();
    }

    public void refresh() {
        revalidate();
        repaint();
    }

    private void addMouseListenerRecursively(Component comp, MouseListener listener) {
        comp.addMouseListener(listener);
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                addMouseListenerRecursively(child, listener);
            }
        }
    }
}
