package com.company.university.student.dto;

import com.company.university.student.domain.StudentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class UpdateStudentResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private StudentStatus status;;
    private LocalDateTime createdAt;
}
