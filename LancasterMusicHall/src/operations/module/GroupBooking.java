package operations.module;

import java.util.ArrayList;
import java.util.List;

public class GroupBooking {
    private String groupName;
    private List<BookingDetails> bookingList;

    public GroupBooking(String groupName) {
        this.groupName = groupName;
        this.bookingList = new ArrayList<>();
    }

    public String getBookingName() {
        return groupName;
    }
//  here is the method to show all the bookings details
    public List<BookingDetails> getBookingList() {
        return bookingList;
    }

    public void addBookingDetails(BookingDetails details) {
        bookingList.add(details);
    }
}
