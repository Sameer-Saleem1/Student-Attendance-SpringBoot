package com.example.attendance.exception;

public class ValidationException extends AttendanceException {

    public ValidationException(String userMessage) {
        super(userMessage);
    }
}
