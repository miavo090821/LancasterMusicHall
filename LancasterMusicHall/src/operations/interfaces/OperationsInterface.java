//package operations.interfaces;
//// this is the marketing file for operation team
//// it led to multiple classes created to support this
//import operations.entities.Booking;
//import operations.entities.Seat;
//import operations.entities.Event;
//import operations.module.GroupBooking;
//import operations.module.Schedule;
//
//import java.util.List;
//import java.time.*;
//public interface OperationsInterface {
//
//
//
//    /** This method is called in order to check if any new films
//     * have been added to the calendar
//     * @param schedule to check against
//     */
//    String getFilmUpdate(Schedule schedule);
//
//    /** This method is called to check the room that an event is taking place in
//     * @param event to check which room it takes place in
//     * @return a String with the room name
//     */
//    String getEventRoom(Event event);
//
//    /** This methods checks the date of which the event takes place in
//     *
//     * @param event to check
//     * @return the date in YYYY-MM-DD format
//     */
//    LocalDate getEventDate(Event event);
//
//    /** This method checks the time when the booking was made
//     *
//     * @param event to check
//     * @return time it was booked
//     */
//    LocalTime getEventTime(Event event);
//
//    /** This method is called to receive the purpose of a certain booking
//     * @param event to check against
//     * @return a String that states the reason
//     */
//    String reasonofBooking(Event event);
//
//    /** This method is called to get the number of attendees
//     * @param event to check number of attendees of
//     * @return number of attendees
//     */
//    int getOverallAttendance(Event event);
//
//
//    /** This method is called to receive the list of held seats for group bookings,
//     * custom Booking objects have been made so the individual data can be extracted via
//     * dedicated accessor methods build into said objects.
//     *
//     */
//    List<Seat> getGroupBookingHeldSeats(GroupBooking group);
//
//
//    /** This method is to receive the group name
//     * @param group you want to check against
//     * @return groupName in form of a string
//     */
//    String getGroupName(GroupBooking group);
//
//    /** This method is called to receive the name of the organizer of said booking
//     * @param booking itself
//     * @return organizerName in form of a string
//     */
//    String getOrganizerName(Booking booking);
//
//
//    /** This method is to be called to receive the number of seats that a group requires
//     * @param booking to check
//     */
//    int getNumberofSeatsNeeded(GroupBooking booking);
//
//    /** This method is to be called to receive the number of members of a group booking that need an accessible seat
//     * @param groupbooking to check
//     * @return integer with the number of seats
//     */
//    int getNumberofAccessibleSeatsNeeded(GroupBooking groupbooking);
//
//    /** Retrieves all seats currently booked for event
//     *
//     * @param event The event for which seats are requested
//     * @return A list of seats that are booked
//     */
//    List<Seat> getBookedSeatsForEvent(Event event);
//
//
//    /** This method is to be called to get the record of discounted ticket
//     *  purchases for students/staff
//     *
//     */
//    List<Booking> getRecordofDiscounts(Event event);
//
//
//    /** This method is to be called to get a schedule of venue tours for students
//     * @param overallSchedule to check
//     * @return
//     */
//    Schedule getVenueTours(Schedule overallSchedule);
//
//
//    /** This method is to be called to receive the expected number of attendees
//     * @param event to check against
//     */
//    int getAttendeesCount(Event event);
//}
