package com.company.university.web.api;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.dto.*;
import com.company.university.lecturer.service.LecturerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;

    @GetMapping
    public ResponseEntity<Set<FindLecturerResponse>> getLecturers() {
        Set<FindLecturerResponse> lecturers = lecturerService.getLecturers();
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FindLecturerResponse>> getLecturersPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                lecturerService.getLecturers(page, size, sortBy, direction)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindLecturerResponse> getLecturer(@PathVariable Long id) {
        FindLecturerResponse lecturer = lecturerService.getLecturer(id);
        return ResponseEntity.ok(lecturer);
    }

    @PostMapping
    public ResponseEntity<CreateLecturerResponse> createLecturer(@RequestBody CreateLecturerRequest lecturer) {
        CreateLecturerResponse created = lecturerService.createLecturer(lecturer);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/lectures")
    public ResponseEntity<Void> addLectureToLecturer(@PathVariable Long id, @RequestBody Lecture lecture) {
        lecturerService.addLectureToLecturer(id, lecture);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateLecturerResponse> updateLecturer(@PathVariable Long id,
                                                                 @RequestBody UpdateLecturerRequest updatedLecturer) {
        UpdateLecturerResponse lecturer = lecturerService.updateLecturer(id, updatedLecturer);
        return ResponseEntity.ok(lecturer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/lectures")
    public ResponseEntity<Void> removeLectureFromLecturer(@PathVariable Long id, @RequestBody Lecture lecture) {
        lecturerService.removeLectureFromLecturer(id, lecture);
        return ResponseEntity.ok().build();
    }
}