package com.company.university.student.application;

import com.company.university.common.vo.Address;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class StudentDTO {
    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String email;
    @Past
    @NotNull
    private LocalDate dateOfBirth;
    @NotNull
    private String studentNumber;
    @NotNull
    private Address address;
}
