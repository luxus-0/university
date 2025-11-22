package com.company.university.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateLectureRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String roomNumber;
    @NotNull
    private LocalDateTime startDateTime;
    @NotNull
    private LocalDateTime endDateTime;
    @NotNull
    private Long lecturerId;
}
