package operations.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * The {@code IReport} interface defines methods that are used to generate reports
 * related to ticketing, seating, refunds, and revenue. Implementations of this interface
 * must provide concrete functionality to retrieve report details and generate a report string.
 *
 * <p>Typical report implementations might include details such as report title, report date,
 * event start time, pricing information, seating details (sold and refunded), refund notes, and
 * total revenue.</p>
 */
public interface IReport {

    /**
     * Retrieves the title of the report.
     *
     * @return the report title.
     */
    String getTitle();

    /**
     * Retrieves the date associated with the report.
     *
     * @return the report date.
     */
    Date getDate();

    /**
     * Retrieves the start time of the event or report period.
     *
     * @return the start time as a double.
     */
    double getStartTime();

    /**
     * Retrieves the price associated with the event or ticket.
     *
     * @return the price as an integer.
     */
    int getPrice();

    /**
     * Retrieves the price as a double value.
     *
     * @return the price as a double.
     */
    double getDoublePrice();

    /**
     * Retrieves a mapping of ticket types to the number of seats sold for each type.
     *
     * @return a map where the key is an {@link ETicketType} and the value is the number of seats sold.
     */
    Map<ETicketType, Integer> getSeatsSold();

    /**
     * Retrieves a mapping of ticket types to the number of seats refunded for each type.
     *
     * @return a map where the key is an {@link ETicketType} and the value is the number of refunded seats.
     */
    Map<ETicketType, Integer> getRefundSeats();

    /**
     * Retrieves the total sum of refunds issued.
     *
     * @return the refund sum.
     */
    double getRefundSum();

    /**
     * Retrieves a list of notes regarding refunds.
     *
     * @return a list of refund notes.
     */
    List<String> getRefundNotes();

    /**
     * Retrieves the total revenue generated.
     *
     * @return the total revenue.
     */
    double getTotalRevenue();

    /**
     * Generates and returns the full report as a formatted string.
     *
     * @return the generated report.
     */
    String generateReport();
}
