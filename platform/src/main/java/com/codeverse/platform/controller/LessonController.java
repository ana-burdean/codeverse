package com.codeverse.platform.controller;

import jakarta.validation.Valid;

import com.codeverse.platform.model.Lesson;
import com.codeverse.platform.model.LessonRequest;
import com.codeverse.platform.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final CourseService courseService;

    public LessonController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Lesson> createLesson(@Valid @RequestBody LessonRequest request) {
        Lesson newLesson = courseService.addLessonToCourse(request);
        return new ResponseEntity<>(newLesson, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(
            @PathVariable int id,
            @Valid @RequestBody LessonRequest request) {
        Lesson updatedLesson = courseService.updateLesson(id, request);
        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(
            @PathVariable int id,
            @RequestParam int courseId,
            @RequestParam int adminUserId) {
        courseService.deleteLesson(courseId, id, adminUserId);
        return ResponseEntity.noContent().build();
    }
}