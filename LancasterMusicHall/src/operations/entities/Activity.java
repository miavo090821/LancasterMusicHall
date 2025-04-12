package operations.entities;

/**
 * Represents a generic activity.
 * <p>
 * The {@code Activity} class serves as a base class for different types of activities by encapsulating
 * common attributes such as the activity ID and name.
 * </p>
 */
public class Activity {
    private int activityId;
    private String name;

    /**
     * Default constructor for {@code Activity}.
     * <p>
     * Creates an {@code Activity} object with default values.
     * </p>
     */
    public Activity() {
    }

    /**
     * Constructs a new {@code Activity} with the specified activity ID and name.
     *
     * @param activityId the unique identifier for the activity.
     * @param name       the name of the activity.
     */
    public Activity(int activityId, String name) {
        this.activityId = activityId;
        this.name = name;
    }

    /**
     * Retrieves the activity ID.
     *
     * @return the activity ID.
     */
    public int getActivityId() {
        return activityId;
    }

    /**
     * Sets the activity ID.
     *
     * @param activityId the activity ID to set.
     */
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    /**
     * Retrieves the activity name.
     *
     * @return the name of the activity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the activity name.
     *
     * @param name the name of the activity to set.
     */
    public void setName(String name) {
        this.name = name;
    }
}
