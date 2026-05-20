package com.example.attendance.exception;

import lombok.Getter;

@Getter
public class AttendanceException extends RuntimeException {

    private final String userMessage;

    public AttendanceException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }

    public AttendanceException(String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
    }
}
