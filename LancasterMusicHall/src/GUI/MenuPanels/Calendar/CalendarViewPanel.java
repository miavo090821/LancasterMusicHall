package GUI.MenuPanels.Calendar;

import operations.module.Event;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class CalendarViewPanel extends JPanel {
    protected LocalDate viewStartDate;
    protected LocalDate viewEndDate;
    protected List<Event> events;

    public CalendarViewPanel(LocalDate startDate, List<Event> events) {
        this.viewStartDate = startDate;
        this.events = events;
    }
    public abstract void setViewDate(LocalDate date);

    public abstract void navigate(int direction);

    public abstract void renderEvents(List<Event> events);

    public abstract void refreshView();

    public LocalDate getViewStartDate() {
        return viewStartDate;
    }

    public LocalDate getViewEndDate() {
        return viewEndDate;
    }

    protected List<Event> filterEvents(LocalDate start, LocalDate end) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : events) {
            if (!event.getStartDate().isBefore(start) &&
                    !event.getStartDate().isAfter(end)) {
                filtered.add(event);
            }
        }
        return filtered;
    }
}
