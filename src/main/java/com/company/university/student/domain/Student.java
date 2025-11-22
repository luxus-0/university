package com.company.university.student.domain;

import com.company.university.lecture.domain.Lecture;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
@EqualsAndHashCode(exclude = "lectures")
@EntityListeners(AuditingEntityListener.class)
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    @Email
    @Column(unique = true)
    private String email;

    @Past
    private LocalDate dateOfBirth;

    @Column(name = "student_number", unique = true, nullable = false) // Klucz biznesowy
    private String studentNumber;

    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "student_lecture",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "lecture_id")
    )
    private Set<Lecture> lectures = new HashSet<>();

    public void addLecture(Lecture lecture) {
        if (lecture != null && this.lectures.add(lecture)) {
            lecture.getStudents().add(this);
        }
    }

    public void removeLecture(Lecture lecture) {
        if (lecture != null && this.lectures.remove(lecture)) {
            lecture.getStudents().remove(this);
        }
    }
}