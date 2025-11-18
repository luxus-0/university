package com.company.university.web.api;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.application.LecturerDTO;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.service.LecturerService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Set<LecturerDTO>> getAllLecturers() {
        Set<LecturerDTO> lecturers = lecturerService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerDTO> getLecturer(@PathVariable Long id) {
        LecturerDTO lecturer = lecturerService.getLecturer(id);
        return ResponseEntity.ok(lecturer);
    }

    @PostMapping
    public ResponseEntity<LecturerDTO> createLecturer(@RequestBody Lecturer lecturer) {
        LecturerDTO created = lecturerService.createLecturer(lecturer);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LecturerDTO> updateLecturer(@PathVariable Long id,
                                                      @RequestBody Lecturer updatedLecturer) {
        LecturerDTO lecturerDTO = lecturerService.updateLecturer(id, updatedLecturer);
        return ResponseEntity.ok(lecturerDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecturer(@PathVariable Long id) {
        lecturerService.deleteLecturer(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/lectures")
    public ResponseEntity<Void> addLectureToLecturer(@PathVariable Long id, @RequestBody Lecture lecture) {
        lecturerService.addLectureToLecturer(id, lecture);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/lectures")
    public ResponseEntity<Void> removeLectureFromLecturer(@PathVariable Long id, @RequestBody Lecture lecture) {
        lecturerService.removeLectureFromLecturer(id, lecture);
        return ResponseEntity.ok().build();
    }
}