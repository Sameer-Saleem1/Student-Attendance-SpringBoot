package com.example.attendance.service;

import com.example.attendance.model.Student;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    List<Student> processUploadedCsv(MultipartFile file);
}
