package com.company.university.student.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecture.domain.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentValidator {
    private final LectureRepository lectureRepository;

    public void validatePaginationAndSorting(int page, int size, String sortBy, String direction) {
        if (page < 0) throw new StudentPageMoreThanZeroException("Page index must not be negative");
        if (size <= 0) throw new StudentPageSizeMoreThanZeroException("Page size must be greater than zero");

        Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name", "surname");
        Set<String> ALLOWED_SORT_DIRECTIONS = Set.of("asc", "desc");

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IncorrectFieldSortedByException("Cannot sort by incorrect field: " + sortBy);
        }

        if (!ALLOWED_SORT_DIRECTIONS.contains(direction.toLowerCase())) {
            throw new IncorrectSortDirectionException("Invalid sort direction: " + direction);
        }
    }

    public void validateScheduleConflict(Long studentId, Lecture newLecture) {
        boolean hasConflict = lectureRepository.existsStudentScheduleConflict(
                studentId,
                newLecture.getStartDateTime(),
                newLecture.getEndDateTime()
        );

        if (hasConflict) {
            throw new StudentScheduleConflictException("Student has a schedule conflict!");
        }
    }
}
