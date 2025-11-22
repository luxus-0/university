package com.company.university.lecture.service;

import com.company.university.lecture.application.BusinessValidationException;
import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.application.LectureValidator;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.CreateLectureRequest;
import com.company.university.lecture.dto.FindLectureResponse;
import com.company.university.lecture.dto.UpdateLectureRequest;
import com.company.university.lecture.util.LectureTestUtils;
import com.company.university.lecturer.application.LecturerNotFoundException;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LectureServiceTest {

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureValidator lectureValidator;

    @Mock
    private LecturerRepository lecturerRepository;

    @InjectMocks
    private LectureService lectureService;

    private Lecture lecture;
    private Lecturer lecturer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        lecturer = Lecturer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .build();

        lecture = Lecture.builder()
                .id(1L)
                .title("Math")
                .description("Algebra")
                .roomNumber("101")
                .startDateTime(LocalDateTime.of(2025, 11, 21, 9, 0))
                .endDateTime(LocalDateTime.of(2025, 11, 21, 10, 0))
                .lecturer(lecturer)
                .build();
    }

    @Test
    void getLectures_ShouldReturnList() {
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        var result = lectureService.getLectures();

        assertEquals(1, result.size());
        assertEquals(lecture.getTitle(), result.get(0).getTitle());
    }

    @Test
    void getLecture_ShouldReturnLecture() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));

        var result = lectureService.getLecture(1L);

        assertEquals("Math", result.getTitle());
    }

    @Test
    void getLecture_ShouldThrowException_WhenNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LectureNotFoundException.class, () -> lectureService.getLecture(1L));
    }

    @Test
    void getLecturesWithPageableAndNullDates_ShouldReturnAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Lecture> page = new PageImpl<>(List.of(lecture));

        when(lectureRepository.findAll(pageable)).thenReturn(page);

        Page<FindLectureResponse> result = lectureService.getLectures(pageable, null, null);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getLecturesWithPageableAndStartDateOnly_ShouldReturnFiltered() {
        LocalDate startDate = LocalDate.of(2025, 11, 20);
        Pageable pageable = PageRequest.of(0, 10);

        when(lectureRepository.findByStartDateTimeBetween(
                eq(startDate.atStartOfDay()),
                any(LocalDateTime.class),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(lecture)));

        Page<FindLectureResponse> result = lectureService.getLectures(pageable, startDate, null);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getLecturesWithPageableAndEndDateOnly_ShouldReturnFiltered() {
        LocalDate endDate = LocalDate.of(2025, 11, 22);
        Pageable pageable = PageRequest.of(0, 10);

        when(lectureRepository.findByStartDateTimeBetween(
                any(LocalDateTime.class),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(lecture)));

        Page<FindLectureResponse> result = lectureService.getLectures(pageable, null, endDate);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getLecturesWithPageableAndBothDates_ShouldReturnFiltered() {
        LocalDate startDate = LocalDate.of(2025, 11, 20);
        LocalDate endDate = LocalDate.of(2025, 11, 22);
        Pageable pageable = PageRequest.of(0, 10);

        when(lectureRepository.findByStartDateTimeBetween(
                eq(startDate.atStartOfDay()),
                eq(endDate.atTime(LocalTime.MAX)),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(lecture)));

        Page<FindLectureResponse> result = lectureService.getLectures(pageable, startDate, endDate);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getLecturesByDate_ShouldReturnLectures() {
        LocalDate date = LocalDate.of(2025, 11, 21);
        when(lectureRepository.findByStartDateTimeBetween(
                any(LocalDateTime.class), any(LocalDateTime.class)
        )).thenReturn(List.of(lecture));

        var result = lectureService.getLectures(date);

        assertEquals(1, result.size());
    }

    @Test
    void createLecture_ShouldReturnLecture() {
        CreateLectureRequest request = CreateLectureRequest.builder()
                .title("Math")
                .description("Algebra")
                .roomNumber("101")
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .lecturerId(lecturer.getId())
                .build();

        when(lecturerRepository.findById(lecturer.getId())).thenReturn(Optional.of(lecturer));
        doNothing().when(lectureValidator).validateCreate(request);
        when(lectureRepository.save(any())).thenReturn(lecture);

        var response = lectureService.createLecture(request);

        assertEquals(lecture.getTitle(), response.getTitle());
        verify(lectureValidator).validateCreate(request);
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void createLecture_ShouldThrowLecturerNotFoundException() {
        CreateLectureRequest request = CreateLectureRequest.builder()
                .lecturerId(2L)
                .build();

        when(lecturerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(LecturerNotFoundException.class, () -> lectureService.createLecture(request));
    }

    @Test
    void createLecture_ShouldCallValidatorAndRepository() {
        CreateLectureRequest request = CreateLectureRequest.builder()
                .title("Math")
                .description("Algebra")
                .roomNumber("101")
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .lecturerId(lecturer.getId())
                .build();

        when(lecturerRepository.findById(lecturer.getId())).thenReturn(java.util.Optional.of(lecturer));
        doNothing().when(lectureValidator).validateCreate(request);
        when(lectureRepository.save(any(Lecture.class))).thenReturn(lecture);

        var response = lectureService.createLecture(request);

        assertEquals("Math", response.getTitle());
        verify(lectureValidator).validateCreate(request);
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void createLecture_ShouldThrowLecturerNotFound() {
        CreateLectureRequest request = CreateLectureRequest.builder().lecturerId(2L).build();
        when(lecturerRepository.findById(2L)).thenReturn(java.util.Optional.empty());

        assertThrows(LecturerNotFoundException.class, () -> lectureService.createLecture(request));
    }

    @Test
    void validateCreate_ShouldThrowException_WhenOverlapExists() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.of(2025, 11, 21, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 11, 21, 11, 30); // Prawidłowy zakres (end > start)

        CreateLectureRequest request = CreateLectureRequest.builder()
                .startDateTime(start)
                .endDateTime(end)
                .lecturerId(lecturer.getId())
                .title("Math").description("Algebra").roomNumber("203")
                .build();

        final String expectedMessage = "Lecture overlaps another lecture (same time, room or lecturer).";

        when(lecturerRepository.findById(request.getLecturerId()))
                .thenReturn(Optional.of(lecturer));

        doThrow(new BusinessValidationException(expectedMessage))
                .when(lectureValidator).validateCreate(eq(request));


        // ACT & ASSERT
        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> lectureService.createLecture(request));

        verify(lectureValidator, times(1)).validateCreate(eq(request));

        LectureTestUtils.verifyValidationFailureInteractions(
                lectureRepository, lecturerRepository, request);
        assertTrue(ex.getMessage().contains("Lecture overlaps another lecture"));
    }

    @Test
    void validateCreate_ShouldThrowException_WhenEndEqualStart() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.of(2025, 11, 21, 11, 30);
        LocalDateTime end = LocalDateTime.of(2025, 11, 21, 11, 30); // Błąd: end < start

        CreateLectureRequest request = CreateLectureRequest.builder()
                .startDateTime(start)
                .endDateTime(end)
                .lecturerId(lecturer.getId())
                .title("Math").description("Algebra").roomNumber("203")
                .build();

        final String expectedMessage = "Lecture end time must be AFTER start time.";

        when(lecturerRepository.findById(request.getLecturerId()))
                .thenReturn(Optional.of(lecturer));

        doThrow(new BusinessValidationException(expectedMessage))
                .when(lectureValidator).validateCreate(eq(request));


        // ACT & ASSERT
        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> lectureService.createLecture(request));

        verify(lectureValidator, times(1)).validateCreate(eq(request));

        LectureTestUtils.verifyValidationFailureInteractions(
                lectureRepository, lecturerRepository, request);

        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void validateCreate_ShouldThrowException_WhenEndBeforeStart() {
        // ARRANGE
        LocalDateTime start = LocalDateTime.of(2025, 11, 21, 10, 30);
        LocalDateTime end = LocalDateTime.of(2025, 11, 15, 11, 30); // Błąd: end < start

        CreateLectureRequest request = CreateLectureRequest.builder()
                .startDateTime(start)
                .endDateTime(end)
                .lecturerId(lecturer.getId())
                .title("Math").description("Algebra").roomNumber("203")
                .build();

        final String expectedMessage = "Lecture end time must be AFTER start time.";

        when(lecturerRepository.findById(request.getLecturerId()))
                .thenReturn(Optional.of(lecturer));

        doThrow(new BusinessValidationException(expectedMessage))
                .when(lectureValidator).validateCreate(eq(request));


        // ACT & ASSERT
        BusinessValidationException ex = assertThrows(BusinessValidationException.class,
                () -> lectureService.createLecture(request));

        verify(lectureValidator, times(1)).validateCreate(eq(request));

        LectureTestUtils.verifyValidationFailureInteractions(
                lectureRepository, lecturerRepository, request);

        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    @Test
    void updateLecture_ShouldReturnUpdatedLecture() {
        UpdateLectureRequest request = UpdateLectureRequest.builder()
                .title("Math Updated")
                .description("Algebra Advanced")
                .roomNumber("101")
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .lecturerId(lecturer.getId())
                .build();

        when(lecturerRepository.findById(lecturer.getId())).thenReturn(Optional.of(lecturer));
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        doNothing().when(lectureValidator).validateUpdate(1L, request);
        when(lectureRepository.save(any())).thenReturn(lecture);

        var response = lectureService.updateLecture(1L, request);

        assertEquals("Math Updated", response.getTitle()); // LectureMapper używa danych z encji
        verify(lectureValidator).validateUpdate(1L, request);
        verify(lectureRepository).save(any(Lecture.class));
    }

    @Test
    void updateLecture_ShouldThrowLectureNotFound() {
        UpdateLectureRequest request = UpdateLectureRequest.builder()
                .lecturerId(lecturer.getId())
                .build();
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());
        when(lecturerRepository.findById(lecturer.getId())).thenReturn(Optional.of(lecturer));

        assertThrows(LectureNotFoundException.class, () -> lectureService.updateLecture(1L, request));
    }

    @Test
    void updateLecture_ShouldThrowLecturerNotFound() {
        UpdateLectureRequest request = UpdateLectureRequest.builder()
                .lecturerId(2L)
                .build();
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
        when(lecturerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(LecturerNotFoundException.class, () -> lectureService.updateLecture(1L, request));
    }

    @Test
    void updateLecture_ShouldCallValidatorAndRepository() {
        UpdateLectureRequest request = UpdateLectureRequest.builder()
                .title("Math Updated")
                .description("Algebra Advanced")
                .roomNumber("101")
                .startDateTime(lecture.getStartDateTime())
                .endDateTime(lecture.getEndDateTime())
                .lecturerId(lecturer.getId())
                .build();

        when(lecturerRepository.findById(lecturer.getId())).thenReturn(java.util.Optional.of(lecturer));
        when(lectureRepository.findById(1L)).thenReturn(java.util.Optional.of(lecture));
        doNothing().when(lectureValidator).validateUpdate(1L, request);
        when(lectureRepository.save(any())).thenReturn(lecture);

        var response = lectureService.updateLecture(1L, request);

        assertEquals("Math Updated", response.getTitle());
        verify(lectureValidator).validateUpdate(1L, request);
        verify(lectureRepository).save(any(Lecture.class));
    }


    @Test
    void deleteLecture_ShouldCallRepository() {
        doNothing().when(lectureRepository).deleteById(1L);

        lectureService.deleteLecture(1L);

        verify(lectureRepository).deleteById(1L);
    }

    @Test
    void getLecturesWithPagination_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("title"));
        Page<Lecture> page = new PageImpl<>(List.of(lecture));
        when(lectureRepository.findAll(pageable)).thenReturn(page);

        Page<FindLectureResponse> result = lectureService.getLectures(0, 10, "title", "asc");

        assertEquals(1, result.getContent().size());
    }
}
