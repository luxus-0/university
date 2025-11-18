package com.company.university.student.application;

import com.company.university.lecture.domain.Lecture;
import com.company.university.student.domain.Student;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class StudentValidator {


    public void validateOnCreate(Student student) {
        if (student.getName() == null || student.getName().isBlank()) {
            throw new IllegalArgumentException("Student name cannot be blank");
        }
        if (student.getSurname() == null || student.getSurname().isBlank()) {
            throw new IllegalArgumentException("Student surname cannot be blank");
        }
        if (student.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Student date of birth is required");
        }
        validateAge(student.getDateOfBirth());
    }


    private void validateAge(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        int age = today.getYear() - dateOfBirth.getYear();
        if (dateOfBirth.plusYears(age).isAfter(today)) {
            age--;
        }
        if (age < 16) {
            throw new IllegalArgumentException("Student must be at least 16 years old");
        }
    }

    public void validateStudentAvailability(Student student, Lecture lecture) {

        if (student.getLectures().contains(lecture)) {
            throw new IllegalStateException("Student is already enrolled in this lecture");
        }

        for (Lecture existingLecture : student.getLectures()) {
            if (timesOverlap(existingLecture.getStartTime(), existingLecture.getEndTime(),
                    lecture.getStartTime(), lecture.getEndTime())) {
                throw new IllegalArgumentException(
                        "Student has a time conflict with lecture: " + existingLecture.getTitle()
                );
            }
        }
    }

    private boolean timesOverlap(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}