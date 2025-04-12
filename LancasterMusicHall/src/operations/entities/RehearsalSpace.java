package operations.entities;

/**
 * The {@code RehearsalSpace} class represents a rehearsal space entity used within the application.
 * <p>
 * This class holds details about a rehearsal space including its unique identifier and the maximum occupancy allowed.
 * </p>
 *
 */
public class RehearsalSpace {
    private int rehearsalSpaceId;    // Unique identifier for the rehearsal space
    private int maxOccupancy;        // Maximum number of occupants allowed

    /**
     * Default constructor.
     */
    public RehearsalSpace() {
        // No-argument constructor.
    }

    /**
     * Constructs a new {@code RehearsalSpace} with the specified ID and maximum occupancy.
     *
     * @param rehearsalSpaceId the unique identifier for the rehearsal space.
     * @param maxOccupancy     the maximum number of occupants allowed.
     */
    public RehearsalSpace(int rehearsalSpaceId, int maxOccupancy) {
        this.rehearsalSpaceId = rehearsalSpaceId;
        this.maxOccupancy = maxOccupancy;
    }

    /**
     * Returns the rehearsal space ID.
     *
     * @return the rehearsal space ID.
     */
    public int getRehearsalSpaceId() {
        return rehearsalSpaceId;
    }

    /**
     * Sets the rehearsal space ID.
     *
     * @param rehearsalSpaceId the unique identifier to be set for this rehearsal space.
     */
    public void setRehearsalSpaceId(int rehearsalSpaceId) {
        this.rehearsalSpaceId = rehearsalSpaceId;
    }

    /**
     * Returns the maximum occupancy allowed in the rehearsal space.
     *
     * @return the maximum occupancy.
     */
    public int getMaxOccupancy() {
        return maxOccupancy;
    }

    /**
     * Sets the maximum occupancy allowed in the rehearsal space.
     *
     * @param maxOccupancy the maximum occupancy to be set.
     */
    public void setMaxOccupancy(int maxOccupancy) {
        this.maxOccupancy = maxOccupancy;
    }
}
