package com.company.university.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateLecturerResponse {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @Email
    @NotBlank
    private String email;
}
