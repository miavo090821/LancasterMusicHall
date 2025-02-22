package operations.entities;

public class Venue {
    private int venueId;             // ID field
    private String name;             // e.g., "Main Hall", "Small Hall"
    private String type;             // e.g., "Hall", "Room", "Rehearsal Space"
    private int capacity;

    // Default constructor
    public Venue() {
    }

    // Parameterized constructor
    public Venue(int venueId, String name, String type, int capacity) {
        this.venueId = venueId;
        this.name = name;
        this.type = type;
        this.capacity = capacity;
    }

    // Getter for venueId
    public int getVenueId() {
        return venueId;
    }

    // Setter for venueId
    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for type
    public String getType() {
        return type;
    }

    // Setter for type
    public void setType(String type) {
        this.type = type;
    }

    // Getter for capacity
    public int getCapacity() {
        return capacity;
    }

    // Setter for capacity
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

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
