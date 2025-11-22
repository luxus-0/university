package com.company.university.lecture.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.CreateLectureRequest;
import com.company.university.lecture.dto.UpdateLectureRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LectureValidator {

    private final LectureRepository lectureRepository;

    public void validateCreate(CreateLectureRequest request) {
        validateTimeRange(request.getStartDateTime(), request.getEndDateTime());
        validateOverlap(null, request.getStartDateTime(), request.getEndDateTime(),
                request.getRoomNumber(), request.getLecturerId());
    }

    public void validateUpdate(Long lectureId, UpdateLectureRequest request) {
        validateTimeRange(request.getStartDateTime(), request.getEndDateTime());
        validateOverlap(lectureId, request.getStartDateTime(), request.getEndDateTime(), request.getRoomNumber(), request.getLecturerId());
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new BusinessValidationException("Lecture end time must be AFTER start time.");
        }
    }

    private void validateOverlap(Long id,
                                 LocalDateTime start,
                                 LocalDateTime end,
                                 String roomNumber,
                                 Long lecturerId) {

        List<Lecture> overlapping = lectureRepository.findOverlapping(id, start, end, roomNumber, lecturerId);

        if (!overlapping.isEmpty()) {
            throw new BusinessValidationException(
                    "Lecture overlaps another lecture (same time, room or lecturer)."
            );
        }
    }
}
