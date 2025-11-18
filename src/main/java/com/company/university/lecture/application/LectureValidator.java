package com.company.university.lecture.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureValidator {

    private final LectureRepository lectureRepository;

    public void validateOnCreate(Lecture lecture) {
        validateTimes(lecture);
        validateRoomAvailability(lecture, null);
        validateLecturerAvailability(lecture, null);
    }

    public void validateOnUpdate(Long lectureId, Lecture lecture) {
        validateTimes(lecture);
        validateRoomAvailability(lecture, lectureId);
        validateLecturerAvailability(lecture, lectureId);
    }

    private void validateTimes(Lecture lecture) {
        if (lecture.getEndTime().isBefore(lecture.getStartTime())) {
            throw new IllegalArgumentException("End time cannot be before start time");
        }

        if (lecture.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start time must be in the future");
        }
    }

    private void validateLecturerAvailability(Lecture lecture, Long ignoreLectureId) {
        List<Lecture> lectures = lectureRepository.findByLecturerId(lecture.getLecturer().getId());

        for (Lecture existing : lectures) {
            if (existing.getId().equals(ignoreLectureId)) continue;

            if (overlap(lecture, existing)) {
                throw new IllegalArgumentException("Lecturer has a conflicting lecture");
            }
        }
    }

    private void validateRoomAvailability(Lecture lecture, Long ignoreLectureId) {
        List<Lecture> lectures = lectureRepository.findByRoomNumber(lecture.getRoomNumber());

        for (Lecture existing : lectures) {
            if (existing.getId().equals(ignoreLectureId)) continue;

            if (overlap(lecture, existing)) {
                throw new IllegalArgumentException("Room is already booked");
            }
        }
    }

    private boolean overlap(Lecture a, Lecture b) {
        return a.getStartTime().isBefore(b.getEndTime())
                && b.getStartTime().isBefore(a.getEndTime());
    }
}