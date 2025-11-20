package com.company.university.student.application;

import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentStatus;
import com.company.university.student.dto.*;

public class StudentMapper {


    public static CreateStudentResponse createStudentResponse(Student studentSaved) {
        return CreateStudentResponse.builder()
                .name(studentSaved.getName())
                .surname(studentSaved.getSurname())
                .email(studentSaved.getEmail())
                .status(studentSaved.getStatus())
                .build();
    }

    public static Student createStudentWithStatusActive(CreateStudentRequest request) {
        return Student.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .status(StudentStatus.ACTIVE)
                .build();
    }

    public static FindStudentResponse findStudentResponse(Student student) {
        return FindStudentResponse.builder()
                .name(student.getName())
                .surname(student.getSurname())
                .status(student.getStatus())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .build();
    }

    public static UpdateStudentResponse updateStudentResponse(Student saved, Student student) {
        return UpdateStudentResponse.builder()
                .name(saved.getName())
                .surname(saved.getSurname())
                .status(String.valueOf(saved.getStatus()))
                .email(saved.getEmail())
                .createdAt(student.getCreatedAt())
                .build();
    }
}
