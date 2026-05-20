package com.example.attendance.utility;

import com.example.attendance.config.AppConstants;
import com.example.attendance.config.AppProperties;
import com.example.attendance.exception.FileStorageException;
import com.example.attendance.model.Student;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsvWriterUtil {

    private final AppProperties appProperties;

    public Path writeAttendanceCsv(List<Student> students) {
        String fileName = generateFileName();
        Path outputPath = Path.of(appProperties.getAttendance().getRecordsDirectory(), fileName);

        try {
            Files.createDirectories(outputPath.getParent());
            try (Writer writer = Files.newBufferedWriter(outputPath);
                 CSVWriter csvWriter = new CSVWriter(writer)) {

                csvWriter.writeNext(buildHeader());
                students.forEach(student -> csvWriter.writeNext(mapStudentToRow(student)));
            }
            log.info("Attendance CSV saved at: {}", outputPath.toAbsolutePath());
            return outputPath;
        } catch (IOException e) {
            log.error("Failed to write attendance CSV", e);
            throw new FileStorageException("Failed to save attendance record. Please try again.", e);
        }
    }

    private String[] buildHeader() {
        return new String[]{
                "cmsId",
                "name",
                "fatherName",
                appProperties.getCsv().getAttendanceHeader()
        };
    }

    private String[] mapStudentToRow(Student student) {
        return new String[]{
                student.getCmsId(),
                student.getName(),
                student.getFatherName(),
                formatAttendance(student)
        };
    }

    private String formatAttendance(Student student) {
        return student.getAttendanceStatus() == null
                ? "Absent"
                : capitalize(student.getAttendanceStatus().name());
    }

    private String capitalize(String value) {
        return value.charAt(0) + value.substring(1).toLowerCase();
    }

    public String generateFileName() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(AppConstants.ATTENDANCE_FILE_TIMESTAMP_PATTERN));
        return AppConstants.ATTENDANCE_FILE_PREFIX + timestamp + AppConstants.CSV_EXTENSION;
    }
}
