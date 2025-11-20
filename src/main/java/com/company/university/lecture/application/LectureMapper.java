package com.company.university.lecture.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.dto.CreateLectureRequest;
import com.company.university.lecture.dto.CreateLectureResponse;
import com.company.university.lecture.dto.FindLectureResponse;
import com.company.university.lecture.dto.UpdateLectureResponse;

public class LectureMapper {

    public static Lecture toLecture(CreateLectureRequest request) {
        return Lecture.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .build();
    }

    public static FindLectureResponse findLectureResponse(Lecture lecture) {
        return FindLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .build();
    }

    public static CreateLectureResponse createLectureResponse(Lecture lecture) {
        return CreateLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .build();
    }

    public static UpdateLectureResponse updateLectureResponse(Lecture lecture) {
        return UpdateLectureResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .roomNumber(lecture.getRoomNumber())
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .build();
    }
}