package com.company.university.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateLectureResponse {
    @NotBlank
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String roomNumber;
    @NotBlank
    private LocalDateTime startDateTime;
    @NotBlank
    private LocalDateTime endDateTime;
}
