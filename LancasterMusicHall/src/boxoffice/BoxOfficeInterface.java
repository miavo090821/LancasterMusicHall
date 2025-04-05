package boxoffice;

import operations.entities.Event;
import operations.entities.Seat;

import java.time.LocalDate;
import java.util.List;

public interface BoxOfficeInterface {

    // --- Calendar Access ---

    /**
     * Get all Events (shows, films, meetings, etc.) scheduled within a specific date range.
     * @param startDate The start of the date range.
     * @param endDate   The end of the date range.
     * @return A list of Events happening within this period, each Event has a:
     * - Title
     * - Date & Time
     * - Location (room/venue)
     * - Ticket price
     * - Capacity
     * - Seating plan
     */
    List<Event> getEventsByDateRange(LocalDate startDate, LocalDate endDate);

    /**
     * Notify the Box Office when a Event changes (e.g., if a show is rescheduled or canceled).
     * @param EventId The ID of the Event being updated.
     * @param updatedDetails The new Event details.
     * @return True if the update was successful.
     */
    boolean notifyEventChanges(int EventId, Event updatedDetails);

    // --- Seating Plans ---

    /**
     * Get the seating chart for a specific event.
     * @param EventId The ID of the event.
     * @return A list of seats showing:
     * - Seat row, and number
     * - Whether they are available, sold, held, or have restricted views
     * - If they are wheelchair-accessible or their companions
     */
    List<Seat> getSeatingPlanForEvent(int EventId);

    /**
     * Update the seating chart for an event (e.g., mark seats as wheelchair-accessible or restricted view).
     * @param EventId The ID of the event.
     * @param updatedSeats The new seating details.
     * @return True if the update was successful.
     */
    boolean updateSeatingPlan(int EventId, List<Seat> updatedSeats);

    /**
     * Get a list of seats reserved for wheelchair users and their companions.
     * @param EventId The ID of the event.
     * @return A list of seats specifically held for accessibility.
     */
    List<Seat> getHeldAccessibleSeats(int EventId);
}

// Not sure if they need these features, since they weren’t mentioned in the requirements.
// Leaving them here just in case.

// --- Sales Monitoring ---
//
// /**
//  * Get live ticket sales data, including the number of tickets sold and total revenue.
//  * @return A report on current sales numbers.
//  */
// FinancialRecord getSalesDashboardData();
//
// --- Revenue Info ---
//
// /**
//  * Get a financial report for a specific event, including revenue, costs, and profit.
//  * @param EventId The ID of the event.
//  * @return A detailed financial breakdown.
//  */
// FinancialRecord getRevenueBreakdownForEvent(int EventId);
