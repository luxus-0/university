package com.company.university.student.application;

public class StudentPageMoreThanZeroException extends RuntimeException {
    public StudentPageMoreThanZeroException(String message) {
        super(message);
    }
}
