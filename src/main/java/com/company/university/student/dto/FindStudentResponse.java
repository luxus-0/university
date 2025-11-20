package com.company.university.student.dto;

import com.company.university.student.domain.StudentStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FindStudentResponse {
    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private StudentStatus status;
    @NotBlank
    private LocalDate dateOfBirth;
}
