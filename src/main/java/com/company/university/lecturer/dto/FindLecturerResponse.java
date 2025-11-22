package com.company.university.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FindLecturerResponse {
    private Long id;
    private String name;
    private String surname;
    private LocalDate dateOfBirth;
    private String email;
}
