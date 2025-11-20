package com.company.university.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FindLecturerRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private LocalDate dateOfBirth;
    @Email
    @NotBlank
    private String email;
}
