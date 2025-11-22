package com.company.university.lecture.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    @Query("""
                SELECT l FROM Lecture l
                WHERE (:id IS NULL OR l.id <> :id)
                  AND l.endDateTime > :start
                  AND l.startDateTime < :end
                  AND (l.roomNumber = :roomNumber OR l.lecturer.id = :lecturerId)
            """)
    List<Lecture> findOverlapping(
            @Param("id") Long id,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("roomNumber") String roomNumber,
            @Param("lecturerId") Long lecturerId
    );

    @Query("""
        SELECT COUNT(l) > 0
        FROM Lecture l
        JOIN l.students s
        WHERE s.id = :studentId
          AND l.endDateTime > :newStart
          AND l.startDateTime < :newEnd
    """)
    boolean existsStudentScheduleConflict(
            @Param("studentId") Long studentId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd
    );

    @EntityGraph(attributePaths = "lecturer")
    List<Lecture> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = "lecturer")
    Page<Lecture> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
