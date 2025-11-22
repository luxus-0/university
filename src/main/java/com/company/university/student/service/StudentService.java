package com.company.university.student.service;

import com.company.university.lecture.application.LectureNotFoundException;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.student.application.StudentMapper;
import com.company.university.student.application.StudentNotFoundException;
import com.company.university.student.application.StudentValidator;
import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentRepository;
import com.company.university.student.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final StudentValidator validator;

    public CreateStudentResponse createStudent(CreateStudentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        Student student = StudentMapper.toStudent(request);
        Student savedStudent = studentRepository.save(student);
        return StudentMapper.createStudentResponse(savedStudent);
    }

    public List<FindStudentResponse> getStudents() {
        return studentRepository.findAll()
                .stream()
                .map(StudentMapper::findStudentResponse)
                .toList();
    }

    public FindStudentResponse getStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        return StudentMapper.findStudentResponse(student);
    }

    public Page<FindStudentResponse> getStudents(int page, int size, String sortBy, String direction) {
        validator.validatePaginationAndSorting(page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return studentRepository.findAll(pageable)
                .map(StudentMapper::findStudentResponse);
    }

    public FindStudentWithLecturesResponse findByIdWithLectures(Long id) {
        Student student = studentRepository.findByIdWithLectures(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Student with id " + id + " not found"));

        return StudentMapper.toFindStudentWithLecturesResponse(student);
    }

    public List<FindStudentWithLecturesResponse> findAllWithLectures() {
        return studentRepository.findAllWithLectures()
                .stream()
                .map(StudentMapper::toFindStudentWithLecturesResponse)
                .toList();
    }

    public Page<FindStudentWithLecturesResponse> findAllWithLectures(Pageable pageable) {
        Page<Student> page = studentRepository.findAllWithLectures(pageable);

        return page.map(StudentMapper::toFindStudentWithLecturesResponse);
    }

    public UpdateStudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (id == null) {
            throw new IllegalArgumentException("Student id cannot be null");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        StudentMapper.updateStudentFromRequest(student, request);

        Student saved = studentRepository.save(student);
        return StudentMapper.updateStudentResponse(saved);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    public void addLectureToStudent(Long studentId, Long lectureId) {
        if (studentId == null || lectureId == null) {
            throw new IllegalArgumentException("Id student and id lecture are null");
        }

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureNotFoundException("Lecture not found with id: " + lectureId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + studentId));

        validator.validateScheduleConflict(studentId, lecture);
        student.addLecture(lecture);
        studentRepository.save(student);
    }

    public void removeLectureFromStudent(Long lectureId, Long studentId) {
        if (lectureId == null) {
            throw new LectureNotFoundException("Lecture id is null");
        }
        if (studentId == null) {
            throw new StudentNotFoundException("Student id is null");
        }

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureNotFoundException("Lecture not found with id: " + lectureId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + studentId));

        student.removeLecture(lecture);
        studentRepository.save(student);
    }
}