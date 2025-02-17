package com.lancaster.operations.module;

public class ReviewManager {
    // Integrates with an external review website’s API and local review data

    public void fetchLatestReviews() { /* ... */ }
    public void respondToReview(int reviewId, String response) { /* ... */ }
    public List<Review> getReviewsForVenue(int venueId) { /* ... */ return null; }
    public List<Review> getReviewsForShow(int showId) { /* ... */ return null; }
}
