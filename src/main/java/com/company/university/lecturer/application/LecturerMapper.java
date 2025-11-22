package com.company.university.lecturer.application;

import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.dto.CreateLecturerRequest;
import com.company.university.lecturer.dto.CreateLecturerResponse;
import com.company.university.lecturer.dto.FindLecturerResponse;
import com.company.university.lecturer.dto.UpdateLecturerResponse;

public class LecturerMapper {
    public static Lecturer toLecturer(CreateLecturerRequest request) {
        return Lecturer.builder()
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .build();
    }

    public static CreateLecturerResponse createLecturerResponse(Lecturer lecturerSaved) {
        return CreateLecturerResponse.builder()
                .id(lecturerSaved.getId())
                .name(lecturerSaved.getName())
                .surname(lecturerSaved.getSurname())
                .email(lecturerSaved.getEmail())
                .build();
    }

    public static UpdateLecturerResponse updateLecturerResponse(Lecturer lecturerSaved) {
        return UpdateLecturerResponse.builder()
                .name(lecturerSaved.getName())
                .surname(lecturerSaved.getSurname())
                .email(lecturerSaved.getEmail())
                .build();
    }

    public static FindLecturerResponse findLecturerResponse(Lecturer lecturer) {
        return FindLecturerResponse.builder()
                .id(lecturer.getId())
                .name(lecturer.getName())
                .surname(lecturer.getSurname())
                .email(lecturer.getEmail())
                .dateOfBirth(lecturer.getDateOfBirth())
                .build();
    }
}
