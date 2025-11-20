package com.company.university.student.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UpdateStudentResponse {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private String email;
    @NotBlank
    private String status;
    @NotBlank
    private LocalDateTime createdAt;
}
