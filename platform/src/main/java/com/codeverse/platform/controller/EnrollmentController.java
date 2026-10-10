package com.codeverse.platform.controller;

import jakarta.validation.Valid;

import com.codeverse.platform.model.Course;
import com.codeverse.platform.model.Enrollment;
import com.codeverse.platform.model.EnrollmentRequest;
import com.codeverse.platform.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Responds to: POST http://localhost:8080/api/enrollments
    @PostMapping("/api/enrollments")
    public ResponseEntity<Enrollment> enroll(@Valid @RequestBody EnrollmentRequest request) {
        Enrollment newEnrollment = enrollmentService.enrollUserInCourse(request);
        return new ResponseEntity<>(newEnrollment, HttpStatus.CREATED);
    }

    // Responds to: GET http://localhost:8080/api/users/1/courses
    @GetMapping("/api/users/{userId}/courses")
    public ResponseEntity<List<Course>> getEnrolledCourses(@PathVariable int userId) {
        List<Course> courses = enrollmentService.getCoursesForUser(userId);
        return ResponseEntity.ok(courses);
    }
}