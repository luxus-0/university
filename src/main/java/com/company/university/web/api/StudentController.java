package com.company.university.web.api;

import com.company.university.student.dto.*;
import com.company.university.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateStudentResponse createStudent(@RequestBody CreateStudentRequest request) {
        return studentService.createStudent(request);
    }

    @GetMapping
    public List<FindStudentResponse> getStudents() {
        return studentService.getStudents();
    }

    @GetMapping("/paged")
    public Page<FindStudentResponse> getStudentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return studentService.getStudents(page, size, sortBy, direction);
    }

    @GetMapping("/{id}")
    public FindStudentResponse getStudent(@PathVariable Long id) {
        return studentService.getStudent(id);
    }

    // ------------------------------------------------------------
    // 5. GET STUDENT WITH LECTURES (JOIN FETCH)
    // ------------------------------------------------------------
    @GetMapping("/{id}/with-lectures")
    public FindStudentWithLecturesResponse findByIdWithLectures(@PathVariable Long id) {
        return studentService.findByIdWithLectures(id);
    }

    @GetMapping("/with-lectures")
    public List<FindStudentWithLecturesResponse> findAllWithLectures() {
        return studentService.findAllWithLectures();
    }

    @GetMapping("/with-lectures/paged")
    public Page<FindStudentWithLecturesResponse> findAllWithLecturesPaged(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return studentService.findAllWithLectures(pageable);
    }

    @PutMapping("/{id}")
    public UpdateStudentResponse updateStudent(
            @PathVariable Long id,
            @RequestBody UpdateStudentRequest request
    ) {
        return studentService.updateStudent(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }

    @PostMapping("/{studentId}/lectures/{lectureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLectureToStudent(
            @PathVariable Long studentId,
            @PathVariable Long lectureId
    ) {
        studentService.addLectureToStudent(studentId, lectureId);
    }

    @DeleteMapping("/{studentId}/lectures/{lectureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLectureFromStudent(
            @PathVariable Long studentId,
            @PathVariable Long lectureId
    ) {
        studentService.removeLectureFromStudent(lectureId, studentId);
    }
}