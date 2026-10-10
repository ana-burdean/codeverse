package com.codeverse.platform.model;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class CourseRequest {
    @Min(value = 1, message = "adminUserId must be positive")
    private int adminUserId;

    @NotBlank(message = "title is required")

    private String title;
    @NotBlank(message = "description is required")
    private String description;
}