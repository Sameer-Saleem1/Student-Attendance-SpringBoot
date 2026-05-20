package com.example.attendance.service.impl;

import com.example.attendance.config.AppProperties;
import com.example.attendance.exception.ValidationException;
import com.example.attendance.model.Student;
import com.example.attendance.service.SessionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final HttpSession httpSession;
    private final AppProperties appProperties;

    @Override
    @SuppressWarnings("unchecked")
    public void storeStudents(List<Student> students) {
        httpSession.setAttribute(appProperties.getSession().getStudentsAttribute(), new ArrayList<>(students));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Student> getStoredStudents() {
        Object attribute = httpSession.getAttribute(appProperties.getSession().getStudentsAttribute());
        if (attribute instanceof List<?> list) {
            return list.stream()
                    .filter(Student.class::isInstance)
                    .map(Student.class::cast)
                    .toList();
        }
        return List.of();
    }

    @Override
    public void storeSavedFileName(String fileName) {
        httpSession.setAttribute(appProperties.getSession().getSavedFileAttribute(), fileName);
    }

    @Override
    public String getSavedFileName() {
        Object attribute = httpSession.getAttribute(appProperties.getSession().getSavedFileAttribute());
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public void clearSavedFile() {
        httpSession.removeAttribute(appProperties.getSession().getSavedFileAttribute());
    }

    @Override
    public void requireStudentsInSession() {
        if (getStoredStudents().isEmpty()) {
            throw new ValidationException("Session expired or no students loaded. Please upload a CSV file again.");
        }
    }
}
