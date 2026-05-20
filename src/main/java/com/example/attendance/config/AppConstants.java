package com.example.attendance.config;

public final class AppConstants {

    public static final String CSV_EXTENSION = ".csv";
    public static final String ATTENDANCE_FILE_PREFIX = "attendance_";
    public static final String ATTENDANCE_FILE_TIMESTAMP_PATTERN = "yyyy_MM_dd_HH_mm";
    public static final String CONTENT_TYPE_CSV = "text/csv";

    public static final String VIEW_HOME = "index";
    public static final String VIEW_ATTENDANCE_SHEET = "attendance-sheet";
    public static final String VIEW_SUCCESS = "success";

    public static final String REDIRECT_ATTENDANCE = "redirect:/attendance";
    public static final String REDIRECT_SUCCESS = "redirect:/attendance/success";

    private AppConstants() {
    }
}
