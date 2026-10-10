package com.codeverse.platform.repository;

import com.codeverse.platform.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewJpaRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCourseId(int courseId);
    List<Review> findByUserId(int userId);
    Optional<Review> findByUserIdAndCourseId(int userId, int courseId);
}
