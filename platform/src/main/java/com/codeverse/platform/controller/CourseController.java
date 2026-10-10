package com.codeverse.platform.controller;

import jakarta.validation.Valid;

import com.codeverse.platform.model.Course;
import com.codeverse.platform.model.CourseRequest; // Import new DTO
import com.codeverse.platform.service.CourseService;
import org.springframework.http.HttpStatus; // Import new Status
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // Import new annotation
import org.springframework.web.bind.annotation.RequestBody; // Import new annotation
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Responds to: GET http://localhost:8080/api/courses
    @GetMapping
    public ResponseEntity<List<Course>> listAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    // Responds to: GET http://localhost:8080/api/courses/1
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable int id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    // Responds to: GET http://localhost:8080/api/courses/search?query=java
    @GetMapping("/search")
    public ResponseEntity<List<Course>> searchCourses(
            @RequestParam(value = "query") String query
    ) {
        List<Course> courses = courseService.searchCourses(query);
        return ResponseEntity.ok(courses);
    }


    // Responds to: POST http://localhost:8080/api/courses
    @PostMapping
    public ResponseEntity<Course> createCourse(@Valid @RequestBody CourseRequest request) {
        Course newCourse = courseService.createCourse(request);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable int id,
            @Valid @RequestBody CourseRequest request
    ) {
        Course updatedCourse = courseService.updateCourse(id, request);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable int id,
            @RequestParam int adminUserId
    ) {
        courseService.deleteCourse(id, adminUserId);
        return ResponseEntity.noContent().build();
    }
}