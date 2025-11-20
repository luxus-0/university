package com.company.university.lecture.service;

import com.company.university.lecture.application.LectureMapper;
import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private LectureService lectureService;

    private Lecture lecture;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        lecture = Lecture.builder()
                .id(1L)
                .title("Math Lecture")
                .roomNumber("101A")
                .startDateTime(LocalDateTime.of(2025, 1, 10, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 1, 10, 12, 0))
                .build();
    }

    @Test
    void shouldReturnAllLectures() {
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        List<FindLectureResponse> result = lectureService.getLectures();

        assertEquals(1, result.size());
        assertEquals("Math Lecture", result.get(0).getTitle());
        verify(lectureRepository).findAll();
    }

    @Test
    void shouldReturnLectureById() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        FindLectureResponse response = lectureService.getLecture(1L);

        assertEquals(1L, response.getId());
        verify(lectureRepository).findById(1L);
    }

    @Test
    void shouldThrowWhenLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LectureNotFoundException.class,
                () -> lectureService.getLecture(1L));
    }

    @Test
    void shouldReturnLecturesByDate() {
        LocalDate date = LocalDate.of(2025, 1, 10);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        when(lectureRepository.findByStartDateTimeBetween(start, end))
                .thenReturn(List.of(lecture));

        List<FindLectureResponse> result = lectureService.getLectures(date);

        assertEquals(1, result.size());
        verify(lectureRepository).findByStartDateTimeBetween(start, end);
    }

    @Test
    void shouldReturnPagedLectures() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        Page<Lecture> page = new PageImpl<>(List.of(lecture));
        when(lectureRepository.findAll(pageable)).thenReturn(page);

        Page<FindLectureResponse> result = lectureService.getLectures(0, 10, "id", "asc");

        assertEquals(1, result.getTotalElements());
        verify(lectureRepository).findAll(pageable);
    }

    @Test
    void shouldReturnLecturesBetweenDatesWithPageable() {
        Pageable pageable = PageRequest.of(0, 10);

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 20);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        Page<Lecture> page = new PageImpl<>(List.of(lecture));

        when(lectureRepository.findByStartDateTimeBetween(start, end, pageable))
                .thenReturn(page);

        Page<FindLectureResponse> result =
                lectureService.getLectures(pageable, startDate, endDate);

        assertEquals(1, result.getTotalElements());
        verify(lectureRepository).findByStartDateTimeBetween(start, end, pageable);
    }

    @Test
    void shouldReturnAllLecturesWhenDatesAreNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Lecture> page = new PageImpl<>(List.of(lecture));
        when(lectureRepository.findAll(pageable)).thenReturn(page);

        Page<FindLectureResponse> result =
                lectureService.getLectures(pageable, null, null);

        assertEquals(1, result.getTotalElements());
        verify(lectureRepository).findAll(pageable);
    }

    @Test
    void shouldCreateLecture() {
        CreateLectureRequest request = CreateLectureRequest.builder()
                .title("Math Lecture")
                .roomNumber("101A")
                .startDateTime(LocalDateTime.of(2025, 1, 10, 10, 0))
                .endDateTime(LocalDateTime.of(2025, 1, 10, 12, 0))
                .build();

        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);

        CreateLectureResponse response = lectureService.createLecture(request);

        assertEquals(1L, response.getId());
        assertEquals("Math Lecture", response.getTitle());
        verify(lectureRepository).save(any(Lecture.class));
    }


    @Test
    void shouldUpdateLecture() {
        UpdateLectureRequest request = UpdateLectureRequest.builder()
                .title("Updated Title")
                .roomNumber("202B")
                .startDateTime(LocalDateTime.of(2025, 1, 15, 9, 0))
                .endDateTime(LocalDateTime.of(2025, 1, 15, 11, 0))
                .build();

        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);

        UpdateLectureResponse response = lectureService.updateLecture(1L, request);

        assertEquals(1L, response.getId());
        assertEquals("Updated Title", lecture.getTitle());
        verify(lectureRepository).findById(1L);
        verify(lectureRepository).save(lecture);
    }

    @Test
    void shouldThrowWhenUpdatingNonExistingLecture() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LectureNotFoundException.class,
                () -> lectureService.updateLecture(1L, UpdateLectureRequest.builder().build()));
    }

    @Test
    void shouldDeleteLecture() {
        doNothing().when(lectureRepository).deleteById(1L);

        lectureService.deleteLecture(1L);

        verify(lectureRepository).deleteById(1L);
    }
}