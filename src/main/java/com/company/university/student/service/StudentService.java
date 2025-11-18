package com.company.university.student.service;

import com.company.university.lecture.application.LectureException;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.student.application.StudentDTO;
import com.company.university.student.application.StudentException;
import com.company.university.student.application.StudentMapper;
import com.company.university.student.application.StudentValidator;
import com.company.university.student.domain.Student;
import com.company.university.student.domain.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final LectureRepository lectureRepository;
    private final StudentMapper studentMapper;
    private final StudentValidator studentValidator;

    public StudentDTO createStudent(Student student) {
        if (student.getId() != null) {
            throw new StudentException("New student cannot have an ID");
        }

        student.setCreatedAt(LocalDateTime.now());
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    public StudentDTO getStudent(Long id) {
        Student student = findStudentById(id);
        return studentMapper.toDto(student);
    }

    public Set<StudentDTO> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    public StudentDTO updateStudent(Long id, Student updatedStudent) {
        Student existing = findStudentById(id);

        studentMapper.updateStudent(existing, updatedStudent);

        Student saved = studentRepository.save(existing);
        return studentMapper.toDto(saved);
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    @Transactional
    public void addLectureToStudent(Long studentId, Long lectureId) {
        Student student = findStudentById(studentId);
        Lecture lecture = findLectureById(lectureId);

        student.addLecture(lecture);
        studentRepository.save(student);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
    }

    private Lecture findLectureById(Long id) {
        return lectureRepository.findById(id)
                .orElseThrow(() -> new LectureException("Lecture not found with id: " + id));
    }

    @Transactional
    public void removeLectureFromStudent(Long studentId, Long lectureId) {
        Student student = findStudentById(studentId);
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new LectureException("Lecture not found with id: " + lectureId));
        student.removeLecture(lecture);
        studentRepository.save(student);
    }

    @Transactional
    public void enrollToLecture(Long studentId, Long lectureId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        studentValidator.validateStudentAvailability(student, lecture);

        student.addLecture(lecture);

        studentRepository.save(student);
    }
}