package com.company.university.web.api;

import com.company.university.student.application.StudentDTO;
import com.company.university.student.domain.Student;
import com.company.university.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        StudentDTO student = studentService.getStudent(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    public ResponseEntity<Set<StudentDTO>> getAllStudents() {
        Set<StudentDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody Student student) {
        StudentDTO created = studentService.createStudent(student);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{studentId}/lectures/{lectureId}")
    public ResponseEntity<String> enrollToLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId) {
        studentService.enrollToLecture(studentId, lectureId);
        return ResponseEntity.ok("Student successfully enrolled in lecture");
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @RequestBody Student student) {

        StudentDTO updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/lectures/{lectureId}")
    public ResponseEntity<Void> addLectureToStudent(
            @PathVariable Long id,
            @PathVariable Long lectureId) {

        studentService.addLectureToStudent(id, lectureId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/lectures/{lectureId}")
    public ResponseEntity<Void> removeLectureFromStudent(
            @PathVariable Long id,
            @PathVariable Long lectureId) {

        studentService.removeLectureFromStudent(id, lectureId);
        return ResponseEntity.noContent().build();
    }
}