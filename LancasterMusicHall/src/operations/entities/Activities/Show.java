package operations.entities.Activities;

/**
 * Represents a show activity.
 * <p>
 * The {@code Show} class encapsulates properties specific to a show including its unique identifier,
 * name, and age rating (e.g., "PG", "12A", "18").
 * </p>
 */
public class Show {
    private int showId;              // ID field
    private String name;
    private String ageRating;        // e.g., "PG", "12A", "18"

    /**
     * Default constructor for {@code Show}.
     */
    public Show() {
        // Default constructor
    }

    /**
     * Constructs a new {@code Show} with the specified details.
     *
     * @param showId    the unique identifier for the show.
     * @param name      the name of the show.
     * @param ageRating the age rating of the show (e.g., "PG", "12A", "18").
     */
    public Show(int showId, String name, String ageRating) {
        this.showId = showId;
        this.name = name;
        this.ageRating = ageRating;
    }

    /**
     * Retrieves the show ID.
     *
     * @return the unique identifier of the show.
     */
    public int getShowId() {
        return showId;
    }

    /**
     * Sets the show ID.
     *
     * @param showId the unique identifier of the show to set.
     */
    public void setShowId(int showId) {
        this.showId = showId;
    }

    /**
     * Retrieves the show name.
     *
     * @return the name of the show.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the show name.
     *
     * @param name the name of the show to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the age rating for the show.
     *
     * @return the age rating of the show (e.g., "PG", "12A", "18").
     */
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * Sets the age rating for the show.
     *
     * @param ageRating the age rating to set for the show (e.g., "PG", "12A", "18").
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
}
