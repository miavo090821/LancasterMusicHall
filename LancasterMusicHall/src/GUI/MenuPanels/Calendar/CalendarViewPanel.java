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

/**
 * Abstract base class representing a calendar view panel that displays events.
 * This panel provides functionality for handling mouse clicks on events and
 * filtering events based on dates.
 */
public abstract class CalendarViewPanel extends JPanel {
    /** The start date of the calendar view. */
    protected LocalDate viewStartDate;

    /** The end date of the calendar view. */
    protected LocalDate viewEndDate;

    /** A list of events displayed on this calendar view. */
    protected List<Event> events;

    /**
     * Constructs a CalendarViewPanel with the specified starting date, list of events,
     * and SQL connection object.
     *
     * @param startDate    The starting date for the view.
     * @param events       The list of events to be displayed.
     * @param sqlConnection A reference to the SQL connection (specific use defined in subclass).
     */
    public CalendarViewPanel(LocalDate startDate, List<Event> events, Object sqlConnection) {
        this.viewStartDate = startDate;
        this.events = events;
        initMouseListener();
    }

    /**
     * Initializes the mouse listener to detect clicks on event slots.
     * When a click is detected, it opens the EventDetailForm for the event at the clicked point.
     */
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

    /**
     * Gets a reference to the SQL connection.
     * Concrete classes must implement this to provide the actual connection.
     *
     * @return a SQLConnection object.
     */
    protected abstract Database.SQLConnection getSQLConnection();

    /**
     * Sets the starting date for the calendar view.
     *
     * @param date the LocalDate representing the new start date.
     */
    public abstract void setViewDate(LocalDate date);

    /**
     * Navigates the calendar view based on the given direction.
     * For example, positive for forward navigation and negative for backward navigation.
     *
     * @param direction an integer representing the navigation direction.
     */
    public abstract void navigate(int direction);

    /**
     * Renders the events on the panel.
     *
     * @param events the list of events to be rendered.
     */
    public abstract void renderEvents(List<Event> events);

    /**
     * Refreshes the view by recalculating layouts, re-rendering events, and updating the UI.
     */
    public abstract void refreshView();

    /**
     * Gets the start date of the calendar view.
     *
     * @return the LocalDate representing the start date.
     */
    public LocalDate getViewStartDate() {
        return viewStartDate;
    }

    /**
     * Gets the end date of the calendar view.
     *
     * @return the LocalDate representing the end date.
     */
    public LocalDate getViewEndDate() {
        return viewEndDate;
    }

    /**
     * Filters the events to include only those occurring between the specified start and end dates.
     *
     * @param start the LocalDate representing the start of the filter range.
     * @param end   the LocalDate representing the end of the filter range.
     * @return a List of events that occur between start and end (inclusive).
     */
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
     * Determines which event (if any) is located at the given point on the panel.
     * <p>
     * This default implementation assumes each event is rendered in a 100x50 rectangle
     * stacked vertically with a 5-pixel gap between them. Override this method with your actual rendering logic.
     * </p>
     *
     * @param p the Point where the mouse was clicked.
     * @return the Event at the given point, or null if no event is found.
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
