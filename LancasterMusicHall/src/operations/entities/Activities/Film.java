package operations.entities.Activities;

import operations.entities.Activity;

/**
 * Represents a film activity and extends the {@link Activity} class.
 * <p>
 * The {@code Film} class encapsulates specific properties of a film such as its ID,
 * name, release year, and age rating.
 * </p>
 */
public class Film extends Activity {
    private int filmId;              // ID field
    private String name;
    private String year;
    private String ageRating;

    /**
     * Default constructor for {@code Film}.
     */
    public Film() {
        // Default constructor
    }

    /**
     * Constructs a new {@code Film} with the specified details.
     *
     * @param filmId    the unique identifier of the film.
     * @param name      the name of the film.
     * @param year      the release year of the film.
     * @param ageRating the age rating of the film.
     */
    public Film(int filmId, String name, String year, String ageRating) {
        this.filmId = filmId;
        this.name = name;
        this.year = year;
        this.ageRating = ageRating;
    }

    /**
     * Retrieves the film ID.
     *
     * @return the film ID.
     */
    public int getFilmId() {
        return filmId;
    }

    /**
     * Sets the film ID.
     *
     * @param filmId the film ID to set.
     */
    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    /**
     * Retrieves the film name.
     *
     * @return the name of the film.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the film name.
     *
     * @param name the name of the film to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the release year of the film.
     *
     * @return the release year of the film.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the release year of the film.
     *
     * @param year the release year to set.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Retrieves the age rating of the film.
     *
     * @return the age rating of the film.
     */
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * Sets the age rating of the film.
     *
     * @param ageRating the age rating to set.
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }
}
