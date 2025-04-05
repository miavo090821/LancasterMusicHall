package GUI.MenuPanels.Calendar;

import GUI.MenuPanels.Event.EventDetailForm;
import operations.entities.Event;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class CalendarViewPanel extends JPanel {
    protected LocalDate viewStartDate;
    protected LocalDate viewEndDate;
    protected List<Event> events;

    public CalendarViewPanel(LocalDate startDate, List<Event> events, Object sqlConnection) {
        this.viewStartDate = startDate;
        this.events = events;
        initMouseListener();
    }

    // Initialize a mouse listener for detecting clicks on event slots.
    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Event clickedEvent = getEventAtPoint(e.getPoint());
                if (clickedEvent != null) {
                    // Open the EventDetailForm using the clicked event's ID.
                    EventDetailForm eventDetailForm = new EventDetailForm(
                            (Frame) SwingUtilities.getWindowAncestor(CalendarViewPanel.this),
                            getSQLConnection(),
                            "" + clickedEvent.getId()
                    );
                    eventDetailForm.setVisible(true);
                }
            }
        });
    }

    // Concrete classes must provide a reference to the SQLConnection.
    protected abstract Database.SQLConnection getSQLConnection();

    // Set the date for the view.
    public abstract void setViewDate(LocalDate date);

    // Navigate (e.g., next/previous day/week/month).
    public abstract void navigate(int direction);

    // Render events on the panel.
    public abstract void renderEvents(List<Event> events);

    // Refresh the view (recalculate layout, etc.).
    public abstract void refreshView();

    public LocalDate getViewStartDate() {
        return viewStartDate;
    }

    public LocalDate getViewEndDate() {
        return viewEndDate;
    }

    // Filter events to those occurring between the given start and end dates.
    protected List<Event> filterEvents(LocalDate start, LocalDate end) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (!event.getStartDate().isBefore(start) && !event.getStartDate().isAfter(end)) {
                filtered.add(event);
            }
        }
        return filtered;
    }

    /**
     * Determines which event (if any) is located at the given point.
     * This default implementation assumes each event is rendered in a 100x50 rectangle
     * stacked vertically with a 5-pixel gap between them.
     * Override this method with your actual rendering logic.
     */
    protected Event getEventAtPoint(Point p) {
        int eventHeight = 55; // 50 for event plus 5 pixels gap.
        int index = p.y / eventHeight;
        if (index >= 0 && index < events.size()) {
            return events.get(index);
        }
        return null;
    }
}
