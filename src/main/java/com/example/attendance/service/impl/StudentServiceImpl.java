package com.example.attendance.service.impl;

import com.example.attendance.model.Student;
import com.example.attendance.service.StudentService;
import com.example.attendance.utility.CsvReaderUtil;
import com.example.attendance.utility.FileValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final FileValidationUtil fileValidationUtil;
    private final CsvReaderUtil csvReaderUtil;

    @Override
    public List<Student> processUploadedCsv(MultipartFile file) {
        fileValidationUtil.validateUpload(file);
        List<Student> students = csvReaderUtil.parseStudents(file);
        log.info("Successfully parsed {} students from uploaded CSV", students.size());
        return students;
    }
}
