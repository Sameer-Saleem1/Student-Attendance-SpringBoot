package com.example.attendance.controller;

import com.example.attendance.config.AppConstants;
import com.example.attendance.dto.AttendanceSaveResultDto;
import com.example.attendance.dto.AttendanceSubmissionDto;
import com.example.attendance.dto.StudentDto;
import com.example.attendance.model.Student;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
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

    @PostMapping("/save")
    public String saveAttendance(@ModelAttribute AttendanceSubmissionDto submission,
                                 RedirectAttributes redirectAttributes) {
        sessionService.requireStudentsInSession();
        List<Student> updatedStudents = attendanceService.applyAttendanceMarks(
                sessionService.getStoredStudents(), submission);
        sessionService.storeStudents(updatedStudents);

        AttendanceSaveResultDto result = attendanceService.saveAttendance(updatedStudents);
        sessionService.storeSavedFileName(result.getFileName());

        redirectAttributes.addFlashAttribute("successMessage",
                "Attendance saved successfully for " + result.getStudentCount() + " students.");
        redirectAttributes.addFlashAttribute("savedFileName", result.getFileName());
        log.info("Attendance saved to file: {}", result.getFileName());
        return AppConstants.REDIRECT_SUCCESS;
    }

    @GetMapping("/success")
    public String successPage(Model model) {
        sessionService.requireStudentsInSession();
        model.addAttribute("savedFileName", sessionService.getSavedFileName());
        model.addAttribute("studentCount", sessionService.getStoredStudents().size());
        return AppConstants.VIEW_SUCCESS;
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadAttendance(@PathVariable String fileName)
            throws MalformedURLException {
        Path filePath = attendanceService.resolveAttendanceFile(fileName);
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(AppConstants.CONTENT_TYPE_CSV))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}
