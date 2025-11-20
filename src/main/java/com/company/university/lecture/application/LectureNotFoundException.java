package com.company.university.lecture.application;

public class LectureNotFoundException extends RuntimeException {
    public LectureNotFoundException(String message) {
        super(message);
    }
}
