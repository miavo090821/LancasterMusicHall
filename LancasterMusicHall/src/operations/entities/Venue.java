package operations.entities;

import java.math.BigDecimal;

public class Venue {
    private int venueId;              // corresponds to venue_id
    private String venueName;         // corresponds to venue_name
    private String venueLocation;     // corresponds to venue_location
    private int venueCapacity;        // corresponds to venue_capacity
    private String venueLayout;       // corresponds to venue_layout
    private boolean isFlexibleSeating; // corresponds to is_flexible_seating
    private boolean isAccessible;     // corresponds to is_accessible
    private BigDecimal baseRentalCost; // corresponds to base_rental_cost

    // Default constructor
    public Venue() {
    }

    // Parameterized constructor using BigDecimal for baseRentalCost
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

    // Convenience constructor using double for baseRentalCost
    public Venue(int venueId, String venueName, String venueLocation, int venueCapacity, String venueLayout, boolean isFlexibleSeating, boolean isAccessible, double baseRentalCost) {
        this(venueId, venueName, venueLocation, venueCapacity, venueLayout, isFlexibleSeating, isAccessible, BigDecimal.valueOf(baseRentalCost));
    }

    // Getters and setters

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueLocation() {
        return venueLocation;
    }

    public void setVenueLocation(String venueLocation) {
        this.venueLocation = venueLocation;
    }

    public int getVenueCapacity() {
        return venueCapacity;
    }

    public void setVenueCapacity(int venueCapacity) {
        this.venueCapacity = venueCapacity;
    }

    public String getVenueLayout() {
        return venueLayout;
    }

    public void setVenueLayout(String venueLayout) {
        this.venueLayout = venueLayout;
    }

    public boolean isFlexibleSeating() {
        return isFlexibleSeating;
    }

    public void setFlexibleSeating(boolean flexibleSeating) {
        isFlexibleSeating = flexibleSeating;
    }

    public boolean isAccessible() {
        return isAccessible;
    }

    public void setAccessible(boolean accessible) {
        isAccessible = accessible;
    }

    public BigDecimal getBaseRentalCost() {
        return baseRentalCost;
    }

    public void setBaseRentalCost(BigDecimal baseRentalCost) {
        this.baseRentalCost = baseRentalCost;
    }

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
