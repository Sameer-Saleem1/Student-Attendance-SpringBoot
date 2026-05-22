package com.example.attendance.controller;

import com.example.attendance.config.AppConstants;
import com.example.attendance.dto.StudentDto;
import com.example.attendance.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final SessionService sessionService;

    @GetMapping
    public String attendanceSheet(Model model) {
        sessionService.requireStudentsInSession();
        List<StudentDto> students = sessionService.getStoredStudents().stream()
                .map(StudentDto::fromEntity)
                .toList();
        model.addAttribute("students", students);
        model.addAttribute("savedFileName", sessionService.getSavedFileName());
        return AppConstants.VIEW_ATTENDANCE_SHEET;
    }

    @GetMapping("/success")
    public String successPage(Model model) {
        sessionService.requireStudentsInSession();
        model.addAttribute("savedFileName", sessionService.getSavedFileName());
        model.addAttribute("studentCount", sessionService.getStoredStudents().size());
        return AppConstants.VIEW_SUCCESS;
    }
}
