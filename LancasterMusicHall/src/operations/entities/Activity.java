package operations.entities;

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

    // Constructors, Getters, Setters...
}
