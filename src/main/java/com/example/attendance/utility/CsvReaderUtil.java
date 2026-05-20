package com.example.attendance.utility;

import com.example.attendance.config.AppProperties;
import com.example.attendance.exception.CsvProcessingException;
import com.example.attendance.exception.ValidationException;
import com.example.attendance.model.AttendanceStatus;
import com.example.attendance.model.Student;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
@Component
@RequiredArgsConstructor
public class CsvReaderUtil {

    private final AppProperties appProperties;

    public List<Student> parseStudents(MultipartFile file) {
        List<String[]> allRows = readAllRows(file);
        validateHeader(allRows.getFirst());
        List<Student> students = parseDataRows(allRows.subList(1, allRows.size()));
        validateStudents(students);
        return students;
    }

    private List<String[]> readAllRows(MultipartFile file) {
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            List<String[]> rows = reader.readAll();
            if (rows.isEmpty()) {
                throw new ValidationException("The uploaded CSV file is empty.");
            }
            return rows;
        } catch (IOException | CsvException e) {
            log.error("Failed to read CSV file", e);
            throw new CsvProcessingException("Unable to read the CSV file. Please check the file format.", e);
        }
    }

    private void validateHeader(String[] headerRow) {
        String[] expected = appProperties.getCsv().getExpectedHeaders().split(",");
        if (headerRow.length < expected.length) {
            throw new CsvProcessingException(
                    "Invalid CSV format. Expected columns: " + appProperties.getCsv().getExpectedHeaders());
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].trim().equalsIgnoreCase(headerRow[i].trim())) {
                throw new CsvProcessingException(
                        "Invalid CSV header. Expected: " + appProperties.getCsv().getExpectedHeaders()
                                + " but found: " + String.join(",", headerRow));
            }
        }
    }

    private List<Student> parseDataRows(List<String[]> dataRows) {
        List<Student> students = new ArrayList<>();
        Set<String> seenCmsIds = new HashSet<>();
        int rowNumber = 2;

        for (String[] row : dataRows) {
            if (isBlankRow(row)) {
                rowNumber++;
                continue;
            }
            try {
                Student student = mapRowToStudent(row, rowNumber);
                if (!seenCmsIds.add(student.getCmsId())) {
                    throw new ValidationException("Duplicate CMS ID found: " + student.getCmsId());
                }
                students.add(student);
            } catch (ValidationException ex) {
                throw ex;
            } catch (Exception ex) {
                log.warn("Skipping malformed row {}: {}", rowNumber, Arrays.toString(row));
            }
            rowNumber++;
        }
        return students;
    }

    private Student mapRowToStudent(String[] row, int rowNumber) {
        if (row.length < appProperties.getCsv().getMaxColumns()) {
            throw new IllegalArgumentException("Insufficient columns at row " + rowNumber);
        }
        String cmsId = row[0].trim();
        String name = row[1].trim();
        String fatherName = row[2].trim();

        if (cmsId.isEmpty() || name.isEmpty() || fatherName.isEmpty()) {
            throw new IllegalArgumentException("Empty required field at row " + rowNumber);
        }

        return Student.builder()
                .cmsId(cmsId)
                .name(name)
                .fatherName(fatherName)
                .attendanceStatus(AttendanceStatus.ABSENT)
                .build();
    }

    private void validateStudents(List<Student> students) {
        if (students.isEmpty()) {
            throw new ValidationException("No valid student records found in the CSV file.");
        }
    }

    private boolean isBlankRow(String[] row) {
        return row == null || Arrays.stream(row).allMatch(cell -> cell == null || cell.isBlank());
    }
}
