package com.company.university.lecturer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    boolean existsByNameAndSurname(String name, String surname);

    boolean existsByEmail(String email);
}
