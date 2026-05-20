package com.example.attendance.dto;

import com.example.attendance.model.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSubmissionDto {

    private List<StudentAttendanceEntry> students = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentAttendanceEntry {
        private String cmsId;
        private AttendanceStatus attendanceStatus;
    }
}
