package com.company.university.lecturer.domain;

import com.company.university.common.vo.Address;
import com.company.university.lecture.domain.Lecture;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lecturers")
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;

    @Email
    private String email;

    @Embedded
    private Address address;

    @Past
    private LocalDate dateOfBirth;

    @OneToMany(mappedBy = "lecturer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lecture> lectures = new HashSet<>();

    public void addLecture(Lecture lecture) {
        lectures.add(lecture);
        lecture.setLecturer(this);
    }

    public void removeLecture(Lecture lecture) {
        lectures.remove(lecture);
        lecture.setLecturer(null);
    }
}