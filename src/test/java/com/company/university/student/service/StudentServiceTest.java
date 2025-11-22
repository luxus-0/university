package com.company.university.student.service;

import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.student.application.*;
import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentRepository;
import com.company.university.student.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private StudentValidator validator;

    @InjectMocks
    private StudentService studentService;

    private Student student;
    private Lecture lecture;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1L);
        student.setName("John Doe");
        student.setLectures(new HashSet<>());

        lecture = new Lecture();
        lecture.setId(1L);
        lecture.setStartDateTime(LocalDateTime.now().plusDays(1));
        lecture.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(1));
    }

    private void mockStudentFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
    }

    private void mockLectureFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.of(lecture));
    }

    @Test
    void createStudent_ShouldReturnCreatedStudent() {
        CreateStudentRequest request = CreateStudentRequest.builder().build();
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        CreateStudentResponse response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals(student.getId(), response.getId());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void createStudent_ShouldThrowException_WhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudent(null));
    }

    @Test
    void createStudent_ShouldThrowException_WhenRequestHasInvalidFields() {
        assertThrows(IllegalArgumentException.class, () -> studentService.createStudent(null));
    }

    @Test
    void createStudent_ShouldPropagateRepositoryException() {
        CreateStudentRequest request = CreateStudentRequest.builder().build();
        when(studentRepository.save(any(Student.class)))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> studentService.createStudent(request));
    }


    @Test
    void getStudents_ShouldReturnAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        List<FindStudentResponse> students = studentService.getStudents();

        assertEquals(1, students.size());
        assertEquals(student.getId(), students.getFirst().getId());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudent_ShouldReturnStudent_WhenExists() {
        mockStudentFound();

        FindStudentResponse response = studentService.getStudent(1L);

        assertNotNull(response);
        assertEquals(student.getId(), response.getId());
    }

    @Test
    void getStudent_ShouldThrowException_WhenNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentNotFoundException.class, () -> studentService.getStudent(1L));
    }

    @Test
    void updateStudent_ShouldUpdateAndReturnStudent() {
        UpdateStudentRequest request = UpdateStudentRequest.builder().name("Jane Doe").email("jane@example.com").build();
        mockStudentFound();
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        UpdateStudentResponse response = studentService.updateStudent(1L, request);

        assertNotNull(response);
        assertEquals("Jane Doe", student.getName());
        assertEquals("jane@example.com", student.getEmail());
        verify(studentRepository).save(student);
    }

    @Test
    void updateStudent_ShouldThrowException_WhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());
        UpdateStudentRequest request = UpdateStudentRequest.builder().build();

        assertThrows(StudentNotFoundException.class, () -> studentService.updateStudent(1L, request));
    }

    @Test
    void updateStudent_ShouldThrowException_WhenRequestIsNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.updateStudent(1L, null));
    }

    @Test
    void updateStudent_ShouldThrowException_WhenStudentIdIsNull() {
        UpdateStudentRequest request = UpdateStudentRequest.builder().build();

        assertThrows(IllegalArgumentException.class, () -> studentService.updateStudent(null, request));
    }

    @Test
    void deleteStudent_ShouldDelete_WhenExists() {
        when(studentRepository.existsById(1L)).thenReturn(true);

        studentService.deleteStudent(1L);

        verify(studentRepository).deleteById(1L);
    }

    @Test
    void deleteStudent_ShouldThrowException_WhenNotExists() {
        when(studentRepository.existsById(1L)).thenReturn(false);

        assertThrows(StudentNotFoundException.class, () -> studentService.deleteStudent(1L));
    }

    @Test
    void deleteStudent_ShouldPropagateRepositoryException() {
        when(studentRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("DB error")).when(studentRepository).deleteById(1L);

        assertThrows(RuntimeException.class, () -> studentService.deleteStudent(1L));
    }

    @Test
    void addLectureToStudent_ShouldAddLecture_WhenNoConflict() {
        mockStudentFound();
        mockLectureFound();

        studentService.addLectureToStudent(1L, 1L);

        assertTrue(student.getLectures().contains(lecture));
        verify(studentRepository).save(student);
    }

    @Test
    void addLectureToStudent_ShouldThrowConflictException() {
        mockStudentFound();
        mockLectureFound();

        doThrow(new StudentScheduleConflictException("Student has a schedule conflict!"))
                .when(validator).validateScheduleConflict(1L, lecture);

        assertThrows(StudentScheduleConflictException.class, () ->
                studentService.addLectureToStudent(1L, 1L)
        );
    }

    @Test
    void addLectureToStudent_ShouldThrowException_WhenStudentOrLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LectureNotFoundException.class, () -> studentService.addLectureToStudent(1L, 1L));
    }

    @Test
    void addLectureToStudent_ShouldThrowException_WhenIdsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> studentService.addLectureToStudent(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> studentService.addLectureToStudent(1L, null));
        assertThrows(IllegalArgumentException.class, () -> studentService.addLectureToStudent(null, null));
    }

    @Test
    void addLectureToStudent_ShouldNotDuplicateLecture_WhenLectureAlreadyAdded() {
        mockStudentFound();
        mockLectureFound();

        student.addLecture(lecture);

        studentService.addLectureToStudent(1L, 1L);

        assertEquals(1, student.getLectures().size());
        verify(studentRepository).save(student);
    }

    @Test
    void removeLectureFromStudent_ShouldRemoveLecture_WhenExists() {
        student.addLecture(lecture);
        mockStudentFound();
        mockLectureFound();

        studentService.removeLectureFromStudent(1L, 1L);

        assertFalse(student.getLectures().contains(lecture));
        verify(studentRepository).save(student);
    }

    @Test
    void removeLectureFromStudent_ShouldThrowException_WhenStudentOrLectureNotFound() {
        when(lectureRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LectureNotFoundException.class, () -> studentService.removeLectureFromStudent(1L, 1L));
    }

    @Test
    void removeLectureFromStudent_ShouldThrowException_WhenIdsAreNull() {
        assertThrows(LectureNotFoundException.class, () -> studentService.removeLectureFromStudent(null, 1L));
        assertThrows(StudentNotFoundException.class, () -> studentService.removeLectureFromStudent(1L, null));
    }

    @Test
    void removeLectureFromStudent_ShouldHandleNonExistentLectureGracefully() {
        mockStudentFound();
        mockLectureFound();

        studentService.removeLectureFromStudent(1L, 1L);

        assertTrue(student.getLectures().isEmpty());
        verify(studentRepository).save(student);
    }

    @Test
    void findByIdWithLectures_ShouldReturnStudentWithLectures_WhenExists() {
        student.addLecture(lecture);
        when(studentRepository.findByIdWithLectures(1L)).thenReturn(Optional.of(student));

        FindStudentWithLecturesResponse response = studentService.findByIdWithLectures(1L);

        assertNotNull(response);
        assertEquals(1, response.lectures().size());
    }

    @Test
    void findByIdWithLectures_ShouldThrowException_WhenNotFound() {
        when(studentRepository.findByIdWithLectures(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> studentService.findByIdWithLectures(1L));
    }

    @Test
    void findAllWithLectures_ShouldReturnList() {
        student.addLecture(lecture);
        when(studentRepository.findAllWithLectures()).thenReturn(List.of(student));

        List<FindStudentWithLecturesResponse> list = studentService.findAllWithLectures();

        assertEquals(1, list.size());
        assertEquals(1, list.getFirst().lectures().size());
    }

    @Test
    void findAllWithLectures_ShouldReturnEmptyList_WhenNoStudents() {
        when(studentRepository.findAllWithLectures()).thenReturn(List.of());

        List<FindStudentWithLecturesResponse> list = studentService.findAllWithLectures();
        assertTrue(list.isEmpty());
    }

    @Test
    void findAllWithLectures_WithPagination_ShouldReturnPage() {
        student.addLecture(lecture);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.findAllWithLectures(pageable)).thenReturn(page);

        Page<FindStudentWithLecturesResponse> result = studentService.findAllWithLectures(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().getFirst().lectures().size());
    }

    @Test
    void findAllWithLectures_ShouldReturnEmptyPage_WhenNoStudents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(studentRepository.findAllWithLectures(pageable)).thenReturn(Page.empty());

        Page<FindStudentWithLecturesResponse> result = studentService.findAllWithLectures(pageable);

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "sortBy={0}, direction={1}")
    @CsvSource({
            "id, asc",
            "id, desc",
            "name, asc",
            "name, desc",
            "surname, asc",
            "surname, desc"
    })
    void getStudents_ShouldReturnPage_WhenSortByExistentField(String sortBy, String direction) {
        when(studentRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(student)));

        Page<FindStudentResponse> result = studentService.getStudents(0, 10, sortBy, direction);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(studentRepository).findAll(captor.capture());
        Pageable captured = captor.getValue();
        assertEquals(sortBy, captured.getSort().iterator().next().getProperty());
        assertEquals(direction.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                captured.getSort().iterator().next().getDirection());
    }

    @Test
    void getStudents_ShouldThrowException_WhenSortByNonExistentField() {
        doThrow(new IncorrectFieldSortedByException("Cannot sort by incorrect field: nonExistentField"))
                .when(validator).validatePaginationAndSorting(anyInt(), anyInt(), eq("nonExistentField"), anyString());

        assertThrows(IncorrectFieldSortedByException.class, () ->
                studentService.getStudents(0, 10, "nonExistentField", "asc")
        );
    }

    @Test
    void getStudents_ShouldHandleInvalidPageAndSize() {
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudents(-1, 10, "name", "asc"));
        assertThrows(IllegalArgumentException.class, () -> studentService.getStudents(0, 0, "name", "asc"));
    }

    @Test
    void getStudents_ShouldThrowException_WhenSortDirectionInvalid() {
        doThrow(new IncorrectSortDirectionException("Invalid sort direction"))
                .when(validator).validatePaginationAndSorting(anyInt(), anyInt(), anyString(), eq("invalid"));

        assertThrows(IncorrectSortDirectionException.class, () ->
                studentService.getStudents(0, 10, "name", "invalid")
        );
    }

    @Test
    void studentMapper_ShouldHandleNullFields() {
        Student studentWithNulls = new Student();
        studentWithNulls.setId(1L);
        studentWithNulls.setName(null);
        studentWithNulls.setSurname(null);
        studentWithNulls.setEmail(null);

        FindStudentResponse response = StudentMapper.findStudentResponse(studentWithNulls);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertNull(response.getName());
        assertNull(response.getSurname());
        assertNull(response.getEmail());
    }
}
