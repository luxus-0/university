package com.company.university.student.dto;

import com.company.university.student.domain.StudentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateStudentResponse {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private StudentStatus status;
}