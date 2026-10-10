package com.codeverse.platform.repository;

import com.codeverse.platform.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonJpaRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseId(int courseId);
}
