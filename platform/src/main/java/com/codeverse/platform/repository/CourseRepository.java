package com.codeverse.platform.repository;

import com.codeverse.platform.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    List<Course> findAll();
    Optional<Course> findById(int id);
    List<Course> findByNameContaining(String query);
    Course save(Course course);
    void update(Course course);
    void deleteById(int id);
}