package com.codeverse.platform.service;

import com.codeverse.platform.model.Review;
import com.codeverse.platform.model.ReviewRequest;
import java.util.List;

public interface ReviewService {
    Review createReview(ReviewRequest request);
    List<Review> getReviewsForCourse(int courseId);
    Review updateReview(int reviewId, ReviewRequest request); // Reuse ReviewRequest
    void deleteReview(int reviewId, int userId); // userId for security check
}