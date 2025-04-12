package operations.entities.Activities;

/**
 * Represents a meeting activity.
 * <p>
 * The {@code Meeting} class encapsulates properties related to a meeting, including the meeting ID and its name.
 * </p>
 */
public class Meeting {
    private int meetingId;           // ID field
    private String meetingName;

    /**
     * Default constructor for {@code Meeting}.
     */
    public Meeting() {
        // Default constructor
    }

    /**
     * Constructs a new {@code Meeting} with the specified meeting ID and name.
     *
     * @param meetingId   the unique identifier of the meeting.
     * @param meetingName the name of the meeting.
     */
    public Meeting(int meetingId, String meetingName) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
    }

    /**
     * Retrieves the meeting ID.
     *
     * @return the meeting ID.
     */
    public int getMeetingId() {
        return meetingId;
    }

    /**
     * Sets the meeting ID.
     *
     * @param meetingId the meeting ID to set.
     */
    public void setMeetingId(int meetingId) {
        this.meetingId = meetingId;
    }

    /**
     * Retrieves the meeting name.
     *
     * @return the meeting name.
     */
    public String getMeetingName() {
        return meetingName;
    }

    /**
     * Sets the meeting name.
     *
     * @param meetingName the meeting name to set.
     */
    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }
}
