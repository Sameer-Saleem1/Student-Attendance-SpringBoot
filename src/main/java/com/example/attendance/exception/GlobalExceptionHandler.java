package com.example.attendance.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AttendanceException.class)
    public String handleAttendanceException(AttendanceException ex,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes,
                                            Model model) {
        log.warn("Attendance error on {}: {}", request.getRequestURI(), ex.getMessage());
        return resolveResponse(ex.getUserMessage(), request, redirectAttributes, model);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex,
                                      HttpServletRequest request,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        log.warn("Upload size exceeded: {}", ex.getMessage());
        return resolveResponse("Uploaded file exceeds the maximum allowed size (5MB).",
                request, redirectAttributes, model);
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         RedirectAttributes redirectAttributes,
                                         Model model) {
        log.error("Unexpected error on {}", request.getRequestURI(), ex);
        return resolveResponse("An unexpected error occurred. Please try again.",
                request, redirectAttributes, model);
    }

    private String resolveResponse(String message,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (isRedirectCandidate(request.getRequestURI())) {
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/";
        }
        model.addAttribute("errorMessage", message);
        return "index";
    }

    private boolean isRedirectCandidate(String uri) {
        return uri != null && (uri.contains("/upload") || uri.contains("/attendance/save"));
    }
}
