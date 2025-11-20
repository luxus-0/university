package com.company.university.lecture.service;

import com.company.university.lecture.application.LectureMapper;
import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.*;
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
import java.util.Optional;

import static com.company.university.lecture.application.LectureMapper.toLecture;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureService {

    private final LectureRepository lectureRepository;

    public List<FindLectureResponse> getLectures() {
        return lectureRepository.findAll()
                .stream()
                .map(LectureMapper::findLectureResponse)
                .toList();
    }

    public FindLectureResponse getLecture(Long id) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new LectureNotFoundException("Lecture not found with id: " + id));
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

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return lectureRepository.findAll(pageable)
                .map(LectureMapper::findLectureResponse);
    }

    public Page<FindLectureResponse> getLectures(Pageable pageable, LocalDate startDate, LocalDate endDate) {

        if (startDate == null && endDate == null) {
            return lectureRepository.findAll(pageable)
                    .map(LectureMapper::findLectureResponse);
        }

        LocalDateTime start = (startDate != null)
                ? startDate.atStartOfDay()
                : LocalDate.MIN.atStartOfDay();

        LocalDateTime end = (endDate != null)
                ? endDate.atTime(LocalTime.MAX)
                : LocalDate.MAX.atTime(LocalTime.MAX);

        return lectureRepository.findByStartDateTimeBetween(start, end, pageable)
                .map(LectureMapper::findLectureResponse);
    }

    public CreateLectureResponse createLecture(CreateLectureRequest request) {
        Lecture lecture = toLecture(request);

        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureMapper.createLectureResponse(savedLecture);
    }

    public UpdateLectureResponse updateLecture(Long lectureId, UpdateLectureRequest request) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureNotFoundException("Lecture not found with id: " + lectureId));

        Optional.ofNullable(request.getTitle()).ifPresent(lecture::setTitle);
        Optional.ofNullable(request.getRoomNumber()).ifPresent(lecture::setRoomNumber);
        Optional.ofNullable(request.getStartDateTime()).ifPresent(lecture::setStartDateTime);
        Optional.ofNullable(request.getEndDateTime()).ifPresent(lecture::setEndDateTime);

        Lecture updatedLecture = lectureRepository.save(lecture);

        return LectureMapper.updateLectureResponse(updatedLecture);
    }

    public void deleteLecture(Long id) {
        lectureRepository.deleteById(id);
    }

}