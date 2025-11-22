package com.company.university.lecture.util;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import com.company.university.lecture.dto.CreateLectureRequest;
import com.company.university.lecturer.domain.LecturerRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class LectureTestUtils {
    public static void verifyValidationFailureInteractions(
            LectureRepository lectureRepository,
            LecturerRepository lecturerRepository,
            CreateLectureRequest request) {

        verify(lecturerRepository, times(1)).findById(request.getLecturerId());

        verify(lectureRepository, never()).save(any(Lecture.class));
    }
}
