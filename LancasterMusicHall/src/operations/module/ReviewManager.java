package operations.module;

import operations.entities.Review;
import java.util.List;

/**
 * The ReviewManager class integrates with an external review website’s API
 * and local review data to manage reviews, including fetching, responding,
 * and filtering reviews for venues and shows.
 */
public class ReviewManager {

    /**
     * Fetches the latest reviews from the external review API and/or the local review data store.
     * <p>
     * In a full implementation, this method would call the external API and update the local data store.
     * </p>
     */
    public void fetchLatestReviews() {

    }

    /**
     * Responds to a specific review by adding a response.
     *
     * @param reviewId the unique identifier of the review to respond to
     * @param response the text of the response to the review
     */
    public void respondToReview(int reviewId, String response) {

    }

    /**
     * Retrieves a list of reviews for a specific venue.
     *
     * @param venueId the unique identifier of the venue
     * @return a {@link List} of {@link Review} objects associated with the specified venue,
     *         or an empty list if no reviews are found
     */
    public List<Review> getReviewsForVenue(int venueId) {

        return null;
    }

    /**
     * Retrieves a list of reviews for a specific show.
     *
     * @param showId the unique identifier of the show
     * @return a {@link List} of {@link Review} objects associated with the specified show,
     *         or an empty list if no reviews are found
     */
    public List<Review> getReviewsForShow(int showId) {

        return null;
    }
}
