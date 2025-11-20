package com.company.university.lecturer.service;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.application.LecturerMapper;
import com.company.university.lecturer.application.LecturerNotFoundException;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import com.company.university.lecturer.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LecturerServiceTest {

    @Mock
    private LecturerRepository lecturerRepository;

    @InjectMocks
    private LecturerService lecturerService;

    private Lecturer lecturer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        lecturer = Lecturer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1980, 1, 2))
                .lectures(new HashSet<>())
                .build();
    }

    @Test
    void shouldCreateLecturer() {
        CreateLecturerRequest request = CreateLecturerRequest.builder()
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1980, 1, 2))
                .build();

        Lecturer lecturer = Lecturer.builder()
                .id(1L)
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .lectures(new HashSet<>())
                .build();

        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        CreateLecturerResponse response = lecturerService.createLecturer(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(lecturerRepository, times(1)).save(any(Lecturer.class));
    }

    @Test
    void shouldThrowExceptionIfCreateLecturerWithId() {
        CreateLecturerRequest request = CreateLecturerRequest.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .build();

        assertThrows(LecturerNotFoundException.class, () -> lecturerService.createLecturer(request));
    }

    @Test
    void shouldGetLecturerById() {
        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        FindLecturerResponse response = lecturerService.getLecturer(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(lecturerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenLecturerNotFoundById() {
        when(lecturerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(LecturerNotFoundException.class, () -> lecturerService.getLecturer(1L));
        assertEquals("Lecturer not found with id: 1", exception.getMessage());
    }

    @Test
    void shouldGetAllLecturers() {
        when(lecturerRepository.findAll()).thenReturn(List.of(lecturer));

        Set<FindLecturerResponse> lecturers = lecturerService.getLecturers();

        assertEquals(1, lecturers.size());
        verify(lecturerRepository, times(1)).findAll();
    }

    @Test
    void shouldGetLecturersWithPagination() {
        Page<Lecturer> page = new PageImpl<>(List.of(lecturer));
        when(lecturerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<FindLecturerResponse> responsePage = lecturerService.getLecturers(0, 10, "name", "asc");

        assertEquals(1, responsePage.getContent().size());
        verify(lecturerRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldUpdateLecturer() {
        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));
        when(lecturerRepository.save(lecturer)).thenReturn(lecturer);

        UpdateLecturerResponse response = lecturerService.updateLecturer(1L, request);

        assertNotNull(response);
        verify(lecturerRepository, times(1)).findById(1L);
        verify(lecturerRepository, times(1)).save(lecturer);
    }

    @Test
    void shouldDeleteLecturer() {
        doNothing().when(lecturerRepository).deleteById(1L);

        lecturerService.deleteLecturer(1L);

        verify(lecturerRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldAddLectureToLecturer() {
        lecturer = Lecturer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .lectures(new HashSet<>())
                .build();

        Lecture lecture = Lecture.builder()
                .id(1L)
                .title("Math")
                .build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        lecturerService.addLectureToLecturer(1L, lecture);

        assertTrue(lecturer.getLectures().contains(lecture));
        verify(lecturerRepository, times(1)).findById(1L);
    }

    @Test
    void shouldRemoveLectureFromLecturer() {
        Lecture lecture = Lecture.builder().id(1L).title("Math").build();

        lecturer = Lecturer.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .lectures(new HashSet<>())
                .build();

        lecturer.addLecture(lecture);

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        lecturerService.removeLectureFromLecturer(1L, lecture);

        assertFalse(lecturer.getLectures().contains(lecture));
        verify(lecturerRepository, times(1)).findById(1L);
    }
}