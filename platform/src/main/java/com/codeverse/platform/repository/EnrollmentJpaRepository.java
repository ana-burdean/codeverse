package com.codeverse.platform.repository;

import com.codeverse.platform.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentJpaRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findByUserId(int userId);
    List<Enrollment> findByCourseId(int courseId);
    Optional<Enrollment> findByUserIdAndCourseId(int userId, int courseId);
}
