package com.codeverse.platform.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(length = 5000)
    private String description;

    // Stored in separate table (lessons). Service fills this field when returning a Course.
    @Transient
    private List<Lesson> lessons = new ArrayList<>();

    // Computed from reviews/enrollments. Service fills these fields.
    @Transient
    private double averageRating;

    @Transient
    private int studentCount;

    public Course() {
    }

    public Course(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
