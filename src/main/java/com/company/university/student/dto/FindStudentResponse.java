package com.company.university.student.dto;

import com.company.university.student.domain.StudentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class FindStudentResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private StudentStatus status;
    private LocalDate dateOfBirth;
}