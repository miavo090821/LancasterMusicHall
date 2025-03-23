package operations.module;

import java.util.ArrayList;
import java.util.List;

public class GroupBooking {
    private String bookingName; // The group name
    private List<BookingDetails> bookingList; // Each entry might hold seat info, accessible seat needs, etc.

    public GroupBooking(String bookingName) {
        this.bookingName = bookingName;
        this.bookingList = new ArrayList<>();
    }

    public String getBookingName() {
        return bookingName;
    }

    public List<BookingDetails> getBookingList() {
        return bookingList;
    }

    // You can add or rename fields/methods to match exactly what the marketing interface calls.
}
