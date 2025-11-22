package com.company.university.student.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("""
       SELECT s FROM Student s
       LEFT JOIN FETCH s.lectures
       WHERE s.id = :id
       """)
    Optional<Student> findByIdWithLectures(@Param("id") Long id);

    @Query("""
       SELECT s FROM Student s
       LEFT JOIN FETCH s.lectures
       """)
    List<Student> findAllWithLectures();

    @Query(
            value = """
                SELECT DISTINCT s FROM Student s
                LEFT JOIN FETCH s.lectures
                """,
            countQuery = """
                SELECT COUNT(s) FROM Student s
                """
    )
    Page<Student> findAllWithLectures(Pageable pageable);
}
