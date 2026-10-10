package com.codeverse.platform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    private String title;

    @Column(length = 10000)
    private String content;

    public Lesson() {
    }

    public Lesson(int id, int courseId, String title, String content) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.content = content;
    }
}
