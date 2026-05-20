package com.example.attendance.service.impl;

import com.example.attendance.config.AppProperties;
import com.example.attendance.dto.AttendanceSaveResultDto;
import com.example.attendance.dto.AttendanceSubmissionDto;
import com.example.attendance.exception.FileStorageException;
import com.example.attendance.exception.ValidationException;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.model.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.utility.CsvWriterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final CsvWriterUtil csvWriterUtil;
    private final AppProperties appProperties;

    @Override
    public List<Student> applyAttendanceMarks(List<Student> sessionStudents,
                                              AttendanceSubmissionDto submission) {
        if (sessionStudents == null || sessionStudents.isEmpty()) {
            throw new ValidationException("No students in session. Please upload a CSV file first.");
        }
        if (submission.getStudents() == null || submission.getStudents().isEmpty()) {
            throw new ValidationException("No attendance data submitted.");
        }

        Map<String, AttendanceStatus> attendanceByCmsId = submission.getStudents().stream()
                .collect(Collectors.toMap(
                        AttendanceSubmissionDto.StudentAttendanceEntry::getCmsId,
                        entry -> entry.getAttendanceStatus() != null
                                ? entry.getAttendanceStatus()
                                : AttendanceStatus.ABSENT,
                        (existing, replacement) -> replacement
                ));

        return sessionStudents.stream()
                .map(student -> mergeAttendance(student, attendanceByCmsId))
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceSaveResultDto saveAttendance(List<Student> students) {
        if (students == null || students.isEmpty()) {
            throw new ValidationException("Cannot save attendance for an empty student list.");
        }
        Path savedPath = csvWriterUtil.writeAttendanceCsv(students);
        log.info("Attendance saved for {} students in file {}", students.size(), savedPath.getFileName());

        return AttendanceSaveResultDto.builder()
                .fileName(savedPath.getFileName().toString())
                .filePath(savedPath.toAbsolutePath().toString())
                .studentCount(students.size())
                .build();
    }

    @Override
    public Path resolveAttendanceFile(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("..")) {
            throw new ValidationException("Invalid file name.");
        }
        Path filePath = Path.of(appProperties.getAttendance().getRecordsDirectory(), fileName);
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new FileStorageException("Attendance file not found.");
        }
        return filePath;
    }

    private Student mergeAttendance(Student student, Map<String, AttendanceStatus> attendanceByCmsId) {
        AttendanceStatus status = attendanceByCmsId.getOrDefault(student.getCmsId(), AttendanceStatus.ABSENT);
        student.setAttendanceStatus(status);
        return student;
    }
}
