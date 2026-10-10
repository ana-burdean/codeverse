package com.codeverse.platform.service;

import com.codeverse.platform.exception.InputValidationException;
import com.codeverse.platform.model.*;
import com.codeverse.platform.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ServiceFunctionalityTests {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserJpaRepository userRepo;

    private int adminId;

    @BeforeEach
    void ensureAdminExists() {
        // Create an admin for tests (H2 DB starts empty)
        User admin = new User();
        admin.setUsername("admin_test");
        admin.setPassword("admin123");
        admin.setRole(Role.ADMIN);
        adminId = userRepo.save(admin).getId();
    }

    @Test
    void register_and_login_user_works() {
        RegistrationRequest reg = new RegistrationRequest();
        reg.setUsername("ana");
        reg.setPassword("secret12");

        User created = userService.registerUser(reg);
        assertTrue(created.getId() > 0);
        assertEquals(Role.STUDENT, created.getRole());

        LoginRequest login = new LoginRequest();
        login.setUsername("ana");
        login.setPassword("secret12");

        User logged = userService.loginUser(login);
        assertEquals(created.getId(), logged.getId());
    }

    @Test
    void admin_can_create_course_and_it_can_be_found() {
        CourseRequest req = new CourseRequest();
        req.setAdminUserId(adminId);
        req.setTitle("Java Basics");
        req.setDescription("Intro course");

        Course created = courseService.createCourse(req);
        assertTrue(created.getId() > 0);
        assertEquals("Java Basics", created.getTitle());

        Course fetched = courseService.getCourseById(created.getId());
        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    void enroll_user_in_course_increases_studentCount() {
        // create student
        RegistrationRequest reg = new RegistrationRequest();
        reg.setUsername("student1");
        reg.setPassword("secret12");
        User student = userService.registerUser(reg);

        // create course
        CourseRequest courseReq = new CourseRequest();
        courseReq.setAdminUserId(adminId);
        courseReq.setTitle("Algorithms");
        courseReq.setDescription("Algorithms 101");
        Course course = courseService.createCourse(courseReq);

        // enroll
        EnrollmentRequest er = new EnrollmentRequest();
        er.setUserId(student.getId());
        er.setCourseId(course.getId());
        enrollmentService.enrollUserInCourse(er);

        Course after = courseService.getCourseById(course.getId());
        assertEquals(1, after.getStudentCount());
    }

    @Test
    void user_can_create_review_and_averageRating_updates() {
        // create student
        RegistrationRequest reg = new RegistrationRequest();
        reg.setUsername("student2");
        reg.setPassword("secret12");
        User student = userService.registerUser(reg);

        // create course
        CourseRequest courseReq = new CourseRequest();
        courseReq.setAdminUserId(adminId);
        courseReq.setTitle("Databases");
        courseReq.setDescription("SQL and DBMS");
        Course course = courseService.createCourse(courseReq);

        // review
        ReviewRequest rr = new ReviewRequest();
        rr.setUserId(student.getId());
        rr.setCourseId(course.getId());
        rr.setRating(5);
        rr.setComment("Great!");
        reviewService.createReview(rr);

        Course after = courseService.getCourseById(course.getId());
        assertEquals(5.0, after.getAverageRating());
    }
}
