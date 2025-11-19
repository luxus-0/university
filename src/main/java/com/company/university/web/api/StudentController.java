package com.company.university.web.api;

import com.company.university.student.application.StudentDTO;
import com.company.university.student.domain.Student;
import com.company.university.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudent(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudent(id));
    }

    @GetMapping
    public ResponseEntity<Set<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@Valid @RequestBody Student student) {
        StudentDTO created = studentService.createStudent(student);
        return ResponseEntity
                .created(URI.create("/api/v1/students/" + created.getId()))
                .body(created);
    }

    @PostMapping("/{studentId}/lectures/{lectureId}")
    public ResponseEntity<Void> addLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId
    ) {
        studentService.addLectureToStudent(studentId, lectureId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{studentId}/lectures/{lectureId}/enroll")
    public ResponseEntity<Void> enrollToLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId
    ) {
        studentService.enrollToLecture(studentId, lectureId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student updatedStudent
    ) {
        return ResponseEntity.ok(studentService.updateStudent(id, updatedStudent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{studentId}/lectures/{lectureId}")
    public ResponseEntity<Void> removeLecture(
            @PathVariable Long studentId,
            @PathVariable Long lectureId
    ) {
        studentService.removeLectureFromStudent(studentId, lectureId);
        return ResponseEntity.noContent().build();
    }
}