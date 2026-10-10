package com.codeverse.platform.service;

import com.codeverse.platform.model.Course;
import com.codeverse.platform.model.CourseRequest;
import com.codeverse.platform.model.Lesson;
import com.codeverse.platform.model.LessonRequest;
import java.util.List;

public interface CourseService {

    Lesson addLessonToCourse(LessonRequest request);

    Lesson updateLesson(int lessonId, LessonRequest request);
    void deleteLesson(int courseId, int lessonId, int adminUserId);
    void deleteCourse(int id, int adminUserId);
    Course updateCourse(int id, CourseRequest request);
    Course createCourse(CourseRequest request);
    List<Course> getAllCourses();
    Course getCourseById(int id);
    List<Course> searchCourses(String query);
}