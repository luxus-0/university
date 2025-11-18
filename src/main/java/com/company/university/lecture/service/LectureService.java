package com.company.university.lecture.service;

import com.company.university.lecture.application.LectureDTO;
import com.company.university.lecture.application.LectureMapper;
import com.company.university.lecture.application.LectureValidator;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {

    private final LectureRepository lectureRepository;
    private final LecturerRepository lecturerRepository;
    private final LectureMapper lectureMapper;
    private final LectureValidator lectureValidator;

    public List<LectureDTO> getLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(lectureMapper::toDto)
                .toList();
    }

    public LectureDTO getLecture(Long id) {
        Lecture lecture = findLectureById(id);
        return lectureMapper.toDto(lecture);
    }

    public List<LectureDTO> getLecturesByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return lectureRepository.findByStartTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(lectureMapper::toDto)
                .toList();
    }

    public LectureDTO createLecture(Lecture lecture) {
        Lecturer lecturer = findLecturerById(lecture.getLecturer().getId());

        lectureValidator.validateOnCreate(lecture);

        lecturer.addLecture(lecture);

        return lectureMapper.toDto(lectureRepository.save(lecture));
    }

    public LectureDTO updateLecture(Long lectureId, Lecture updatedLecture) {
        Lecture lecture = findLectureById(lectureId);
        Lecturer newLecturer = findLecturerById(updatedLecture.getLecturer().getId());

        lectureValidator.validateOnUpdate(lectureId, updatedLecture);

        updateLecturerIfChanged(lecture, newLecturer);

        lectureMapper.updateLecture(lecture, updatedLecture);

        return lectureMapper.toDto(lectureRepository.save(lecture));
    }

    public void deleteLecture(Long id) {
        lectureRepository.deleteById(id);
    }

    private Lecture findLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found with id: " + id));
    }

    private Lecturer findLecturerById(Long id) {
        return lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));
    }

    private void updateLecturerIfChanged(Lecture lecture, Lecturer newLecturer) {
        Lecturer oldLecturer = lecture.getLecturer();
        if (oldLecturer != null && !oldLecturer.getId().equals(newLecturer.getId())) {
            oldLecturer.removeLecture(lecture);
            newLecturer.addLecture(lecture);
        } else if (oldLecturer == null) {
            newLecturer.addLecture(lecture);
        }
    }
}