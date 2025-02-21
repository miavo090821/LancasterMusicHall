package operations.entities;

public class Venue {
    private int venueId;             // ID field
    private String name;             // e.g., "Main Hall", "Small Hall"
    private String type;             // e.g., "Hall", "Room", "Rehearsal Space"
    private int capacity;

    // Constructors, Getters, Setters...

    @Override
    public String toString() {
        return "Venue{" +
                "Venue ID=" + venueId +
                ", Name='" + name + '\'' +
                ", Type='" + type + '\'' +
                ", Capacity=" + capacity +
                '}';
    }
}
