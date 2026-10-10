package com.codeverse.platform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "reviews",
       uniqueConstraints = @UniqueConstraint(name = "uq_review_user_course", columnNames = {"user_id", "course_id"}))
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @Column(name = "user_id", nullable = false)
    private int userId;

    private int rating;

    @Column(length = 5000)
    private String comment;

    public Review() {
    }

    public Review(int id, int userId, int courseId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.courseId = courseId;
        this.rating = rating;
        this.comment = comment;
    }
}
