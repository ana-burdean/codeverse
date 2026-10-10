package com.codeverse.platform.model;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class ReviewRequest {
    @Min(value = 1, message = "userId must be positive")
    private int userId;
    @Min(value = 1, message = "courseId must be positive")
    private int courseId;
    @Min(value = 1, message = "rating must be 1..5")
    @Max(value = 5, message = "rating must be 1..5")
    private int rating;
    @NotBlank(message = "comment is required")
    @Size(max = 500, message = "comment too long")
    private String comment;
}