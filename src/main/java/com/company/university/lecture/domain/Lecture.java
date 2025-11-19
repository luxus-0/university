package com.company.university.lecture.domain;

import com.company.university.lecturer.domain.Lecturer;
import com.company.university.student.domain.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lectures")
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String roomNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToMany(mappedBy = "lectures")
    private Set<Student> students = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    public void addStudent(Student student) {
        students.add(student);
        student.getLectures().add(this);
    }

    public void removeStudent(Student student) {
        students.remove(student);
        student.getLectures().remove(this);
    }
}