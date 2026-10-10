package com.codeverse.platform.repository;

import com.codeverse.platform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseJpaRepository extends JpaRepository<Course, Integer> {
    List<Course> findByTitleContainingIgnoreCase(String query);
}
