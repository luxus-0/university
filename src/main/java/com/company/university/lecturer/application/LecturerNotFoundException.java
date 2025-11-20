package com.company.university.lecturer.application;

public class LecturerNotFoundException extends RuntimeException {
    public LecturerNotFoundException(String message) {
        super(message);
    }
}
