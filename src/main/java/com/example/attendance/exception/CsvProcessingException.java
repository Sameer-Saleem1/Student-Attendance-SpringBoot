package com.example.attendance.exception;

public class CsvProcessingException extends AttendanceException {

    public CsvProcessingException(String userMessage) {
        super(userMessage);
    }

    public CsvProcessingException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
