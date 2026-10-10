package com.codeverse.platform.service;

import com.codeverse.platform.exception.InputValidationException;
import com.codeverse.platform.exception.ResourceNotFoundException;
import com.codeverse.platform.model.Course;
import com.codeverse.platform.model.Enrollment;
import com.codeverse.platform.model.EnrollmentRequest;
import com.codeverse.platform.model.Review;
import com.codeverse.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentJpaRepository enrollmentRepo;
    private final CourseJpaRepository courseRepo;
    private final LessonJpaRepository lessonRepo;
    private final ReviewJpaRepository reviewRepo;
    private final UserJpaRepository userRepo;

    public EnrollmentServiceImpl(
            EnrollmentJpaRepository enrollmentRepo,
            CourseJpaRepository courseRepo,
            LessonJpaRepository lessonRepo,
            ReviewJpaRepository reviewRepo,
            UserJpaRepository userRepo
    ) {
        this.enrollmentRepo = enrollmentRepo;
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.reviewRepo = reviewRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Enrollment enrollUserInCourse(EnrollmentRequest request) {
        int userId = request.getUserId();
        int courseId = request.getCourseId();

        if (userId <= 0 || courseId <= 0) {
            throw new InputValidationException("userId and courseId must be positive.");
        }

        userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        courseRepo.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if (enrollmentRepo.findByUserIdAndCourseId(userId, courseId).isPresent()) {
            throw new InputValidationException("User is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUserId(userId);
        enrollment.setCourseId(courseId);

        return enrollmentRepo.save(enrollment);
    }

    @Override
    public List<Course> getCoursesForUser(int userId) {
        userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found."));

        List<Integer> courseIds = enrollmentRepo.findByUserId(userId)
                .stream()
                .map(Enrollment::getCourseId)
                .collect(Collectors.toList());

        List<Course> courses = courseRepo.findAllById(courseIds);
        courses.forEach(this::hydrateCourse);
        return courses;
    }

    private void hydrateCourse(Course course) {
        int courseId = course.getId();
        course.setLessons(lessonRepo.findByCourseId(courseId));
        course.setStudentCount(enrollmentRepo.findByCourseId(courseId).size());

        List<Review> reviews = reviewRepo.findByCourseId(courseId);
        if (reviews.isEmpty()) {
            course.setAverageRating(0);
        } else {
            course.setAverageRating(reviews.stream().mapToInt(Review::getRating).average().orElse(0));
        }
    }
}
