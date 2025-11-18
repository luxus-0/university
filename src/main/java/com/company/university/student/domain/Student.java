package com.company.university.student.domain;

import com.company.university.address.Address;
import com.company.university.lecture.domain.Lecture;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String name;
    private String surname;

    @Email
    private String email;

    @Past
    private LocalDate dateOfBirth;

    private String studentNumber;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Embedded
    private Address address;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "student_lecture",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    private Set<Lecture> lectures = new HashSet<>();

    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
        lecture.getStudents().add(this);
    }

    public void removeLecture(Lecture lecture) {
        lectures.remove(lecture);
        lecture.getStudents().remove(this);
    }
}