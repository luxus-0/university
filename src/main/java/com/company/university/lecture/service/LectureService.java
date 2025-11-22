package com.company.university.lecture.service;

import com.company.university.lecture.application.LectureMapper;
import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.application.LectureValidator;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.*;
import com.company.university.lecturer.application.LecturerNotFoundException;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final LectureValidator lectureValidator;
    private final LecturerRepository lecturerRepository;

    public List<FindLectureResponse> getLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(LectureMapper::findLectureResponse)
                .toList();
    }

    public FindLectureResponse getLecture(Long id) {
        Lecture lecture = findLectureById(id);
        return LectureMapper.findLectureResponse(lecture);
    }

    public List<FindLectureResponse> getLectures(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return lectureRepository.findByStartDateTimeBetween(startOfDay, endOfDay)
                .stream()
                .map(LectureMapper::findLectureResponse)
                .toList();
    }

    public Page<FindLectureResponse> getLectures(int page, int size, String sortBy, String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        return lectureRepository.findAll(pageable)
                .map(LectureMapper::findLectureResponse);
    }

    public Page<FindLectureResponse> getLectures(Pageable pageable, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return lectureRepository.findAll(pageable)
                    .map(LectureMapper::findLectureResponse);
        }

        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime end = endDate != null ? endDate.atTime(LocalTime.MAX) : LocalDate.MAX.atTime(LocalTime.MAX);

        return lectureRepository.findByStartDateTimeBetween(start, end, pageable)
                .map(LectureMapper::findLectureResponse);
    }

    public CreateLectureResponse createLecture(CreateLectureRequest request) {
        Lecturer lecturer = findLecturerById(request.getLecturerId());

        lectureValidator.validateCreate(request);

        Lecture lecture = LectureMapper.toLecture(request, lecturer);
        Lecture saved = lectureRepository.save(lecture);

        return LectureMapper.createLectureResponse(saved);
    }

    public UpdateLectureResponse updateLecture(Long id, UpdateLectureRequest request) {
        Lecturer lecturer = findLecturerById(request.getLecturerId());
        Lecture lecture = findLectureById(id);

        lectureValidator.validateUpdate(id, request);

        LectureMapper.updateLecture(lecture, request, lecturer);
        Lecture saved = lectureRepository.save(lecture);

        return LectureMapper.updateLectureResponse(saved);
    }

    public void deleteLecture(Long id) {
        lectureRepository.deleteById(id);
    }

    private Lecture findLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new LectureNotFoundException("Lecture not found with id: " + id));
    }

    private Lecturer findLecturerById(Long id) {
        return lecturerRepository.findById(id)
                .orElseThrow(() -> new LecturerNotFoundException("Lecturer not found: " + id));
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, size, sort);
    }
}