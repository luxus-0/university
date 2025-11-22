package com.company.university.student.application;

public class StudentScheduleConflictException extends RuntimeException{
    public StudentScheduleConflictException(String message) {
        super(message);
    }
}
