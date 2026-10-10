package com.codeverse.platform.service;

import com.codeverse.platform.exception.InputValidationException;
import com.codeverse.platform.exception.ResourceNotFoundException;
import com.codeverse.platform.exception.UnauthorizedException;
import com.codeverse.platform.model.Review;
import com.codeverse.platform.model.ReviewRequest;
import com.codeverse.platform.repository.CourseJpaRepository;
import com.codeverse.platform.repository.ReviewJpaRepository;
import com.codeverse.platform.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewJpaRepository reviewRepo;
    private final UserJpaRepository userRepo;
    private final CourseJpaRepository courseRepo;

    public ReviewServiceImpl(ReviewJpaRepository reviewRepo, UserJpaRepository userRepo, CourseJpaRepository courseRepo) {
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
        this.courseRepo = courseRepo;
    }

    @Override
    public Review createReview(ReviewRequest request) {
        int userId = request.getUserId();
        int courseId = request.getCourseId();

        userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InputValidationException("Rating must be between 1 and 5.");
        }

        if (reviewRepo.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new InputValidationException("You already reviewed this course.");
        }

        Review review = new Review();
        review.setUserId(userId);
        review.setCourseId(courseId);
        review.setRating(request.getRating());
        review.setComment(request.getComment() == null ? "" : request.getComment().trim());

        return reviewRepo.save(review);
    }

    @Override
    public List<Review> getReviewsForCourse(int courseId) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found.");
        }
        return reviewRepo.findByCourseId(courseId);
    }

    @Override
    public Review updateReview(int reviewId, ReviewRequest request) {
        Review existing = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        if (existing.getUserId() != request.getUserId()) {
            throw new UnauthorizedException("You can only update your own review.");
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new InputValidationException("Rating must be between 1 and 5.");
        }

        existing.setRating(request.getRating());
        existing.setComment(request.getComment() == null ? "" : request.getComment().trim());

        return reviewRepo.save(existing);
    }

    @Override
    public void deleteReview(int reviewId, int userId) {
        Review existing = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found."));

        if (existing.getUserId() != userId) {
            throw new UnauthorizedException("You can only delete your own review.");
        }

        reviewRepo.deleteById(reviewId);
    }
}
