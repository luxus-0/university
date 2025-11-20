package com.company.university.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UpdateLectureRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String roomNumber;
    @NotBlank
    private LocalDateTime startDateTime;
    @NotBlank
    private LocalDateTime endDateTime;
}
