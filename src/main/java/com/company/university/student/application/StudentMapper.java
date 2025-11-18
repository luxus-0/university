package com.company.university.student.application;

import com.company.university.student.domain.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    StudentDTO toDto(Student student);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lectures", ignore = true)
    void updateStudent(@MappingTarget Student target, Student source);
}