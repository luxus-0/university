package com.company.university.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FindLecturerResponse {
    @NotBlank
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    private LocalDate dateOfBirth;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private LocalDate getDateOfBirth;
}
