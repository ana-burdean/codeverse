package com.codeverse.platform.repository;

import com.codeverse.platform.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    List<Review> findByCourseId(int courseId);
    Review save(Review review);
    Optional<Review> findByUserIdAndCourseId(int userId, int courseId);
    void update(Review review);
    void deleteById(int id);
    Optional<Review> findById(int id);
}