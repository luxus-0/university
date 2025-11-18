package com.company.university.lecture.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {
    List<Lecture> findByRoomNumber(String roomNumber);

    List<Lecture> findByLecturerId(Long id);

    List<Lecture> findByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
