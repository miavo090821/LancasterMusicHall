package operations.entities;

public class Activity {
    private int activityId;
    private String name;

    // Default constructor
    public Activity() {
    }

    // Parameterized constructor
    public Activity(int activityId, String name) {
        this.activityId = activityId;
        this.name = name;
    }

    // Getter for activityId
    public int getActivityId() {
        return activityId;
    }

    // Setter for activityId
    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }
}
