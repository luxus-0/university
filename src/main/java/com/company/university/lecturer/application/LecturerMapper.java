package com.company.university.lecturer.application;

import com.company.university.lecturer.domain.Lecturer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LecturerMapper {
    LecturerDTO toDto(Lecturer lecturer);
}
