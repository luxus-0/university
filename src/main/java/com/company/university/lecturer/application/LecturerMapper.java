package com.company.university.lecturer.application;

import com.company.university.lecturer.domain.Lecturer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LecturerMapper {
    @Mapping(target = "id", source = "id")
    LecturerDTO toDto(Lecturer lecturer);
}
