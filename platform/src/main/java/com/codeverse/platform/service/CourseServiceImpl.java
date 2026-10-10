package com.codeverse.platform.service;

import com.codeverse.platform.exception.InputValidationException;
import com.codeverse.platform.exception.ResourceNotFoundException;
import com.codeverse.platform.exception.UnauthorizedException;
import com.codeverse.platform.model.*;
import com.codeverse.platform.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseJpaRepository courseRepo;
    private final LessonJpaRepository lessonRepo;
    private final ReviewJpaRepository reviewRepo;
    private final EnrollmentJpaRepository enrollmentRepo;
    private final UserJpaRepository userRepo;

    public CourseServiceImpl(
            CourseJpaRepository courseRepo,
            LessonJpaRepository lessonRepo,
            ReviewJpaRepository reviewRepo,
            EnrollmentJpaRepository enrollmentRepo,
            UserJpaRepository userRepo
    ) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.reviewRepo = reviewRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = courseRepo.findAll();
        courses.forEach(this::hydrateCourse);
        return courses;
    }

    @Override
    public Course getCourseById(int id) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));
        hydrateCourse(course);
        return course;
    }

    @Override
    public List<Course> searchCourses(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCourses();
        }
        List<Course> courses = courseRepo.findByTitleContainingIgnoreCase(query.trim());
        courses.forEach(this::hydrateCourse);
        return courses;
    }

    @Override
    public Course createCourse(CourseRequest request) {
        validateAdmin(request.getAdminUserId());

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InputValidationException("Course title cannot be empty.");
        }

        Course course = new Course();
        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());

        Course saved = courseRepo.save(course);
        hydrateCourse(saved);
        return saved;
    }

    @Override
    public Course updateCourse(int id, CourseRequest request) {
        validateAdmin(request.getAdminUserId());

        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found."));

        if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
            throw new InputValidationException("Course title cannot be empty.");
        }

        course.setTitle(request.getTitle().trim());
        course.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());

        Course saved = courseRepo.save(course);
        hydrateCourse(saved);
        return saved;
    }

    @Override
    public void deleteCourse(int id, int adminUserId) {
        validateAdmin(adminUserId);

        if (!courseRepo.existsById(id)) {
            throw new ResourceNotFoundException("Course not found.");
        }

        // Clean dependent tables first (helps keep DB tidy)
        lessonRepo.deleteAll(lessonRepo.findByCourseId(id));
        reviewRepo.deleteAll(reviewRepo.findByCourseId(id));
        enrollmentRepo.deleteAll(enrollmentRepo.findByCourseId(id));

        courseRepo.deleteById(id);
    }

    @Override
    public Lesson addLessonToCourse(LessonRequest request) {
        validateAdmin(request.getAdminUserId());

        int courseId = request.getCourseId();
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found.");
        }

        Lesson lesson = new Lesson();
        lesson.setCourseId(courseId);
        lesson.setTitle(request.getTitle().trim());
        lesson.setContent(request.getContent().trim());

        return lessonRepo.save(lesson);
    }

    @Override
    public Lesson updateLesson(int lessonId, LessonRequest request) {
        validateAdmin(request.getAdminUserId());

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        // Optional: ensure the lesson belongs to the provided courseId (if caller sends it)
        if (request.getCourseId() > 0 && lesson.getCourseId() != request.getCourseId()) {
            throw new InputValidationException("Lesson does not belong to the provided courseId.");
        }

        lesson.setTitle(request.getTitle().trim());
        lesson.setContent(request.getContent().trim());

        return lessonRepo.save(lesson);
    }

    @Override
    public void deleteLesson(int courseId, int lessonId, int adminUserId) {
        validateAdmin(adminUserId);

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found."));

        if (lesson.getCourseId() != courseId) {
            throw new InputValidationException("Lesson does not belong to this course.");
        }

        lessonRepo.deleteById(lessonId);
    }

    private void validateAdmin(int adminUserId) {
        User adminUser = userRepo.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));

        if (adminUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only admins can perform this action.");
        }
    }

    private void hydrateCourse(Course course) {
        int courseId = course.getId();

        // lessons
        course.setLessons(lessonRepo.findByCourseId(courseId));

        // studentCount
        course.setStudentCount(enrollmentRepo.findByCourseId(courseId).size());

        // averageRating
        List<Review> reviews = reviewRepo.findByCourseId(courseId);
        if (reviews.isEmpty()) {
            course.setAverageRating(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            course.setAverageRating(avg);
        }
    }
}
