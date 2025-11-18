package com.company.university.web.api;

import com.company.university.lecture.application.LectureDTO;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/lectures")
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    @GetMapping
    public ResponseEntity<List<LectureDTO>> getAllLectures() {
        List<LectureDTO> lectures = lectureService.getLectures();
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureDTO> getLectureById(@PathVariable Long id) {
        LectureDTO lecture = lectureService.getLecture(id);
        return ResponseEntity.ok(lecture);
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<LectureDTO>> getLecturesByDate(@RequestParam("date") String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        List<LectureDTO> lectures = lectureService.getLecturesByDate(date);
        return ResponseEntity.ok(lectures);
    }

    @PostMapping
    public ResponseEntity<LectureDTO> createLecture(@RequestBody Lecture lecture) {
        LectureDTO created = lectureService.createLecture(lecture);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectureDTO> updateLecture(@PathVariable Long id,
                                                    @RequestBody Lecture updatedLecture) {
        LectureDTO lectureDTO = lectureService.updateLecture(id, updatedLecture);
        return ResponseEntity.ok(lectureDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
}