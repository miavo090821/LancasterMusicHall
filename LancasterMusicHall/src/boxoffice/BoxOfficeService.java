package boxoffice;

import operations.entities.Event;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class BoxOfficeService implements BoxOfficeInterface {

    private final Map<Integer, Event> Events = new HashMap<>();

    @Override
    public List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate) {
        return Events.values().stream()
                .filter(Event -> {
                    LocalDate EventDate = Event.getStartDate();
                    return (!EventDate.isBefore(startDate)) && (!EventDate.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean notifyEventChanges(int EventId, Event updatedDetails) {
        if (!Events.containsKey(EventId)) {
            System.err.println("Event ID not found: " + EventId);
            return false;
        }
        Events.put(EventId, updatedDetails);
        System.out.println("Event updated successfully: " + EventId);
        return true;
    }

    @Override
    public List<Seat> getSeatingPlanForEvent(int EventId) {
        return Events.containsKey(EventId) ?
                Events.get(EventId).getSeats() :
                Collections.emptyList();
    }

    @Override
    public boolean updateSeatingPlan(int EventId, List<Seat> updatedSeats) {
        if (!Events.containsKey(EventId)) {
            System.err.println("Event ID not found: " + EventId);
            return false;
        }
        Events.get(EventId).setSeats(updatedSeats);
        System.out.println("Seating plan updated for Event ID " + EventId);
        return true;
    }

    @Override
    public List<Seat> getHeldAccessibleSeats(int EventId) {
        List<Seat> seatingPlan = getSeatingPlanForEvent(EventId);
        if (seatingPlan == null) {
            return Collections.emptyList();
        }
        return seatingPlan.stream()
                .filter(seat -> seat.getType() == Seat.Type.WHEELCHAIR ||
                        seat.getType() == Seat.Type.COMPANION)
                .collect(Collectors.toList());
    }
}
