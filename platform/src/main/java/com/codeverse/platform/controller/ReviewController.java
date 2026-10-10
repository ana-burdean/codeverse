package com.codeverse.platform.controller;

import jakarta.validation.Valid;

import com.codeverse.platform.model.Review;
import com.codeverse.platform.model.ReviewRequest;
import com.codeverse.platform.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviewsForCourse(@RequestParam int courseId) {
        List<Review> reviews = reviewService.getReviewsForCourse(courseId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    public ResponseEntity<Review> createReview(@Valid @RequestBody ReviewRequest request) {
        Review newReview = reviewService.createReview(request);
        return new ResponseEntity<>(newReview, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(
            @PathVariable int id,
            @Valid @RequestBody ReviewRequest request // Contains userId for security check
    ) {
        Review updatedReview = reviewService.updateReview(id, request);
        return ResponseEntity.ok(updatedReview);
    }

    // Responds to: DELETE http://localhost:8080/api/reviews/{id}?userId=1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable int id,
            @RequestParam int userId
    ) {
        reviewService.deleteReview(id, userId);
        return ResponseEntity.noContent().build();
    }

}