package com.codeverse.platform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(name = "uq_enrollment_user_course", columnNames = {"user_id", "course_id"}))
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    public Enrollment() {
    }

    public Enrollment(int id, int userId, int courseId) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
    }
}
