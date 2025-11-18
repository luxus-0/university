package com.company.university.lecture.application;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class LectureDTO {
    @NotBlank
    private String title;
    @Size(max = 1000)
    private String description;
    @NotNull
    @Future
    private LocalDateTime startTime;
    @NotNull
    @Future
    private LocalDateTime endTime;
    @NotBlank
    private String roomNumber;
    @NotNull
    private Long lecturerId;
}
