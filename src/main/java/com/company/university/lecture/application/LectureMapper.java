package com.company.university.lecture.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.dto.*;
import com.company.university.lecturer.domain.Lecturer;

public class LectureMapper {

    public static Lecture toLecture(CreateLectureRequest request, Lecturer lecturer) {
        return Lecture.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .roomNumber(request.getRoomNumber())
                .lecturer(lecturer)
                .build();
    }

    public static void updateLecture(Lecture lecture,
                                     UpdateLectureRequest request,
                                     Lecturer lecturer) {

        lecture.setTitle(request.getTitle());
        lecture.setDescription(request.getDescription());
        lecture.setStartDateTime(request.getStartDateTime());
        lecture.setEndDateTime(request.getEndDateTime());
        lecture.setRoomNumber(request.getRoomNumber());
        lecture.setLecturer(lecturer);
    }

    public static FindLectureResponse findLectureResponse(Lecture lecture) {
        return FindLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .roomNumber(lecture.getRoomNumber())
                .lecturerId(lecture.getLecturer().getId())
                .build();
    }

    public static CreateLectureResponse createLectureResponse(Lecture lecture) {
        return CreateLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .roomNumber(lecture.getRoomNumber())
                .lecturerId(lecture.getLecturer().getId())
                .build();
    }

    public static UpdateLectureResponse updateLectureResponse(Lecture lecture) {
        return UpdateLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .roomNumber(lecture.getRoomNumber())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .lecturerId(lecture.getLecturer().getId())
                .build();
    }
}