package com.example.attendance.dto;

import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.model.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDto {

    private String cmsId;
    private String name;
    private String fatherName;
    private AttendanceStatus attendanceStatus;

    public static StudentDto fromEntity(Student student) {
        return StudentDto.builder()
                .cmsId(student.getCmsId())
                .name(student.getName())
                .fatherName(student.getFatherName())
                .attendanceStatus(student.getAttendanceStatus())
                .build();
    }

    public Student toEntity() {
        return Student.builder()
                .cmsId(cmsId)
                .name(name)
                .fatherName(fatherName)
                .attendanceStatus(attendanceStatus != null ? attendanceStatus : AttendanceStatus.ABSENT)
                .build();
    }
}
