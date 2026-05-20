package com.example.attendance.exception;

public class FileStorageException extends AttendanceException {

    public FileStorageException(String userMessage) {
        super(userMessage);
    }

    public FileStorageException(String userMessage, Throwable cause) {
        super(userMessage, cause);
    }
}
