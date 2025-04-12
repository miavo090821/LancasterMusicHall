package operations.entities;

/**
 * Represents a Hall within the application.
 * <p>
 * A Hall is defined by a unique identifier, the type of hall (e.g., "Main Hall", "Small Hall"),
 * and its maximum occupancy.
 * </p>
 *
 * @author
 */
public class Hall {
    private int hallId;              // Unique identifier for the hall
    private String type;             // Type of hall, for example, "Main Hall", "Small Hall"
    private int maxOccupancy;        // Maximum number of occupants allowed in the hall

    /**
     * Default constructor.
     */
    public Hall() {
        // No-argument constructor
    }

    /**
     * Constructs a Hall with the specified id, type, and maximum occupancy.
     *
     * @param hallId       the unique identifier for the hall.
     * @param type         the type or name of the hall (e.g., "Main Hall").
     * @param maxOccupancy the maximum occupancy of the hall.
     */
    public Hall(int hallId, String type, int maxOccupancy) {
        this.hallId = hallId;
        this.type = type;
        this.maxOccupancy = maxOccupancy;
    }

    /**
     * Returns the unique identifier of the hall.
     *
     * @return the hallId.
     */
    public int getHallId() {
        return hallId;
    }

    /**
     * Sets the unique identifier of the hall.
     *
     * @param hallId the hall id to set.
     */
    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    /**
     * Returns the type of the hall.
     *
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the hall.
     *
     * @param type the type (e.g., "Main Hall" or "Small Hall") to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the maximum occupancy of the hall.
     *
     * @return the maxOccupancy.
     */
    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    /**
     * Sets the maximum occupancy of the hall.
     *
     * @param maxOccupancy the maximum occupancy to set.
     */
    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
}
