package com.example.attendance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    private String cmsId;
    private String name;
    private String fatherName;

    @Builder.Default
    private AttendanceStatus attendanceStatus = AttendanceStatus.ABSENT;
}
