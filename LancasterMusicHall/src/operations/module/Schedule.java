package operations.module;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private List<Event> events;

    public Schedule() {
        this.events = new ArrayList<>();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}
