package com.company.university.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateLectureResponse {
    private Long id;
    private String title;
    private String roomNumber;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Long lecturerId;
}
