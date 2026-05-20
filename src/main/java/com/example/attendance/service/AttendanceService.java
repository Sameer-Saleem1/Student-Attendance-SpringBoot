package com.example.attendance.service;

import com.example.attendance.dto.AttendanceSaveResultDto;
import com.example.attendance.dto.AttendanceSubmissionDto;
import com.example.attendance.model.Student;

import java.nio.file.Path;
import java.util.List;

public interface AttendanceService {

    List<Student> applyAttendanceMarks(List<Student> sessionStudents, AttendanceSubmissionDto submission);

    AttendanceSaveResultDto saveAttendance(List<Student> students);

    Path resolveAttendanceFile(String fileName);
}
