package com.codeverse.platform.model;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class LessonRequest {
    @Min(value = 1, message = "adminUserId must be positive")
    private int adminUserId;
    @Min(value = 1, message = "courseId must be positive")
    private int courseId;
    @NotBlank(message = "title is required")
    private String title;
    @NotBlank(message = "content is required")
    private String content;
}