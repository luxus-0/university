package com.company.university.lecture.application;

import com.company.university.lecture.domain.Lecture;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LectureMapper {
    LectureDTO toDto(Lecture lecture);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "lecturer", ignore = true)
    void updateLecture(@MappingTarget Lecture target, Lecture source);
}