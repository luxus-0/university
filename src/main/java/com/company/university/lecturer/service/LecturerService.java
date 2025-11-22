package com.company.university.lecturer.service;

import com.company.university.lecture.domain.Lecture;
import com.company.university.lecturer.application.LecturerMapper;
import com.company.university.lecturer.application.LecturerNotFoundException;
import com.company.university.lecturer.domain.Lecturer;
import com.company.university.lecturer.domain.LecturerRepository;
import com.company.university.lecturer.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.company.university.lecturer.application.LecturerMapper.*;

@Service
@RequiredArgsConstructor
@Transactional
public class LecturerService {
    private final LecturerRepository lecturerRepository;

    public CreateLecturerResponse createLecturer(CreateLecturerRequest request) {
        if (request.getId() != null) {
            throw new LecturerNotFoundException("ID must not be provided when creating lecturer");
        }
        Lecturer lecturer = toLecturer(request);
        Lecturer lecturerSaved = lecturerRepository.save(lecturer);

        return createLecturerResponse(lecturerSaved);
    }

    public FindLecturerResponse getLecturer(Long id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new LecturerNotFoundException("Lecturer not found with id: " + id));

        return findLecturerResponse(lecturer);
    }

    public List<FindLecturerResponse> getLecturers() {
        return lecturerRepository.findAll()
                .stream()
                .map(LecturerMapper::findLecturerResponse)
                .toList();
    }

    public Page<FindLecturerResponse> getLecturers(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return lecturerRepository.findAll(pageable)
                .map(LecturerMapper::findLecturerResponse);
    }

    public UpdateLecturerResponse updateLecturer(Long id, UpdateLecturerRequest updatedLecturer) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new LecturerNotFoundException("Lecturer not found with id: " + id));

        if (updatedLecturer.getName() != null) {
            lecturer.setName(updatedLecturer.getName());
        }
        if (updatedLecturer.getSurname() != null) {
            lecturer.setSurname(updatedLecturer.getSurname());
        }
        if (updatedLecturer.getEmail() != null) {
            lecturer.setEmail(updatedLecturer.getEmail());
        }
        if (updatedLecturer.getDateOfBirth() != null) {
            lecturer.setDateOfBirth(updatedLecturer.getDateOfBirth());
        }

        Lecturer lecturerSaved = lecturerRepository.save(lecturer);

        return updateLecturerResponse(lecturerSaved);
    }

    public void deleteLecturer(Long id) {
        lecturerRepository.deleteById(id);
    }

    public void addLectureToLecturer(Long lecturerId, Lecture lecture) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + lecturerId));
        lecturer.addLecture(lecture);
        lecturerRepository.save(lecturer);
    }

    public void removeLectureFromLecturer(Long lecturerId, Lecture lecture) {
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new IllegalArgumentException("Lecturer not found with id: " + lecturerId));
        lecturer.removeLecture(lecture);
        lecturerRepository.save(lecturer);
    }
}