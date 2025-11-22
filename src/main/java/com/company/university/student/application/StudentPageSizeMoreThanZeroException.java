package com.company.university.student.application;

public class StudentPageSizeMoreThanZeroException extends RuntimeException {
    public StudentPageSizeMoreThanZeroException(String message) {
        super(message);
    }
}
