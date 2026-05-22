package com.example.attendance.exception;

import com.example.attendance.controller.api.AttendanceApiController;
import com.example.attendance.dto.ApiErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice(basePackageClasses = AttendanceApiController.class)
public class RestExceptionHandler {

    @ExceptionHandler(AttendanceException.class)
    public ResponseEntity<ApiErrorResponseDto> handleAttendanceException(AttendanceException ex,
                                                                         HttpServletRequest request) {
        log.warn("API attendance error on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getUserMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMaxUploadSize(MaxUploadSizeExceededException ex,
                                                                    HttpServletRequest request) {
        log.warn("API upload size exceeded on {}: {}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.PAYLOAD_TOO_LARGE,
                "Uploaded file exceeds the maximum allowed size (5MB).",
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleGenericException(Exception ex,
                                                                      HttpServletRequest request) {
        log.error("Unexpected API error on {}", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again.",
                request.getRequestURI());
    }

    private ResponseEntity<ApiErrorResponseDto> buildError(HttpStatus status, String message, String path) {
        return ResponseEntity.status(status).body(ApiErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build());
    }
}