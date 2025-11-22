package com.company.university.lecturer.service;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.application.LecturerNotFoundException;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import com.company.university.lecturer.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
    void createLecturer_ShouldThrowException_WhenIdIsProvidedInRequest() {
        CreateLecturerRequest invalidRequest = CreateLecturerRequest.builder()
                .id(10L)
                .name("Adam")
                .surname("Kowalski")
                .email("a.k@example.com")
                .build();

        assertThrows(LecturerNotFoundException.class, // Nazwa wyjątku jest używana do sygnalizacji błędu walidacji
                () -> lecturerService.createLecturer(invalidRequest));

        verify(lecturerRepository, never()).save(any(Lecturer.class));
    }

    @Test
    void shouldCreateLecturer_WithEmptyLectureSet() {
        // Arrange
        CreateLecturerRequest request = CreateLecturerRequest.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .build();

        Lecturer savedLecturer = Lecturer.builder()
                .id(2L)
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .dateOfBirth(request.getDateOfBirth())
                .lectures(new HashSet<>())
                .build();

        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(savedLecturer);

        // Act
        CreateLecturerResponse response = lecturerService.createLecturer(request);

        // Assert
        assertNotNull(response);

        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        verify(lecturerRepository).save(lecturerCaptor.capture());
        assertTrue(lecturerCaptor.getValue().getLectures().isEmpty());
        assertEquals(2L, response.getId());
    }

    @Test
    void getLecturers_ShouldUseAscendingSort_WhenDirectionIsAsc() {
        // Arrange
        Page<Lecturer> page = new PageImpl<>(List.of(lecturer));
        when(lecturerRepository.findAll(any(Pageable.class))).thenReturn(page);
        String sortBy = "name";
        String direction = "asc";

        // Act
        lecturerService.getLecturers(0, 10, sortBy, direction);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(lecturerRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertTrue(capturedPageable.getSort().isSorted());
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor(sortBy).getDirection());
    }

    @Test
    void getLecturers_ShouldDefaultToSortAsc_WhenDirectionIsInvalid() {
        // Arrange
        Page<Lecturer> page = new PageImpl<>(List.of(lecturer));
        when(lecturerRepository.findAll(any(Pageable.class))).thenReturn(page);
        String sortBy = "name";
        String direction = "INVALID_DIRECTION";

        // Act
        lecturerService.getLecturers(0, 10, sortBy, direction);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(lecturerRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor(sortBy).getDirection());
    }

    @Test
    void getLecturers_ShouldUseDescendingSort_WhenDirectionIsDesc() {
        // Arrange
        Page<Lecturer> page = new PageImpl<>(List.of(lecturer));
        when(lecturerRepository.findAll(any(Pageable.class))).thenReturn(page);
        String sortBy = "name";
        String direction = "DESC";

        // Act
        lecturerService.getLecturers(0, 10, sortBy, direction);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(lecturerRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.DESC, capturedPageable.getSort().getOrderFor(sortBy).getDirection());
    }

    @Test
    void getLecturers_ShouldHandleEmptyListSuccessfully() {
        // Arrange
        when(lecturerRepository.findAll()).thenReturn(List.of());

        // Act
        List<FindLecturerResponse> lecturers = lecturerService.getLecturers();

        // Assert
        assertNotNull(lecturers);
        assertTrue(lecturers.isEmpty());
        verify(lecturerRepository, times(1)).findAll();
    }

    @Test
    void getLecturersWithPagination_ShouldHandleDefaultAscendingSort() {
        // Arrange
        Page<Lecturer> page = new PageImpl<>(List.of(lecturer));
        when(lecturerRepository.findAll(any(Pageable.class))).thenReturn(page);
        String sortBy = "email";
        String direction = "aSc";

        // Act
        lecturerService.getLecturers(0, 5, sortBy, direction);

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(lecturerRepository).findAll(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertEquals(Sort.Direction.ASC, capturedPageable.getSort().getOrderFor(sortBy).getDirection());
        assertEquals(0, capturedPageable.getPageNumber());
        assertEquals(5, capturedPageable.getPageSize());
    }

    @Test
    void shouldThrowExceptionIfCreateLecturerWithId() {
        CreateLecturerRequest request = CreateLecturerRequest.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1986, 12, 1))
                .build();
        lecturerRepository.save(lecturer);

        assertThrows(LecturerNotFoundException.class, () -> lecturerService.createLecturer(request));
    }

    @Test
    void updateLecturer_ShouldThrowException_WhenLecturerIdNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .build();

        when(lecturerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(LecturerNotFoundException.class,
                () -> lecturerService.updateLecturer(nonExistentId, request));

        assertEquals("Lecturer not found with id: 99", exception.getMessage());
        verify(lecturerRepository, never()).save(any(Lecturer.class));
    }

    @Test
    void updateLecturer_ShouldApplyAllFieldsAndReturnUpdatedResponse() {
        // Arrange
        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .dateOfBirth(LocalDate.of(1990, 5, 5))
                .build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));
        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        // Act
        UpdateLecturerResponse response = lecturerService.updateLecturer(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals("Jane", response.getName());
        assertEquals("jane.smith@example.com", response.getEmail());

        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        verify(lecturerRepository).save(lecturerCaptor.capture());

        Lecturer capturedLecturer = lecturerCaptor.getValue();
        assertEquals("Jane", capturedLecturer.getName());
        assertEquals(LocalDate.of(1990, 5, 5), capturedLecturer.getDateOfBirth());
        verify(lecturerRepository, times(1)).findById(1L);
    }

    @Test
    void updateLecturer_ShouldUpdateOnlyProvidedFields() {
        // Arrange
        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .name("Jane")
                .surname("Smith")
                .email("jane.smith@example.com")
                .dateOfBirth(null)
                .build();

        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        // Act
        lecturerService.updateLecturer(1L, request);

        // Assert
        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        verify(lecturerRepository).save(lecturerCaptor.capture());

        Lecturer capturedLecturer = lecturerCaptor.getValue();

        assertEquals(LocalDate.of(1980, 1, 2), capturedLecturer.getDateOfBirth());
        assertEquals("Jane", capturedLecturer.getName());
        assertEquals("jane.smith@example.com", capturedLecturer.getEmail());
    }

    @Test
    void updateLecturer_ShouldOnlySave_IfFieldsAreDifferent() {
        // Arrange
        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .name(lecturer.getName())
                .surname(lecturer.getSurname())
                .email(lecturer.getEmail())
                .dateOfBirth(lecturer.getDateOfBirth())
                .build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));
        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        // Act
        lecturerService.updateLecturer(1L, request);
        verify(lecturerRepository, times(1)).save(lecturer);

       // Assert
        assertEquals("John", lecturer.getName());
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

        List<FindLecturerResponse> lecturers = lecturerService.getLecturers();

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
    void updateLecturer_ShouldAllowSettingFieldsToNull_IfRequestDesignAllowsIt() {
        // Arrange
        UpdateLecturerRequest request = UpdateLecturerRequest.builder()
                .email("new.email@example.com")
                .dateOfBirth(null)
                .build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));
        when(lecturerRepository.save(any(Lecturer.class))).thenReturn(lecturer);

        // Act
        lecturerService.updateLecturer(1L, request);

        // Assert
        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        verify(lecturerRepository).save(lecturerCaptor.capture());

        Lecturer capturedLecturer = lecturerCaptor.getValue();

        assertEquals("new.email@example.com", capturedLecturer.getEmail());
        assertEquals(LocalDate.of(1980, 1, 2), capturedLecturer.getDateOfBirth());
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
    void addLectureToLecturer_ShouldThrowException_WhenLecturerNotFound() {
        // Arrange
        Long nonExistentId = 5L;
        Lecture dummyLecture = Lecture.builder().id(2L).title("Dummy").build();
        when(lecturerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> lecturerService.addLectureToLecturer(nonExistentId, dummyLecture));

        verify(lecturerRepository, never()).save(any(Lecturer.class));
    }

    @Test
    void addLectureToLecturer_ShouldNotSave_IfLectureAlreadyExists() {
        // Arrange
        Lecture existingLecture = Lecture.builder().id(2L).title("Physics").build();
        lecturer.addLecture(existingLecture);

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        // Act
        lecturerService.addLectureToLecturer(1L, existingLecture);

        assertEquals(1, lecturer.getLectures().size());
        verify(lecturerRepository, times(1)).save(lecturer);
    }

    @Test
    void addLectureToLecturer_ShouldUpdateBidirectionalRelationship() {
        // Arrange
        Lecture newLecture = Lecture.builder().id(2L).title("Physics").build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        // Act
        lecturerService.addLectureToLecturer(1L, newLecture);

        // Assert
        assertTrue(lecturer.getLectures().contains(newLecture));
        assertNotNull(newLecture.getLecturer());
        assertEquals(lecturer.getId(), newLecture.getLecturer().getId());

        verify(lecturerRepository, times(1)).save(lecturer);
    }

    @Test
    void removeLectureFromLecturer_ShouldNotFail_IfLectureIsNotAssigned() {
        // Arrange
        Lecture nonAssignedLecture = Lecture.builder().id(99L).title("Non-existent").build();

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        // Act
        lecturerService.removeLectureFromLecturer(1L, nonAssignedLecture);

        // Assert
        assertTrue(lecturer.getLectures().isEmpty());
        verify(lecturerRepository, times(1)).save(lecturer);
        assertNull(nonAssignedLecture.getLecturer());
    }

    @Test
    void removeLectureFromLecturer_ShouldThrowException_WhenLecturerNotFound() {
        // Arrange
        Long nonExistentId = 5L;
        Lecture dummyLecture = Lecture.builder().id(2L).title("Dummy").build();
        when(lecturerRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> lecturerService.removeLectureFromLecturer(nonExistentId, dummyLecture));

        verify(lecturerRepository, never()).save(any(Lecturer.class));
    }

    @Test
    void removeLectureFromLecturer_ShouldBreakBidirectionalRelationship() {
        // Arrange
        Lecture existingLecture = Lecture.builder().id(3L).title("Chemistry").build();

        lecturer.addLecture(existingLecture);

        when(lecturerRepository.findById(1L)).thenReturn(Optional.of(lecturer));

        // Act
        lecturerService.removeLectureFromLecturer(1L, existingLecture);

        // Assert
        assertFalse(lecturer.getLectures().contains(existingLecture));
        assertNull(existingLecture.getLecturer());

        verify(lecturerRepository, times(1)).save(lecturer);
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