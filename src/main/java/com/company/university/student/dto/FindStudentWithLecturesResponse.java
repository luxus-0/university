package com.company.university.student.dto;

import java.util.List;

public record FindStudentWithLecturesResponse(
        Long id,
        String name,
        String email,
        List<LectureDto> lectures
) {

    public record LectureDto(
            Long id,
            String title,
            String description,
            String roomNumber
    ) { }
}
