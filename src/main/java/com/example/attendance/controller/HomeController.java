package com.example.attendance.controller;

import com.example.attendance.config.AppConstants;
import com.example.attendance.model.Student;
import com.example.attendance.service.SessionService;
import com.example.attendance.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StudentService studentService;
    private final SessionService sessionService;

    @GetMapping("/")
    public String home(Model model) {
        return AppConstants.VIEW_HOME;
    }

    @PostMapping("/upload")
    public String uploadCsv(@RequestParam("file") MultipartFile file,
                            RedirectAttributes redirectAttributes) {
        List<Student> students = studentService.processUploadedCsv(file);
        sessionService.storeStudents(students);
        sessionService.clearSavedFile();
        redirectAttributes.addFlashAttribute("successMessage",
                "CSV uploaded successfully. " + students.size() + " students loaded.");
        log.info("CSV upload completed with {} students", students.size());
        return AppConstants.REDIRECT_ATTENDANCE;
    }
}
