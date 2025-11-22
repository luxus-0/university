package com.company.university.lecture.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateLectureResponse {
    private Long id;
    private String title;
    private String description;
    private String roomNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long lecturerId;
}
