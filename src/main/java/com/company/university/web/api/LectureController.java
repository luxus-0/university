package com.company.university.web.api;

import com.company.university.lecture.application.LectureDTO;
import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<List<LectureDTO>> getLectures() {
        List<LectureDTO> lectures = lectureService.getLectures();
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureDTO> getLecture(@PathVariable Long id) {
        LectureDTO lecture = lectureService.getLecture(id);
        return ResponseEntity.ok(lecture);
    }

    @GetMapping("/date")
    public ResponseEntity<List<LectureDTO>> getLectures(@RequestParam("date") String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        List<LectureDTO> lectures = lectureService.getLectures(date);
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<LectureDTO>> getLecturesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return ResponseEntity.ok(
                lectureService.getLectures(page, size, sortBy, direction)
        );
    }

    @GetMapping
    public Page<LectureDTO> getLectures(
            @PageableDefault(size = 10, sort = "startDate") Pageable pageable,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return lectureService.getLectures(pageable, startDate, endDate);
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