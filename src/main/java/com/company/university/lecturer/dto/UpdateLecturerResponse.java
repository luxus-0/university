package com.company.university.lecturer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateLecturerResponse {
    private String name;
    private String surname;
    private String email;
}
