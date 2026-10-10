package com.codeverse.platform.model;

import lombok.Data;

import jakarta.validation.constraints.*;

@Data
public class EnrollmentRequest {
    @Min(value = 1, message = "userId must be positive")
    private int userId;
    @Min(value = 1, message = "courseId must be positive")
    private int courseId;
}