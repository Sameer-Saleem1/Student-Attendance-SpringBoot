package com.example.attendance.service;

import com.example.attendance.model.Student;

import java.util.List;

public interface SessionService {

    void storeStudents(List<Student> students);

    List<Student> getStoredStudents();

    void storeSavedFileName(String fileName);

    String getSavedFileName();

    void clearSavedFile();

    void requireStudentsInSession();
}
