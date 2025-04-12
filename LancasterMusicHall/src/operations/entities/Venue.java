package operations.entities;

import java.math.BigDecimal;

/**
 * Represents a venue with various attributes such as id, name, location, capacity, layout,
 * seating flexibility, accessibility, and base rental cost.
 */
public class Venue {
    /**
     * The unique identifier of the venue.
     */
    private int venueId;

    /**
     * The name of the venue.
     */
    private String venueName;

    /**
     * The location of the venue.
     */
    private String venueLocation;

    /**
     * The total capacity of the venue.
     */
    private int venueCapacity;

    /**
     * The layout of the venue.
     */
    private String venueLayout;

    /**
     * Indicates whether the venue has flexible seating.
     */
    private boolean isFlexibleSeating;

    /**
     * Indicates whether the venue is accessible.
     */
    private boolean isAccessible;

    /**
     * The base rental cost for the venue.
     */
    private BigDecimal baseRentalCost;

    /**
     * Default constructor.
     */
    public Venue() {
    }

    /**
     * Constructs a new Venue with the specified details using BigDecimal for baseRentalCost.
     *
     * @param venueId           the unique identifier of the venue
     * @param venueName         the name of the venue
     * @param venueLocation     the location of the venue
     * @param venueCapacity     the capacity of the venue
     * @param venueLayout       the layout of the venue
     * @param isFlexibleSeating indicates if the venue supports flexible seating
     * @param isAccessible      indicates if the venue is accessible
     * @param baseRentalCost    the base rental cost as a BigDecimal
     */
    public Venue(int venueId, String venueName, String venueLocation, int venueCapacity, String venueLayout, boolean isFlexibleSeating, boolean isAccessible, BigDecimal baseRentalCost) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.venueLocation = venueLocation;
        this.venueCapacity = venueCapacity;
        this.venueLayout = venueLayout;
        this.isFlexibleSeating = isFlexibleSeating;
        this.isAccessible = isAccessible;
        this.baseRentalCost = baseRentalCost;
    }

    /**
     * Convenience constructor that accepts the base rental cost as a double and converts it to BigDecimal.
     *
     * @param venueId           the unique identifier of the venue
     * @param venueName         the name of the venue
     * @param venueLocation     the location of the venue
     * @param venueCapacity     the capacity of the venue
     * @param venueLayout       the layout of the venue
     * @param isFlexibleSeating indicates if the venue supports flexible seating
     * @param isAccessible      indicates if the venue is accessible
     * @param baseRentalCost    the base rental cost as a double
     */
    public Venue(int venueId, String venueName, String venueLocation, int venueCapacity, String venueLayout, boolean isFlexibleSeating, boolean isAccessible, double baseRentalCost) {
        this(venueId, venueName, venueLocation, venueCapacity, venueLayout, isFlexibleSeating, isAccessible, BigDecimal.valueOf(baseRentalCost));
    }

    /**
     * Gets the unique identifier of the venue.
     *
     * @return the venueId
     */
    public int getVenueId() {
        return venueId;
    }

    /**
     * Sets the unique identifier of the venue.
     *
     * @param venueId the venueId to set
     */
    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    /**
     * Gets the name of the venue.
     *
     * @return the venueName
     */
    public String getVenueName() {
        return venueName;
    }

    /**
     * Sets the name of the venue.
     *
     * @param venueName the venueName to set
     */
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    /**
     * Gets the location of the venue.
     *
     * @return the venueLocation
     */
    public String getVenueLocation() {
        return venueLocation;
    }

    /**
     * Sets the location of the venue.
     *
     * @param venueLocation the venueLocation to set
     */
    public void setVenueLocation(String venueLocation) {
        this.venueLocation = venueLocation;
    }

    /**
     * Gets the capacity of the venue.
     *
     * @return the venueCapacity
     */
    public int getVenueCapacity() {
        return venueCapacity;
    }

    /**
     * Sets the capacity of the venue.
     *
     * @param venueCapacity the venueCapacity to set
     */
    public void setVenueCapacity(int venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    /**
     * Gets the layout of the venue.
     *
     * @return the venueLayout
     */
    public String getVenueLayout() {
        return venueLayout;
    }

    /**
     * Sets the layout of the venue.
     *
     * @param venueLayout the venueLayout to set
     */
    public void setVenueLayout(String venueLayout) {
        this.venueLayout = venueLayout;
    }

    /**
     * Checks if the venue supports flexible seating.
     *
     * @return true if the venue has flexible seating, false otherwise
     */
    public boolean isFlexibleSeating() {
        return isFlexibleSeating;
    }

    /**
     * Sets whether the venue supports flexible seating.
     *
     * @param flexibleSeating true if the venue has flexible seating, false otherwise
     */
    public void setFlexibleSeating(boolean flexibleSeating) {
        isFlexibleSeating = flexibleSeating;
    }

    /**
     * Checks if the venue is accessible.
     *
     * @return true if the venue is accessible, false otherwise
     */
    public boolean isAccessible() {
        return isAccessible;
    }

    /**
     * Sets whether the venue is accessible.
     *
     * @param accessible true if the venue is accessible, false otherwise
     */
    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    /**
     * Gets the base rental cost of the venue.
     *
     * @return the baseRentalCost
     */
    public BigDecimal getBaseRentalCost() {
        return baseRentalCost;
    }

    /**
     * Sets the base rental cost of the venue.
     *
     * @param baseRentalCost the baseRentalCost to set as a BigDecimal
     */
    public void setBaseRentalCost(BigDecimal baseRentalCost) {
        this.baseRentalCost = baseRentalCost;
    }

    /**
     * Returns a string representation of the Venue object.
     *
     * @return a formatted string containing the venue's details
     */
    @Override
    public String toString() {
        return "Venue{" +
                "venueId=" + venueId +
                ", venueName='" + venueName + '\'' +
                ", venueLocation='" + venueLocation + '\'' +
                ", venueCapacity=" + venueCapacity +
                ", venueLayout='" + venueLayout + '\'' +
                ", isFlexibleSeating=" + isFlexibleSeating +
                ", isAccessible=" + isAccessible +
                ", baseRentalCost=" + baseRentalCost +
                '}';
    }
}
