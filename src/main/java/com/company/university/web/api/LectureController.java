package com.company.university.web.api;

import com.company.university.lecture.dto.*;
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
    public ResponseEntity<List<FindLectureResponse>> getLectures() {
        List<FindLectureResponse> lectures = lectureService.getLectures();
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FindLectureResponse> getLecture(@PathVariable Long id) {
        FindLectureResponse lecture = lectureService.getLecture(id);
        return ResponseEntity.ok(lecture);
    }

    @GetMapping("/date")
    public ResponseEntity<List<FindLectureResponse>> getLectures(@RequestParam("date") String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        List<FindLectureResponse> lectures = lectureService.getLectures(date);
        return ResponseEntity.ok(lectures);
    }

    @GetMapping("/page")
    public ResponseEntity<Page<FindLectureResponse>> getLecturesPage(
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
    public Page<FindLectureResponse> getLectures(
            @PageableDefault(sort = "startDate") Pageable pageable,

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
    public ResponseEntity<CreateLectureResponse> createLecture(@RequestBody CreateLectureRequest request) {
        CreateLectureResponse created = lectureService.createLecture(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateLectureResponse> updateLecture(@PathVariable Long id,
                                                               @RequestBody UpdateLectureRequest updatedLecture) {
        UpdateLectureResponse lectureDTO = lectureService.updateLecture(id, updatedLecture);
        return ResponseEntity.ok(lectureDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable Long id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
}