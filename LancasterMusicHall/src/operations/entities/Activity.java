package operations.entities;

import operations.entities.Activities.Film;
import operations.entities.Activities.Meeting;
import operations.entities.Activities.Show;

public class Activity {
    private int activityId;          // ID field
    private String name;             // e.g., "Concert", "Meeting", "Film Show"
    private String startDate;        // Date Field
    private String endDate;          // Date Field
    private Type type;             // e.g., "Show", "Film", "Meeting"

    // Enum for seat types
    public enum Type {
        SHOW, FILM, MEETING
    }

    // Link to Meeting, Show, or Film if needed:
    private Meeting meeting;         // If it's a Meeting
    private Show show;               // If it's a Show
    private Film film;               // If it's a Film

    @Override
    public String toString() {
        return "Activity{" +
                "ID=" + activityId +
                ", Name='" + name + '\'' +
                ", Start Date=" + startDate +
                ", End Date=" + endDate +
                ", Type='" + type + '\'' +
                '}';
    }
}
