package operations.interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReport {
    String getTitle();
    Date getDate();
    double getStartTime();
    int getPrice();
    double getDoublePrice();
    Map<ETicketType, Integer> getSeatsSold();
    Map<ETicketType, Integer> getRefundSeats();
    double getRefundSum();
    List<String> getRefundNotes();
    double getTotalRevenue();
    String generateReport();
}
