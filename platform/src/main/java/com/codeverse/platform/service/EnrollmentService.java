package com.codeverse.platform.service;

import com.codeverse.platform.model.Course;
import com.codeverse.platform.model.Enrollment;
import com.codeverse.platform.model.EnrollmentRequest;

import java.util.List;

public interface EnrollmentService {
    Enrollment enrollUserInCourse(EnrollmentRequest request);
    List<Course> getCoursesForUser(int userId);
}