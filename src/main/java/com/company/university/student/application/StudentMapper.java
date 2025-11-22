package com.company.university.student.application;

import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentStatus;
import com.company.university.student.dto.*;

import java.util.UUID;

public class StudentMapper {

    public static Student toStudent(CreateStudentRequest studentRequest) {
        return Student.builder()
                .name(studentRequest.getName())
                .surname(studentRequest.getSurname())
                .email(studentRequest.getEmail())
                .status(StudentStatus.ACTIVE)
                .studentNumber(UUID.randomUUID().toString())
                .dateOfBirth(studentRequest.getDateOfBirth())
                .build();
    }

    public static void updateStudentFromRequest(Student student, UpdateStudentRequest request) {
        student.setName(request.getName());
        student.setSurname(request.getSurname());
        student.setEmail(request.getEmail());
        student.setStatus(request.getStatus());
    }

    public static CreateStudentResponse createStudentResponse(Student student) {
        return CreateStudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .email(student.getEmail())
                .status(student.getStatus())
                .build();
    }

    public static FindStudentResponse findStudentResponse(Student student) {
        return FindStudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .status(student.getStatus())
                .email(student.getEmail())
                .dateOfBirth(student.getDateOfBirth())
                .build();
    }

    public static UpdateStudentResponse updateStudentResponse(Student student) {
        return UpdateStudentResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .surname(student.getSurname())
                .status(student.getStatus())
                .email(student.getEmail())
                .createdAt(student.getCreatedAt())
                .build();
    }

    public static FindStudentWithLecturesResponse toFindStudentWithLecturesResponse(Student student) {

        return new FindStudentWithLecturesResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),

                student.getLectures().stream()
                        .map(lecture -> new FindStudentWithLecturesResponse.LectureDto(
                                lecture.getId(),
                                lecture.getTitle(),
                                lecture.getDescription(),
                                lecture.getRoomNumber()
                        ))
                        .toList()
        );
    }
}