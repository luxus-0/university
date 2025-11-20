package com.company.university.student.service;

import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentRepository;
import com.company.university.student.domain.StudentStatus;
import com.company.university.student.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student student;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        student = Student.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("aaa@o2.pl")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .createdAt(LocalDate.now().atStartOfDay())
                .status(StudentStatus.ACTIVE)
                .build();
    }

    @Test
    void shouldCreateStudent() {
        CreateStudentRequest request = CreateStudentRequest.builder()
                .name("John")
                .surname("Doe")
                .email("lukas@o2.pl")
                .build();

        when(studentRepository.save(any(Student.class))).thenReturn(student);

        CreateStudentResponse response = studentService.createStudent(request);

        assertNotNull(response);
        assertEquals(student.getName(), response.getName());
        assertEquals(student.getSurname(), response.getSurname());
        assertEquals(student.getEmail(), response.getEmail());
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    void shouldGetAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(student));

        Set<FindStudentResponse> students = studentService.getStudents();

        assertEquals(1, students.size());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void shouldGetStudentById() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        FindStudentResponse response = studentService.getStudent(1L);

        assertNotNull(response);
        assertEquals(student.getName(), response.getName());
        assertEquals(student.getSurname(), response.getSurname());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenStudentNotFound() {
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> studentService.getStudent(1L));
        assertEquals("Student not found with id: 1", exception.getMessage());
    }

    @Test
    void shouldGetStudentsWithPagination() {
        Page<Student> page = new PageImpl<>(List.of(student));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<FindStudentResponse> responsePage = studentService.getStudents(0, 10, "firstName", "asc");

        assertEquals(1, responsePage.getContent().size());
        verify(studentRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void shouldUpdateStudent() {
        UpdateStudentRequest request = UpdateStudentRequest.builder()
                .name("Jane")
                .surname("Doe")
                .email("aaaa@o2.pl")
                .status(StudentStatus.ACTIVE.name())
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);

        UpdateStudentResponse response = studentService.updateStudent(1L, request);

        assertNotNull(response);
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(student);
    }

    @Test
    void shouldDeleteStudent() {
        doNothing().when(studentRepository).deleteById(1L);

        studentService.deleteStudent(1L);

        verify(studentRepository, times(1)).deleteById(1L);
    }
}