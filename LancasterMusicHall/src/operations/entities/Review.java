package operations.entities;

/**
 * The Review class represents a review provided by a customer on a booking, venue, or activity.
 * It encapsulates details such as the review text and a rating (e.g., 1–5 stars).
 */
public class Review {
    // ID field representing the review identifier.
    private int reviewId;

    // The customer who provided the review.
    private Customer customer;

    // The booking related to the review.
    private Booking booking;

    // The venue related to the review.
    private Venue venue;

    // The activity related to the review.
    private Activity activity;

    // The text of the review.
    private String reviewText;

    // The rating given in the review (e.g., 1–5 stars).
    private int rating;

    /**
     * Default constructor.
     */
    public Review() {
    }

    /**
     * Constructs a new Review with the specified details.
     *
     * @param reviewId the unique identifier for the review
     * @param customer the customer who provided the review
     * @param booking the booking related to the review
     * @param venue the venue related to the review
     * @param activity the activity related to the review
     * @param reviewText the text content of the review
     * @param rating the rating provided in the review (e.g., 1–5 stars)
     */
    public Review(int reviewId, Customer customer, Booking booking, Venue venue, Activity activity, String reviewText, int rating) {
        this.reviewId = reviewId;
        this.customer = customer;
        this.booking = booking;
        this.venue = venue;
        this.activity = activity;
        this.reviewText = reviewText;
        this.rating = rating;
    }

    /**
     * Gets the review identifier.
     *
     * @return the review ID
     */
    public int getReviewId() {
        return reviewId;
    }

    /**
     * Sets the review identifier.
     *
     * @param reviewId the review ID to set
     */
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * Gets the customer who provided the review.
     *
     * @return the customer
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer who provided the review.
     *
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the booking associated with the review.
     *
     * @return the booking
     */
    public Booking getBooking() {
        return booking;
    }

    /**
     * Sets the booking associated with the review.
     *
     * @param booking the booking to set
     */
    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    /**
     * Gets the venue associated with the review.
     *
     * @return the venue
     */
    public Venue getVenue() {
        return venue;
    }

    /**
     * Sets the venue associated with the review.
     *
     * @param venue the venue to set
     */
    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * Gets the activity associated with the review.
     *
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity associated with the review.
     *
     * @param activity the activity to set
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Gets the text of the review.
     *
     * @return the review text
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Sets the text of the review.
     *
     * @param reviewText the review text to set
     */
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    /**
     * Gets the rating provided in the review.
     *
     * @return the rating (e.g., 1–5 stars)
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating for the review.
     *
     * @param rating the rating to set (e.g., 1–5 stars)
     */
    public void setRating(int rating) {
        this.rating = rating;
    }
}
