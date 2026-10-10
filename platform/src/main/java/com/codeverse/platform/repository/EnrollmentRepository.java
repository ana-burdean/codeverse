package com.codeverse.platform.repository;

import com.codeverse.platform.model.Enrollment;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository {
    Optional<Enrollment> findByUserIdAndCourseId(int userId, int courseId);
    List<Enrollment> findByUserId(int userId);
    List<Enrollment> findByCourseId(int courseId);
    Enrollment save(Enrollment enrollment);
}
