package operations.module;

import operations.entities.Event;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Event> events;

    public Schedule() {
        events = new ArrayList<>();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }
}
