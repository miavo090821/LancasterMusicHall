package operations.module;

import operations.entities.Event;
import java.util.ArrayList;
import java.util.List;

/**
 * The Schedule class represents a collection of events.
 * <p>
 * This class provides methods to add, remove, and retrieve events.
 * </p>
 */
public class Schedule {
    /**
     * The list of events managed by this schedule.
     */
    private List<Event> events;

    /**
     * Constructs a new Schedule instance with an empty list of events.
     */
    public Schedule() {
        events = new ArrayList<>();
    }

    /**
     * Returns the list of events in the schedule.
     *
     * @return a {@link List} of {@link Event} objects
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Adds an event to the schedule.
     *
     * @param event the {@link Event} to be added to the schedule
     */
    public void addEvent(Event event) {
        events.add(event);
    }

    /**
     * Removes an event from the schedule.
     *
     * @param event the {@link Event} to be removed from the schedule
     */
    public void removeEvent(Event event) {
        events.remove(event);
    }
}
