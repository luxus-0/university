package com.company.university.lecturer.service;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.application.LecturerDTO;
import com.company.university.lecturer.application.LecturerMapper;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LecturerService {
    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;

    public LecturerDTO createLecturer(Lecturer lecturer) {
        if (lecturer.getId() != null) {
            throw new IllegalArgumentException("New lecturer cannot have an ID");
        }

        Lecturer lecturerSaved = lecturerRepository.save(lecturer);
        return lecturerMapper.toDto(lecturerSaved);
    }

    public LecturerDTO getLecturer(Long id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));
        return lecturerMapper.toDto(lecturer);
    }

    public Set<LecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll()
                .stream()
                .map(lecturerMapper::toDto)
                .collect(Collectors.toSet());
    }

    public LecturerDTO updateLecturer(Long id, Lecturer updatedLecturer) {
        Lecturer existing = lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));

        existing.setName(updatedLecturer.getName());
        existing.setSurname(updatedLecturer.getSurname());
        existing.setDateOfBirth(updatedLecturer.getDateOfBirth());
        existing.setEmail(updatedLecturer.getEmail());

        Lecturer saved = lecturerRepository.save(existing);
        return lecturerMapper.toDto(saved);
    }

    public void deleteLecturer(Long id) {
        Lecturer existing = lecturerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + id));
        lecturerRepository.delete(existing);
    }

    public void addLectureToLecturer(Long lecturerId, Lecture lecture) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + lecturerId));
        lecturer.addLecture(lecture);
    }

    public void removeLectureFromLecturer(Long lecturerId, Lecture lecture) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + lecturerId));
        lecturer.removeLecture(lecture);
    }
}