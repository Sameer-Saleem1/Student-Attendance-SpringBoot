package com.example.attendance.model;

public enum AttendanceStatus {
    PRESENT,
    ABSENT;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static AttendanceStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ABSENT;
        }
        return AttendanceStatus.valueOf(value.trim().toUpperCase());
    }
}
