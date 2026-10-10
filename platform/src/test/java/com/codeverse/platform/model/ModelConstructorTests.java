package com.codeverse.platform.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ModelConstructorTests {

    @Test
    void user_allArgsConstructor_setsFields() {
        User u = new User(1, "ana", "secret", Role.STUDENT);
        assertEquals(1, u.getId());
        assertEquals("ana", u.getUsername());
        assertEquals("secret", u.getPassword());
        assertEquals(Role.STUDENT, u.getRole());
    }

    @Test
    void course_allArgsConstructor_setsFields() {
        Course c = new Course(10, "Java", "Basics");
        assertEquals(10, c.getId());
        assertEquals("Java", c.getTitle());
        assertEquals("Basics", c.getDescription());
    }

    @Test
    void lesson_allArgsConstructor_setsFields() {
        Lesson l = new Lesson(5, 10, "Intro", "Hello");
        assertEquals(5, l.getId());
        assertEquals(10, l.getCourseId());
        assertEquals("Intro", l.getTitle());
        assertEquals("Hello", l.getContent());
    }

    @Test
    void enrollment_allArgsConstructor_setsFields() {
        Enrollment e = new Enrollment(2, 1, 10);
        assertEquals(2, e.getId());
        assertEquals(1, e.getUserId());
        assertEquals(10, e.getCourseId());
    }

    @Test
    void review_allArgsConstructor_setsFields() {
        Review r = new Review(3, 1, 10, 5, "great");
        assertEquals(3, r.getId());
        assertEquals(1, r.getUserId());
        assertEquals(10, r.getCourseId());
        assertEquals(5, r.getRating());
        assertEquals("great", r.getComment());
    }
}