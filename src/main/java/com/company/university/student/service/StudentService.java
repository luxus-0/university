package com.company.university.student.service;

import com.company.university.student.domain.StudentStatus;
import com.company.university.student.dto.*;
import com.company.university.student.application.StudentMapper;
import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static com.company.university.student.application.StudentMapper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    public CreateStudentResponse createStudent(CreateStudentRequest request) {
        Student student = createStudentWithStatusActive(request);
        Student studentSaved = studentRepository.save(student);
        return createStudentResponse(studentSaved);
    }

    public Set<FindStudentResponse> getStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::findStudentResponse)
                .collect(Collectors.toSet());
    }

    public FindStudentResponse getStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        return StudentMapper.findStudentResponse(student);
    }

    public Page<FindStudentResponse> getStudents(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.findAll(pageable)
                .map(StudentMapper::findStudentResponse);
    }

    @Transactional
    public UpdateStudentResponse updateStudent(Long id, UpdateStudentRequest request) {

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        Student saved = studentRepository.save(student);

        return updateStudentResponse(saved, student);
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}